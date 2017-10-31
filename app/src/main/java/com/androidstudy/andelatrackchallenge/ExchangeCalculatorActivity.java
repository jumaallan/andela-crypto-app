package com.androidstudy.andelatrackchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.models.Country;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anonymous on 11/1/17.
 */

public class ExchangeCalculatorActivity extends AppCompatActivity {
    public static final String COUNTRY = "COUNTRY";

    private Country country;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_flag)
    ImageView imageFlag;
    @BindView(R.id.text_title)
    TextView textTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_calculator);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        country = getIntent().getParcelableExtra(COUNTRY);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            textTitle.setText(country.name);
            imageFlag.setImageResource(country.flagRes);
        }
    }
}
