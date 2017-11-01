package com.androidstudy.andelatrackchallenge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.Exchange;
import com.androidstudy.andelatrackchallenge.network.Api;
import com.androidstudy.andelatrackchallenge.network.ApiClient;
import com.androidstudy.andelatrackchallenge.utils.SimpleTextWatcher;
import com.uber.autodispose.AutoDispose;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CalculatorActivity extends RxActivity {
    public static final String COUNTRY = "COUNTRY";

    private Box<Country> countryBox;
    private Country country;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_flag)
    ImageView imageFlag;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_curr_btc)
    TextView currBTCText;
    @BindView(R.id.text_curr_eth)
    TextView currETHText;
    @BindView(R.id.edit_curr_btc)
    EditText currBTCEdit;
    @BindView(R.id.edit_curr_eth)
    EditText currETHEdit;
    @BindView(R.id.edit_bitcoin)
    EditText btcEdit;
    @BindView(R.id.edit_eth)
    EditText ethEdit;

    private float btc = -1f;
    private float currBTC = -1f;
    private float eth = -1f;
    private float currETH = -1f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        countryBox = ((AndelaTrackChallenge) getApplicationContext()).getBoxStore().boxFor(Country.class);
        country = getIntent().getParcelableExtra(COUNTRY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            textTitle.setText(country.name);
            imageFlag.setImageResource(country.flagRes);
        }

        currBTCText.setText(country.code);
        currETHText.setText(country.code);

        if (country.eth > 0f && country.btc > 0f) {
            finishSetup();
        } else {
            loadRate();
        }
    }

    private void finishSetup() {
        btcEdit.setText("1");
        ethEdit.setText("1");

        calculateBTC(true);
        calculateETH(true);

        btcEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                calculateBTC(true);
            }
        });
        ethEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                calculateETH(true);
            }
        });
        /*currBTCEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBTC(false);
            }
        });
        currETHEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                calculateETH(false);
            }
        });*/
    }

    private void calculateBTC(boolean forward) {
        if (forward) {
            float value = getFloat(btcEdit);
            btc = value > 0f ? value / country.btc : 0f;
            currBTCEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), btc));
        } else {
            float value = getFloat(currBTCEdit);
            currBTC = value * country.btc;
            btcEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), currBTC));
        }
    }

    private void calculateETH(boolean forward) {
        if (forward) {
            float value = getFloat(ethEdit);
            eth = value > 0f ? value / country.eth : 0f;
            currETHEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), eth));
        } else {
            float value = getFloat(currETHEdit);
            currETH = value * country.eth;
            ethEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), currETH));
        }
    }

    private void loadRate() {
        long fiveMinsBefore = System.currentTimeMillis() - (10 * 60 * 1000);
        if (country.refreshedAt > fiveMinsBefore)
            return;

        ApiClient.getApi().getPrice(country.code, "BTC,ETH")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .to(AutoDispose.with(this).forSingle())
                .subscribe(response -> {
                    HttpUrl url = response.raw().request().url();
                    String from = url.queryParameter(Api.FROM_SYMBOL);

                    if (TextUtils.isEmpty(from)) {
                        return;
                    }

                    Exchange exchange = response.body();
                    if (exchange == null) {
                        return;
                    }

                    if (ObjectsCompat.equals(country.code, from)) {
                        int btcStatus = Country.SAME;
                        if (country.btc != -1) {
                            if (exchange.bitcoin > country.btc) {
                                btcStatus = Country.RISE;
                            } else if (exchange.bitcoin < country.btc) {
                                btcStatus = Country.DROP;
                            }
                        }

                        int ethStatus = Country.SAME;
                        if (country.eth != -1) {
                            if (exchange.bitcoin > country.btc) {
                                ethStatus = Country.RISE;
                            } else if (exchange.bitcoin < country.btc) {
                                ethStatus = Country.DROP;
                            }
                        }
                        country.btcStatus = btcStatus;
                        country.ethStatus = ethStatus;

                        country.eth = exchange.ethereum;
                        country.btc = exchange.bitcoin;
                        country.refreshedAt = System.currentTimeMillis();
                        countryBox.put(country);
                        finishSetup();
                    }
                }, Timber::e);
    }

    private float getFloat(EditText editText) {
        float value;
        try {
            value = Float.parseFloat(editText.getText().toString());
        } catch (Exception e) {
            Timber.e(e);
            value = 0f;
        }

        return value;
    }
}

