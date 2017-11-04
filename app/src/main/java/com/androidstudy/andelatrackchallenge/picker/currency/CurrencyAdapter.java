package com.androidstudy.andelatrackchallenge.picker.currency;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.cards.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyVH> {
    private ArrayList<Country> countries;
    private List<Country> disabled;
    private OnItemClickListener<Country> onItemClickListener;

    public CurrencyAdapter(ArrayList<Country> countries, List<Country> disabled) {
        this.countries = countries;
        this.disabled = disabled;

        // Sort the countries to arrange in alphabetical order
        Collections.sort(this.countries, comparator);
    }

    public void setOnItemClickListener(OnItemClickListener<Country> onItemClickListener) {
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

    private Comparator<Country> comparator = (lhs, rhs) -> lhs.code.compareToIgnoreCase(rhs.code);

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
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    onItemClickListener.onItemClick(countries.get(position), position);
                }
            });
        }

        public void bind(Country country) {
            if (disabled.contains(country)) {
                itemView.setOnClickListener(null);
                itemView.setAlpha(0.7f);
            }

            nameTextView.setText(country.name);
            codeTextView.setText(country.code);
            flagImageView.setImageResource(country.getFlagRes());
        }
    }

}
