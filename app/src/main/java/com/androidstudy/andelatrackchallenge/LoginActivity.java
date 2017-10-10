package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.androidstudy.andelatrackchallenge.models.User;
import com.androidstudy.andelatrackchallenge.utils.Settings;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.mFacebookLogin)
    LoginButton mFacebookLogin;
    @BindView(R.id.mGoogleLogin)
    SignInButton mGoogleLogin;

    Settings settings;
    Box<User> userBox;

    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        settings = new Settings(this.getApplicationContext());
        userBox = ((AndelaTrackChallenge) getApplicationContext()).getBoxStore().boxFor(User.class);
        callbackManager = CallbackManager.Factory.create();

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
    }

    private void loadUserData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        Log.i("Response ", response.toString());

                        // Application code
                        try {
                            String name = response.getJSONObject().getString("name");
                            String image_url = "http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() +"/picture?type=large";

                            /**
                             * Save to ObjectBox ORM
                             * Set the Logged in status to true
                             * Navigate user to Main Activity
                             */
                            User user = new User();
                            user.name = name;
                            user.image_url = image_url;
                            userBox.put(user);

                            Log.d("Users", "Total Users : " + userBox.count());

                            settings.setLoggedInSharedPref(true);

                            Intent login = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(login);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

