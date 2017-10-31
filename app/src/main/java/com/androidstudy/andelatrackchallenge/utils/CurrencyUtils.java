package com.androidstudy.andelatrackchallenge.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

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
}
