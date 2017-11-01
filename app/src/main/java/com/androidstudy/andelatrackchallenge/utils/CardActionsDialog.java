package com.androidstudy.andelatrackchallenge.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.andelatrackchallenge.cards.OnCardActionListener;
import com.androidstudy.andelatrackchallenge.R;
import com.androidstudy.andelatrackchallenge.models.Country;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anonymous on 11/1/17.
 */

public class CardActionsDialog extends BottomSheetDialogFragment {
    private static final String COUNTRY = "COUNTRY";
    private OnCardActionListener onCardActionListener;

    @BindView(R.id.text_view_name)
    TextView nameText;
    @BindView(R.id.text_view_code)
    TextView codeText;
    @BindView(R.id.image_view_flag)
    ImageView flagImage;
    @BindView(R.id.text_view_edit)
    TextView editText;
    @BindView(R.id.text_view_delete)
    TextView deleteText;

    public static CardActionsDialog newInstance(Country country) {
        Bundle args = new Bundle();
        args.putParcelable(COUNTRY, country);
        CardActionsDialog fragment = new CardActionsDialog();
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
            flagImage.setImageResource(country.flagRes);
            editText.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "Editing...", Toast.LENGTH_SHORT).show();
                onCardActionListener.onEdited(country);
                dismiss();
            });
            deleteText.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "Deleting...", Toast.LENGTH_SHORT).show();
                onCardActionListener.onRemoved(country);
                dismiss();
            });
        }
        return view;
    }
}

