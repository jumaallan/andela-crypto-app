package com.androidstudy.andelatrackchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.adapter.HistorySparkAdapter;
import com.androidstudy.andelatrackchallenge.cards.HistoryRepository;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.network.ApiClient;
import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.androidstudy.andelatrackchallenge.utils.CurrencyUtils;
import com.androidstudy.andelatrackchallenge.utils.SimpleTextWatcher;
import com.evernote.android.state.State;
import com.robinhood.spark.SparkView;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import timber.log.Timber;

public class ConverterActivity extends ThemableActivity {
    public static final String COUNTRY = "COUNTRY";

    // todo include network-manager to resend request when internet connection comes around
    private HistoryRepository historyRepository;
    private Box<Country> countryBox;
    private Country country;

    @State
    boolean wasForward = true;
    @State
    int selectedIndex = -1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_flag)
    ImageView flagImage;
    @BindView(R.id.radio_bitcoin)
    RadioButton radioBitcoin;
    @BindView(R.id.radio_ethereum)
    RadioButton radioEthereum;
    @BindView(R.id.text_currency)
    TextView currencyText;
    @BindView(R.id.edit_crypto)
    EditText cryptoEdit;
    @BindView(R.id.edit_currency)
    EditText currencyEdit;
    @BindView(R.id.image_star)
    ImageView starImage;
    @BindView(R.id.spark_btc_container)
    View btcSparkContainer;
    @BindView(R.id.spark_eth_container)
    View ethSparkContainer;
    @BindView(R.id.spark_btc)
    SparkView btcSpark;
    @BindView(R.id.spark_eth)
    SparkView ethSpark;
    @BindView(R.id.text_btc_fluctuation)
    TextView btcFluctuationText;
    @BindView(R.id.text_eth_fluctuation)
    TextView ethFluctuationText;
    @BindView(R.id.progress_bar)
    MaterialProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        AndelaTrackChallenge cryptoConverter = (AndelaTrackChallenge) getApplicationContext();
        countryBox = cryptoConverter.getBoxStore().boxFor(Country.class);
        historyRepository = cryptoConverter.getHistoryRepo();
        country = getIntent().getParcelableExtra(COUNTRY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            flagImage.setImageResource(country.getFlagRes());
        }

        currencyText.setText(String.format(Locale.getDefault(), getString(R.string.calc_currency), country.code, country.currency));

        if (country.eth > 0f && country.btc > 0f) {
            finishSetup();
        } else {
            ApiClient.loadRate(country)
                    .doOnSubscribe(d -> progressBar.setVisibility(View.VISIBLE))
                    .doAfterTerminate(() -> progressBar.setVisibility(View.GONE))
                    .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                    .subscribe(newCountry -> {
                        this.country = newCountry;
                        countryBox.put(newCountry);
                        finishSetup();
                    }, Timber::e);
        }
    }

    private void finishSetup() {
        starImage.setVisibility(country.isFavorite ? View.VISIBLE : View.GONE);
        cryptoEdit.setEnabled(true);
        currencyEdit.setEnabled(true);
        cryptoEdit.setSelection(cryptoEdit.getText().length());

        if (selectedIndex < 0) {
            selectedIndex = 0;
        }

        if (selectedIndex == 1) {
            radioEthereum.setChecked(true);
        } else {
            radioBitcoin.setChecked(true);
        }

        convertCurrency(wasForward);
        radioBitcoin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIndex = 0;
                convertCurrency(wasForward);
            }
        });
        radioEthereum.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIndex = 1;
                convertCurrency(wasForward);
            }
        });

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

        setupSparkViews();
    }

    private void convertCurrency(boolean forward) {
        wasForward = forward;
        if (forward) {
            float value = CurrencyUtils.getFloat(cryptoEdit);
            float currency = value > 0f
                    ? value / (selectedIndex == 1 ? country.eth : country.btc)
                    : 0f;

            currencyEdit.setText(String.format(
                    Locale.getDefault(),
                    Settings.isThreeDecimalPlaces()
                            ? getString(R.string.text_currency_unit_3dp)
                            : getString(R.string.text_currency_unit),
                    currency));
        } else {
            float value = CurrencyUtils.getFloat(currencyEdit);
            float crypto = value * (selectedIndex == 1 ? country.eth : country.btc);

            cryptoEdit.setText(String.format(
                    Locale.getDefault(),
                    Settings.isThreeDecimalPlaces()
                            ? getString(R.string.text_currency_unit_3dp)
                            : getString(R.string.text_currency_unit),
                    crypto));
        }
    }

    private void setupSparkViews() {
        historyRepository.getDailyHistoryFor(country, ApiClient.BTC)
                .doOnSubscribe(d -> progressBar.setVisibility(View.VISIBLE))
                .doAfterTerminate(() -> progressBar.setVisibility(View.GONE))
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                .subscribe(historyDb -> {
                    btcSparkContainer.setVisibility(View.VISIBLE);
                    btcFluctuationText.setText(String.format(Locale.getDefault(),
                            getString(R.string.text_spark_line_title), "Bitcoin", 5 + " days"));
                    HistorySparkAdapter adapter = new HistorySparkAdapter(historyDb.data);
                    btcSpark.setAdapter(adapter);
                }, Timber::e);

        historyRepository.getDailyHistoryFor(country, ApiClient.ETH)
                .doOnSubscribe(d -> progressBar.setVisibility(View.VISIBLE))
                .doAfterTerminate(() -> progressBar.setVisibility(View.GONE))
                .to(AutoDispose.with(AndroidLifecycleScopeProvider.from(this)).forSingle())
                .subscribe(historyDb -> {
                    ethSparkContainer.setVisibility(View.VISIBLE);
                    ethFluctuationText.setText(String.format(Locale.getDefault(),
                            getString(R.string.text_spark_line_title), "Ethereum", 5 + " days"));
                    HistorySparkAdapter adapter = new HistorySparkAdapter(historyDb.data);
                    ethSpark.setAdapter(adapter);
                }, Timber::e);
    }

    private SimpleTextWatcher cryptoTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            convertCurrency(true);
        }
    };
    private SimpleTextWatcher currencyTextWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            convertCurrency(false);
        }
    };
}
