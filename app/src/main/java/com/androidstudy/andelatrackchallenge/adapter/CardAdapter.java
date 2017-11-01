package com.androidstudy.andelatrackchallenge.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.OnItemLongClickListener;
import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.utils.CurrencyUtils;
import com.androidstudy.andelatrackchallenge.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anonymous on 10/17/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {
    public static final String BITCOIN = "BITCOIN";
    public static final String ETHEREUM = "ETHEREUM";
    public static final String CODE = "CODE";

    private OnItemClickListener<Country> onItemClickListener;
    private OnItemLongClickListener<Country> onItemLongClickListener;
    private List<Country> countries;

    public CardAdapter() {
        countries = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener<Country> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<Country> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        /*DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CountryDiffUtil(countries, countries));
        diffResult.dispatchUpdatesTo(this);*/

        // adding check for countries.size() == 0 fixes the crash on adding using add(Country country)
        // when the list is empty because countries passed from countryBox.getAll() returns an
        // EmptyList from Collections.emptyList()
        if (countries == null || countries.size() == 0) {
            this.countries.clear();
        } else {
            this.countries = countries;
        }
        notifyDataSetChanged();
    }

    public void add(Country... countries) {
        if (countries == null) return;
        for (Country country : countries) {
            if (country == null) continue;
            this.countries.add(country);
            notifyItemInserted(this.countries.size() - 1);
        }
    }

    public void replace(Country newCountry) {
        for (Country country : countries) {
            if (ObjectsCompat.equals(country.code, newCountry.code)) {
                int index = countries.indexOf(country);
                countries.set(index, newCountry);
                notifyItemChanged(index);

                break;
            }
        }
    }

    public void remove(Country country) {
        if (country == null) return;

        int index = countries.indexOf(country);
        if (index >= 0) {
            countries.remove(country);
            notifyItemRemoved(index);
        }
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency, parent, false);
        return new CardHolder(view);
    }

    /*@Override
    public void onBindViewHolder(CardHolder holder, int position, List<Object> payloads) {
        if (payloads != null && payloads.size() > position) {
            Object payload = payloads.get(position);
            if (payload != null && payload instanceof Bundle) {
                holder.bind((Bundle) payload);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }*/

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        holder.bind(countries.get(position));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_currency_title)
        TextView titleTextView;
        @BindView(R.id.image_view_currency_flag)
        ImageView flagImageView;
        @BindView(R.id.text_view_btc)
        TextView btcTextView;
        @BindView(R.id.text_view_eth)
        TextView ethTextView;
        @BindView(R.id.button_overflow)
        ImageButton overflowButton;

        public CardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    onItemClickListener.onItemClick(countries.get(position), position);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (onItemLongClickListener != null) {
                    int position = getAdapterPosition();
                    onItemLongClickListener.onItemLongClick(getCountries().get(position), position);
                    return true;
                }
                return false;
            });
            overflowButton.setOnClickListener(v -> {
                if (onItemLongClickListener != null) {
                    int position = getAdapterPosition();
                    onItemLongClickListener.onItemLongClick(getCountries().get(position), position);
                }
            });
        }

        public void bind(Country country) {
            titleTextView.setText(country.name);
            flagImageView.setImageResource(country.flagRes);

            Context context = itemView.getContext();
            String currencyFormat = context.getString(R.string.text_currency_units);

            float btcCountry = 1.0f / country.btc;
            float ethCountry = 1.0f / country.eth;
            Log.e("CardAdapter", country.toString());

            btcTextView.setText(country.btc > 0
                    ? String.format(Locale.getDefault(), currencyFormat, country.code, CurrencyUtils.format.format(btcCountry))
                    : "...");

            ethTextView.setText(country.eth > 0
                    ? String.format(Locale.getDefault(), currencyFormat, country.code, CurrencyUtils.format.format(ethCountry))
                    : "...");
        }

        public void bind(Bundle bundle) {
            Context context = itemView.getContext();
            String currencyFormat = context.getString(R.string.text_currency_units);

            float btc = bundle.getFloat(CardAdapter.BITCOIN, -1);
            float eth = bundle.getFloat(CardAdapter.ETHEREUM, -1);
            String code = bundle.getString(CardAdapter.CODE, "");
            float btcCountry = 1.0f / btc;
            float ethCountry = 1.0f / eth;

            btcTextView.setText(btc > 0
                    ? String.format(Locale.getDefault(), currencyFormat, code, CurrencyUtils.format.format(btcCountry))
                    : "...");

            ethTextView.setText(eth > 0
                    ? String.format(Locale.getDefault(), currencyFormat, code, CurrencyUtils.format.format(ethCountry))
                    : "...");
        }

        public void unbind() {
            titleTextView.setText("");
            btcTextView.setText("");
            ethTextView.setText("");

        }
    }
}