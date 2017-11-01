package com.androidstudy.andelatrackchallenge.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.util.DiffUtil;

import com.androidstudy.andelatrackchallenge.adapter.CardsAdapter;
import com.androidstudy.andelatrackchallenge.models.Country;

import java.util.List;

/**
 * Created by anonymous on 10/17/17.
 */

public class CountryDiffUtil extends DiffUtil.Callback {
    private List<Country> oldCountries;
    private List<Country> newCountries;

    public CountryDiffUtil(List<Country> oldCountries, List<Country> newCountries) {
        this.oldCountries = oldCountries;
        this.newCountries = newCountries;
    }

    @Override
    public int getOldListSize() {
        return oldCountries == null ? 0 : oldCountries.size();
    }

    @Override
    public int getNewListSize() {
        return newCountries == null ? 0 : newCountries.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCountries.get(oldItemPosition).equals(newCountries.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Country oldCountry = oldCountries.get(oldItemPosition);
        Country newCountry = newCountries.get(newItemPosition);

        return oldCountry.refreshedAt == newCountry.refreshedAt &&
                oldCountry.btc == newCountry.btc &&
                oldCountry.eth == newCountry.eth &&
                ObjectsCompat.equals(oldCountry.currency, newCountry.currency) &&
                ObjectsCompat.equals(oldCountry.code, newCountry.code);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Country oldCountry = oldCountries.get(oldItemPosition);
        Country newCountry = newCountries.get(newItemPosition);

        Bundle bundle = new Bundle();
        bundle.putString(CardsAdapter.CODE, newCountry.code);

        if (oldCountry.btc != newCountry.btc)
            bundle.putFloat(CardsAdapter.BITCOIN, newCountry.btc);
        if (oldCountry.btc != newCountry.btc)
            bundle.putFloat(CardsAdapter.ETHEREUM, newCountry.eth);

        return bundle;
    }
}
