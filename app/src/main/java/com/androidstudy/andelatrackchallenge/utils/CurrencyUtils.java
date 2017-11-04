package com.androidstudy.andelatrackchallenge.utils;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by anonymous on 11/1/17.
 */

public class CurrencyUtils {
    public static final NumberFormat format;

    static {
        format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) format).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) format).setDecimalFormatSymbols(decimalFormatSymbols);
    }

    private CurrencyUtils() {
    }

    public static float getFloat(EditText editText) {
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

