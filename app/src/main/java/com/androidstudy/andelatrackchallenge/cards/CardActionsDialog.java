package com.androidstudy.andelatrackchallenge.cards;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;
import com.androidstudy.andelatrackchallenge.models.HistoryDb;
import com.androidstudy.andelatrackchallenge.models.HistoryDb_;
import com.androidstudy.andelatrackchallenge.network.ApiClient;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by anonymous on 11/1/17.
 */

public class CardActionsDialog extends BottomSheetDialogFragment {
    private static final String COUNTRY = "COUNTRY";
    private static OnCardActionListener onCardActionListener;

    @BindView(R.id.text_view_name)
    TextView nameText;
    @BindView(R.id.text_view_code)
    TextView codeText;
    @BindView(R.id.image_view_flag)
    ImageView flagImage;
    @BindView(R.id.text_view_pin)
    TextView pinText;
    @BindView(R.id.text_view_edit)
    TextView editText;
    @BindView(R.id.text_view_delete)
    TextView deleteText;

    public static CardActionsDialog newInstance(Country country, OnCardActionListener listener) {
        Bundle args = new Bundle();
        args.putParcelable(COUNTRY, country);
        CardActionsDialog fragment = new CardActionsDialog();
        onCardActionListener = listener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnCardActionListener) {
            onCardActionListener = (OnCardActionListener) getActivity();
        } else {
            dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_actions, container, false);
        Country country = getArguments().getParcelable(COUNTRY);
        ButterKnife.bind(this, view);

        if (country != null) {
            nameText.setText(country.name);
            codeText.setText(country.code);
            flagImage.setImageResource(country.getFlagRes());

            pinText.setText(country.isFavorite ? "Unpin item" : "Pin item to top");
            pinText.setCompoundDrawablesWithIntrinsicBounds(
                    country.isFavorite ? R.drawable.ic_star : R.drawable.ic_star_border,
                    0,
                    0,
                    0);

            pinText.setOnClickListener(v -> {
                onCardActionListener.onToggleStar(country);
                dismiss();
            });

            editText.setOnClickListener(v -> {
                onCardActionListener.onEdited(country);
                dismiss();
            });
            deleteText.setOnClickListener(v -> {
                onCardActionListener.onRemoved(country);
                dismiss();
            });
        }
        return view;
    }
}
