package com.androidstudy.andelatrackchallenge.cards;

import com.androidstudy.andelatrackchallenge.models.Country;

/**
 * Created by anonymous on 11/1/17.
 */

public interface OnCardActionListener {
    void onToggleStar(Country country);

    void onRemoved(Country country);

    void onEdited(Country country);
}