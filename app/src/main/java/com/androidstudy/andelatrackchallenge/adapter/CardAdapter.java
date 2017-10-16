package com.androidstudy.andelatrackchallenge.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anonymous on 10/17/17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardHolder> {
    private OnItemClickListener<Country> onItemClickListener;
    private List<Country> countries;
    private View emptyView;

    public CardAdapter(OnItemClickListener<Country> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        countries = new ArrayList<>();
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        /*Log.d(this.getClass().getSimpleName(), "Countries: " + newCountries.size());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CountryDiffUtil(countries, newCountries));
        diffResult.dispatchUpdatesTo(this);*/
        if (countries == null) {
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
            this.countries.add(0, country);
            notifyItemInserted(0);
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

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency, parent, false);
        return new CardHolder(view);
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        // to do change payloads use
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        holder.bind(countries.get(position));
    }

    @Override
    public int getItemCount() {
        int size = countries.size();
        if (size <= 0) {
            if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
        } else {
            if (emptyView != null) emptyView.setVisibility(View.GONE);
        }
        return size;
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

        public CardHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                onItemClickListener.onItemClick(countries.get(position), position);
            });
        }

        public void bind(Country country) {
            titleTextView.setText(country.name);
            flagImageView.setImageResource(country.flagRes);
        }
    }
}

