package com.androidstudy.andelatrackchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.network.ApiClient;
import com.androidstudy.andelatrackchallenge.utils.SimpleTextWatcher;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import timber.log.Timber;

public class CalculatorActivity extends ThemableActivity {
    public static final String COUNTRY = "COUNTRY";
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
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
    private Box<Country> countryBox;
    private Country country;
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
            ApiClient.loadRate(country)
                    .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                    .subscribe(newCountry -> {
                        this.country = newCountry;
                        countryBox.put(newCountry);
                        finishSetup();
                    }, Timber::e);
        }
    }

    private void finishSetup() {
        btcEdit.setText("1");
        ethEdit.setText("1");

        calculateBTC(true);
        calculateETH(true);

        setEnabled(Arrays.asList(btcEdit, ethEdit, currBTCEdit, currETHEdit), true);

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

    private void setEnabled(List<EditText> editTexts, final boolean enabled) {
        ButterKnife.apply(Arrays.asList(btcEdit, ethEdit, currBTCEdit, currETHEdit),
                (ButterKnife.Action<EditText>) (view, index) -> view.setEnabled(enabled));
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
