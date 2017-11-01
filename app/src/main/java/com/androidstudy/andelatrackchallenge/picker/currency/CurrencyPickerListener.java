package com.androidstudy.andelatrackchallenge.picker.currency;

import com.androidstudy.andelatrackchallenge.models.Country;

/**
 * Created by anonymous on 10/17/17.
 */

public interface CurrencyPickerListener {
    void onPicked(Country country, int position);
}


