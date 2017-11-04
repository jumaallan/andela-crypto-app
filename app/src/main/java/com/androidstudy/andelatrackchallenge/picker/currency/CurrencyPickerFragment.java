package com.androidstudy.andelatrackchallenge.picker.currency;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.androidstudy.andelatrackchallenge.AndelaTrackChallenge;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.cards.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

/**
 * Created by anonymous on 10/17/17.
 */

public class CurrencyPickerFragment extends DialogFragment implements OnItemClickListener<Country> {
    private static final String COUNTRIES = "COUNTRIES";

    private CurrencyAdapter adapter;
    private LinearLayoutManager layoutManager;
    private CurrencyPickerListener pickerListener;


    /**
     * @param countries is the list of countries for which to disable picking
     * @return a new instance of {@link CurrencyPickerFragment}
     */
    public static CurrencyPickerFragment newInstance(List<Country> countries) {
        Bundle args = new Bundle();
        if (countries instanceof ArrayList) {
            args.putParcelableArrayList(COUNTRIES, (ArrayList<Country>) countries);
        } else {
            args.putParcelableArrayList(COUNTRIES, new ArrayList<>());
        }

        CurrencyPickerFragment fragment = new CurrencyPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Country> disabled = getArguments().getParcelableArrayList(COUNTRIES);

        if (getActivity() instanceof CurrencyPickerListener)
            pickerListener = (CurrencyPickerListener) getActivity();
        else
            dismiss();

        adapter = new CurrencyAdapter(Countries.countries, disabled);
        layoutManager = new LinearLayoutManager(getActivity());

        adapter.setOnItemClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title("Select currency")
                .adapter(adapter, layoutManager)
                .negativeText("Cancel")
                .build();
    }

    @Override
    public void onItemClick(Country country, int position) {
        pickerListener.onPicked(country, position);
        dismiss();
    }
}
