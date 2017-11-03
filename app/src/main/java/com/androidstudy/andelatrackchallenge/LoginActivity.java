package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.androidstudy.andelatrackchallenge.models.User;
import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import timber.log.Timber;

public class LoginActivity extends ThemableActivity implements GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.mFacebookLogin)
    LoginButton mFacebookLogin;
    @BindView(R.id.mGoogleLogin)
    SignInButton mGoogleLogin;

    Box<User> userBox;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 121;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        userBox = ((AndelaTrackChallenge) getApplicationContext()).getBoxStore().boxFor(User.class);
        callbackManager = CallbackManager.Factory.create();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In Api and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Load User Data
                loadUserData(loginResult);
            }

            @Override
            public void onCancel() {
                //Handle Cancel Here
            }

            @Override
            public void onError(FacebookException e) {
                //Handle Error Here
            }
        });

        mGoogleLogin.setOnClickListener(v -> googleSignIn());
    }

    private void loadUserData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                (object, response) -> {

                    Timber.i(response.toString());

                    // Application code
                    try {
                        String name = response.getJSONObject().getString("name");
                        String image_url = "http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large";

                        /*
                         * Save to ObjectBox ORM
                         * Set the Logged in status to true
                         * Navigate user to Main Activity
                         */
                        User user = new User();
                        user.name = name;
                        user.imageUrl = image_url;
                        userBox.put(user);

                        Timber.d("Total Users : %d", userBox.count());

                        Settings.setLoggedInSharedPref(true);
                        Settings.setIsFacebook(true);

                        Intent login = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(login);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Timber.d("handleSignInResult:" + result.isSuccess() + " " + result.getStatus());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            assert acct != null;

            String name = acct.getDisplayName();
            String imageUrl = String.valueOf(acct.getPhotoUrl());

            /*
             * Save to ObjectBox ORM
             * Set the Logged in status to true
             * Navigate user to Main Activity
             */
            User user = new User();
            user.name = name;
            user.imageUrl = imageUrl;
            userBox.put(user);

            Timber.d("Total Users : %d", userBox.count());

            Settings.setLoggedInSharedPref(true);
            Settings.setIsFacebook(false);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

