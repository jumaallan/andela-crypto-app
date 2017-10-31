package com.androidstudy.andelatrackchallenge.picker.currency;

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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anonymous on 11/1/17.
 */

class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyVH> {
    private ArrayList<Country> countries;
    private OnItemClickListener<Country> onItemClickListener;

    public CurrencyAdapter(ArrayList<Country> countries, OnItemClickListener<Country> onItemClickListener) {
        this.countries = countries;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public CurrencyVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_picker, parent, false);
        return new CurrencyVH(view);
    }

    @Override
    public void onBindViewHolder(CurrencyVH holder, int position) {
        holder.bind(countries.get(position));
    }

    @Override
    public int getItemCount() {
        return countries == null ? 0 : countries.size();
    }

    public class CurrencyVH extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view_flag)
        ImageView flagImageView;
        @BindView(R.id.text_view_name)
        TextView nameTextView;
        @BindView(R.id.text_view_code)
        TextView codeTextView;

        public CurrencyVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                onItemClickListener.onItemClick(countries.get(position), position);
            });
        }

        public void bind(Country country) {
            nameTextView.setText(country.name);
            codeTextView.setText(country.code);
            flagImageView.setImageResource(country.flagRes);
        }
    }

}

