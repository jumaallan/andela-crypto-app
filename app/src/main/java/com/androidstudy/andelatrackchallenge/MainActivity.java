package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidstudy.andelatrackchallenge.adapter.CardAdapter;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.User;
import com.androidstudy.andelatrackchallenge.picker.currency.Countries;
import com.androidstudy.andelatrackchallenge.picker.currency.CurrencyPickerFragment;
import com.androidstudy.andelatrackchallenge.picker.currency.CurrencyPickerListener;
import com.androidstudy.andelatrackchallenge.utils.OnItemClickListener;
import com.androidstudy.andelatrackchallenge.utils.Settings;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, CurrencyPickerListener, OnItemClickListener<Country> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;
    @BindView(R.id.view_empty)
    View emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private CurrencyPickerFragment pickerFragment;
    private CardAdapter adapter;
    private Box<User> userBox;
    private Box<Country> countryBox;
    private User user;
    private Settings settings;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        pickerFragment = CurrencyPickerFragment.newInstance(Countries.countries);
        settings = new Settings(this.getApplicationContext());

        BoxStore boxStore = ((AndelaTrackChallenge) getApplicationContext()).getBoxStore();
        userBox = boxStore.boxFor(User.class);
        countryBox = boxStore.boxFor(Country.class);
        user = userBox.query().build().findFirst();

        init();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        fab.setOnClickListener(v -> pickerFragment.show(MainActivity.this.getSupportFragmentManager(), "currency-picker"));
    }

    private void init() {
        // Load User's Profile Image
        Glide.with(getApplicationContext())
                .load(user.image_url)
                .into(mProfileImage);

        //Toast welcome message
        Toast.makeText(this, "Welcome " + user.name, Toast.LENGTH_SHORT).show();

        adapter = new CardAdapter(this);
        adapter.setEmptyView(emptyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setCountries(countryBox.getAll());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean facebook = settings.isFacebook();

        if (id == R.id.action_signout) {
            if (facebook) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        status -> {

                            //Clear Shared Pref File
                            settings.setLoggedInSharedPref(false);

                            //Clear Local DB
                            userBox.removeAll();

                            //Redirect User to Login Page
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
            } else {
                new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> {

                    LoginManager.getInstance().logOut();

                    //Clear Shared Pref File
                    settings.setLoggedInSharedPref(false);

                    //Clear Local DB
                    userBox.removeAll();

                    //Redirect User to Login Page
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();

                }).executeAsync();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onPicked(Country country, int position) {
        countryBox.put(country);
        adapter.add(country);
        Toast.makeText(this, "Added " + country.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Country item, int position) {
        // Open details and calculator
    }
}
