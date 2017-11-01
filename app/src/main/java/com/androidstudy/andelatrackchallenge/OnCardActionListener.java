package com.androidstudy.andelatrackchallenge;

import com.androidstudy.andelatrackchallenge.models.Country;

/**
 * Created by anonymous on 11/1/17.
 */

public interface OnCardActionListener {
    void onRemoved(Country country);

    void onEdited(Country country);
}