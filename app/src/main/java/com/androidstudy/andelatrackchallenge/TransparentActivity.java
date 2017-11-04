package com.androidstudy.andelatrackchallenge;

import android.support.annotation.StyleRes;

import timber.log.Timber;

/**
 * Created by anonymous on 11/4/17.
 */

public class TransparentActivity extends ThemableActivity {
    public static final int[] themes = {R.style.TransparentTheme, R.style.TransparentTheme_Light};

    @StyleRes
    protected int getThemeRes(int index) {
        try {
            return themes[index];
        } catch (Exception e) {
            Timber.e(e);
            return themes[0];
        }
    }
}

