package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ObjectsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidstudy.andelatrackchallenge.adapter.CardsAdapter;
import com.androidstudy.andelatrackchallenge.cards.OnCardActionListener;
import com.androidstudy.andelatrackchallenge.cards.OnItemLongClickListener;
import com.androidstudy.andelatrackchallenge.cards.ProfileDialog;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.Country_;
import com.androidstudy.andelatrackchallenge.models.Exchange;
import com.androidstudy.andelatrackchallenge.models.User;
import com.androidstudy.andelatrackchallenge.network.Api;
import com.androidstudy.andelatrackchallenge.network.ApiClient;
import com.androidstudy.andelatrackchallenge.picker.currency.Countries;
import com.androidstudy.andelatrackchallenge.picker.currency.CurrencyPickerFragment;
import com.androidstudy.andelatrackchallenge.picker.currency.CurrencyPickerListener;
import com.androidstudy.andelatrackchallenge.settings.SettingsActivity;
import com.androidstudy.andelatrackchallenge.cards.CardActionsDialog;
import com.androidstudy.andelatrackchallenge.cards.OnItemClickListener;
import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import timber.log.Timber;

public class MainActivity extends ThemableActivity implements CurrencyPickerListener,
        OnItemClickListener<Country>, OnCardActionListener, GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.layout_empty)
    View emptyView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private GoogleApiClient mGoogleApiClient;
    private boolean isShowColoredCards = Settings.isShowColoredCards();

    private CurrencyPickerFragment pickerFragment;
    private ProfileDialog profileDialog;
    private CardsAdapter adapter;
    private Box<Country> countryBox;
    private Box<User> userBox;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        pickerFragment = CurrencyPickerFragment.newInstance(Countries.countries);
        profileDialog = ProfileDialog.newInstance(((dialog, which) -> logout()));

        BoxStore boxStore = ((AndelaTrackChallenge) getApplicationContext()).getBoxStore();
        userBox = boxStore.boxFor(User.class);
        countryBox = boxStore.boxFor(Country.class);
        user = userBox.query().build().findFirst();

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
        init();

        fab.setOnClickListener(v -> pickerFragment.show(MainActivity.this.getSupportFragmentManager(), "currency-picker"));
    }

    private void init() {
        //Toast welcome message
        Toast.makeText(
                this,
                (Settings.isFirstTimeLaunch() ? "Welcome, " : "Welcome back, ") + user.name,
                Toast.LENGTH_SHORT).show();

        adapter = new CardsAdapter();
        adapter.setOnItemClickListener(this);
        adapter.setOnCardActionListener(this);
        adapter.setEmptyView(emptyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        List<Country> all = countryBox.getAll();
        adapter.setCountries(all);
        loadRates(all.toArray(new Country[0]));

        swipeRefreshLayout.setOnRefreshListener(() ->
                loadRates(adapter.getCountries().toArray(new Country[0])));
    }

    private void loadRates(Country... countries) {
        for (Country country : countries) {
            ApiClient.loadRate(country)
                    .doOnSubscribe(d -> swipeRefreshLayout.setRefreshing(true))
                    .doAfterTerminate(() -> swipeRefreshLayout.setRefreshing(false))
                    .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                    .subscribe(newCountry -> {
                        countryBox.put(country);
                        adapter.replace(country);
                    }, Timber::e);
        }
    }

    private void logout() {
        boolean facebook = Settings.isFacebook();
        if (facebook) {
            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> {
                LoginManager.getInstance().logOut();
                //Clear Shared Pref File
                Settings.setLoggedInSharedPref(false);
                //Clear Local DB
                userBox.removeAll();
                //Redirect User to Login Page
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }).executeAsync();
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                //Clear Shared Pref File
                Settings.setLoggedInSharedPref(false);
                //Clear Local DB
                userBox.removeAll();
                //Redirect User to Login Page
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected boolean shouldRestart() {
        return super.shouldRestart() || (isShowColoredCards != Settings.isShowColoredCards());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        Glide.with(this)
                .asBitmap()
                .load(user.imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        profileItem.setIcon(new BitmapDrawable(getResources(), resource));
                    }
                });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            profileDialog.show(getSupportFragmentManager(), "profile");
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPicked(Country country, int position) {
        if (adapter.getCountries().contains(country)) {
            return;
        }

        Country existingCountry = countryBox.query().equal(Country_.code, country.code).build()
                .findFirst();
        if (existingCountry != null) {
            return;
        }

        countryBox.put(country);
        adapter.add(country);
        loadRates(country);
        Toast.makeText(this, "Added " + country.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Country item, int position) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        intent.putExtra(CalculatorActivity.COUNTRY, item);
        startActivity(intent);
    }

    @Override
    public void onToggleStar(Country country) {
        country.isFavorite = !country.isFavorite;
        countryBox.put(country);
        adapter.moveToPosition(country);
    }

    @Override
    public void onRemoved(Country country) {
        countryBox.remove(country);
        adapter.remove(country);
        Toast.makeText(this, "Removed " + country.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEdited(Country country) {
        // open editing
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e(connectionResult.getErrorMessage());
    }
}
