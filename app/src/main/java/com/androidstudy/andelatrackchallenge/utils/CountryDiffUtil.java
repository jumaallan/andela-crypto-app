package com.androidstudy.andelatrackchallenge.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

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
        return oldCountries.get(oldItemPosition).code
                .equalsIgnoreCase(newCountries.get(newItemPosition).currency);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldCountries.get(oldItemPosition)
                .equals(newCountries.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

