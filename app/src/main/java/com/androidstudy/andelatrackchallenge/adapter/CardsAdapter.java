package com.androidstudy.andelatrackchallenge.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidstudy.andelatrackchallenge.cards.CardActionsDialog;
import com.androidstudy.andelatrackchallenge.cards.OnCardActionListener;
import com.androidstudy.andelatrackchallenge.cards.OnItemLongClickListener;
import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.androidstudy.andelatrackchallenge.utils.CurrencyUtils;
import com.androidstudy.andelatrackchallenge.cards.OnItemClickListener;
import com.androidstudy.andelatrackchallenge.utils.Easel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by anonymous on 10/17/17.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardHolder> {
    public static final String BITCOIN = "BITCOIN";
    public static final String ETHEREUM = "ETHEREUM";
    public static final String CODE = "CODE";

    private View emptyView;
    private OnItemClickListener<Country> onItemClickListener;
    private OnCardActionListener onCardActionListener;
    private List<Country> countries;

    public CardsAdapter() {
        countries = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener<Country> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCardActionListener(OnCardActionListener onCardActionListener) {
        this.onCardActionListener = onCardActionListener;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    private void showEmptyView(boolean show) {
        if (emptyView != null) {
            if (show) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setAlpha(0.0f);
                emptyView.setScaleX(0.6f);
                emptyView.setScaleY(0.6f);

                emptyView.animate()
                        .alpha(1.0f)
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setInterpolator(new AccelerateInterpolator())
                        .setDuration(400L)
                        .setListener(null)
                        .start();
            } else {
                emptyView.animate()
                        .alpha(0.0f)
                        .scaleX(0.6f)
                        .scaleY(0.6f)
                        .setInterpolator(new OvershootInterpolator())
                        .setDuration(400L)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                emptyView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }
        }
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
            showEmptyView(true);
        } else {
            this.countries = countries;
            Collections.sort(this.countries, comparator);
            showEmptyView(false);
        }
        notifyDataSetChanged();
    }

    public void add(Country... countries) {
        if (countries == null) return;
        for (Country country : countries) {
            if (country == null) continue;
            this.countries.add(country);
            showEmptyView(false);
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

    public void moveToPosition(Country country) {
        replace(country);

        int fromPosition = countries.indexOf(country);
        Collections.sort(countries, comparator);
        int toPosition = countries.indexOf(country);

        notifyItemMoved(fromPosition, toPosition);
        notifyItemChanged(toPosition);
    }

    public void remove(Country country) {
        if (country == null) return;

        int index = countries.indexOf(country);
        if (index >= 0) {
            countries.remove(country);
            notifyItemRemoved(index);
        }

        showEmptyView(countries.size() == 0);
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
    public void onViewRecycled(CardHolder holder) {
        super.onViewRecycled(holder);
        holder.unbind();
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    private Comparator<Country> comparator = (lhs, rhs) -> {
        if (lhs.isFavorite && rhs.isFavorite) {
            return (int) (lhs.id - rhs.id);
        } else if (rhs.isFavorite) {
            return 1;
        } else if (lhs.isFavorite) {
            return -1;
        } else {
            return (int) (lhs.id - rhs.id);
        }
    };

    public class CardHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_currency)
        CardView cardCurrency;
        @BindView(R.id.text_view_currency_title)
        TextView titleTextView;
        @BindView(R.id.image_view_currency_flag)
        ImageView flagImageView;
        @BindView(R.id.text_view_btc)
        TextView btcTextView;
        @BindView(R.id.text_view_eth)
        TextView ethTextView;
        @BindView(R.id.text_view_btc_label)
        TextView btcLabelTextView;
        @BindView(R.id.text_view_eth_label)
        TextView ethLabelTextView;
        @BindView(R.id.button_overflow)
        ImageButton overflowButton;
        @BindView(R.id.image_star)
        ImageView starImage;
        @BindView(R.id.icon_btc_price)
        ImageView btcPriceIcon;
        @BindView(R.id.icon_eth_price)
        ImageView ethPriceIcon;

        private String signature = "";

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
                if (onCardActionListener != null) {
                    CardActionsDialog actionsDialog = CardActionsDialog
                            .newInstance(countries.get(getAdapterPosition()), onCardActionListener);
                    actionsDialog.show(
                            ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(),
                            "card-actions");
                    return true;
                }
                return false;
            });
            overflowButton.setOnClickListener(v -> {
                if (onCardActionListener != null) {
                    CardActionsDialog actionsDialog = CardActionsDialog
                            .newInstance(countries.get(getAdapterPosition()), onCardActionListener);
                    actionsDialog.show(
                            ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager(),
                            "card-actions");
                }
            });
        }

        public void bind(Country country) {
            signature = country.toString();
            titleTextView.setText(country.currency);
            flagImageView.setImageResource(country.getFlagRes());
            starImage.setVisibility(country.isFavorite ? View.VISIBLE : View.GONE);

            Context context = itemView.getContext();
            String currencyFormat = context.getString(R.string.text_currency_units);

            float btcCountry = 1.0f / country.btc;
            float ethCountry = 1.0f / country.eth;
            Timber.e(country.toString());

            btcTextView.setText(country.btc > 0
                    ? String.format(Locale.getDefault(), currencyFormat, country.code, CurrencyUtils.format.format(btcCountry))
                    : "...");

            ethTextView.setText(country.eth > 0
                    ? String.format(Locale.getDefault(), currencyFormat, country.code, CurrencyUtils.format.format(ethCountry))
                    : "...");

            // Is BTC/ETH price rising or dropping? Reflect it to user
            setPriceIcon(btcPriceIcon, country.btcStatus);
            setPriceIcon(ethPriceIcon, country.ethStatus);

            if (Settings.isShowColoredCards()) {
                loadCardColor(country);
            }
        }

        private void loadCardColor(Country country) {
            Context context = itemView.getContext();
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), country.getFlagRes());
            Palette.from(bitmap)
                    .maximumColorCount(4)
                    .generate(palette -> {
                        if (!ObjectsCompat.equals(signature, country.toString())) {
                            return;
                        }
                        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                        if (swatch == null) {
                            swatch = palette.getLightVibrantSwatch();
                        }
                        if (swatch == null) {
                            swatch = new Palette.Swatch(Easel.getThemeAttrColor(context, R.attr.cardColor), 4);
                        }

                        cardCurrency.setCardBackgroundColor(Easel.getDarkerColor(swatch.getRgb(), 0.9f));
                        titleTextView.setTextColor(swatch.getTitleTextColor());
                        int bodyTextColor = swatch.getBodyTextColor();

                        ButterKnife.apply(
                                Arrays.asList(btcTextView, ethTextView, btcLabelTextView, ethLabelTextView),
                                (ButterKnife.Action<TextView>) (view, index) ->
                                        view.setTextColor(bodyTextColor));

                        overflowButton.setImageDrawable(Easel
                                .tint(overflowButton.getDrawable(), bodyTextColor));
                    });
        }

        private void setPriceIcon(ImageView imageView, int status) {
            switch (status) {
                case Country.RISE:
                    imageView.setImageResource(R.drawable.ic_arrow_rise);
                    break;
                case Country.SAME:
                    imageView.setImageResource(R.drawable.ic_same);
                    break;
                case Country.DROP:
                    Context context = imageView.getContext();
                    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop);
                    DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.color_price_drop));
                    imageView.setImageDrawable(drawable);
                    break;
            }
        }

        public void bind(Bundle bundle) {
            Context context = itemView.getContext();
            String currencyFormat = context.getString(R.string.text_currency_units);

            float btc = bundle.getFloat(CardsAdapter.BITCOIN, -1);
            float eth = bundle.getFloat(CardsAdapter.ETHEREUM, -1);
            String code = bundle.getString(CardsAdapter.CODE, "");
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
