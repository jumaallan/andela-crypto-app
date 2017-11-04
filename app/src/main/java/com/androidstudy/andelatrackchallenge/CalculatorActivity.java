package com.androidstudy.andelatrackchallenge;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.network.ApiClient;
import com.androidstudy.andelatrackchallenge.utils.CurrencyUtils;
import com.androidstudy.andelatrackchallenge.utils.SimpleTextWatcher;
import com.evernote.android.state.State;
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

    private Box<Country> countryBox;
    private Country country;

    @State
    boolean wasForward = true;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_title)
    TextView titleText;
    @BindView(R.id.image_flag)
    ImageView flagImage;
    @BindView(R.id.spinner_crypto)
    Spinner cryptoSpinner;
    @BindView(R.id.text_currency)
    TextView currencyText;
    @BindView(R.id.text_currency_label)
    TextView currencyLabelText;
    @BindView(R.id.text_crypto_label)
    TextView cryptoLabelText;
    @BindView(R.id.edit_crypto)
    EditText cryptoEdit;
    @BindView(R.id.edit_currency)
    EditText currencyEdit;

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
            titleText.setText(country.currency);
            flagImage.setImageResource(country.flagRes);
        }

        final String[] cryptos = getResources().getStringArray(R.array.crypto_currencies);
        cryptoSpinner.setAdapter(new SpinnerAdapter(this, 0, cryptos));
        cryptoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String label = position == 1 ? "Ethereum" : "Bitcoin";
                cryptoLabelText.setText(label);
                calculateCrypto(wasForward);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currencyText.setText(country.code);
        currencyLabelText.setText(country.currency);
        cryptoLabelText.setText(cryptos[0]);

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
        cryptoEdit.setText("1");
        cryptoEdit.setSelection(cryptoEdit.getText().length());

        calculateCrypto(true);

        cryptoEdit.setEnabled(true);
        currencyEdit.setEnabled(true);

        cryptoEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                cryptoEdit.addTextChangedListener(cryptoTextWatcher);
            } else {
                cryptoEdit.removeTextChangedListener(cryptoTextWatcher);
            }
        });
        currencyEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                currencyEdit.addTextChangedListener(currencyTextWatcher);
            } else {
                currencyEdit.removeTextChangedListener(currencyTextWatcher);
            }
        });
    }

    private void calculateCrypto(boolean forward) {
        wasForward = forward;
        int position = cryptoSpinner.getSelectedItemPosition();
        if (forward) {
            float value = CurrencyUtils.getFloat(cryptoEdit);
            float crypto = value > 0f ? value / (position == 1 ? country.eth : country.btc) : 0f;
            currencyEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), crypto));
        } else {
            float value = CurrencyUtils.getFloat(currencyEdit);
            float currency = value * (position == 1 ? country.eth : country.btc);
            cryptoEdit.setText(String.format(Locale.getDefault(),
                    getString(R.string.text_currency_unit), currency));
        }
    }

    private SimpleTextWatcher cryptoTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            calculateCrypto(true);
        }
    };
    private SimpleTextWatcher currencyTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            calculateCrypto(false);
        }
    };

    private class SpinnerAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;

        SpinnerAdapter(@NonNull Context context, int resource, @NonNull String[] objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_spinner_crypto, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.text_name);
            textView.setText(getItem(position));
            return convertView;
        }
    }
}
