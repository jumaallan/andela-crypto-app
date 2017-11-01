package com.androidstudy.andelatrackchallenge;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.androidstudy.andelatrackchallenge.settings.Settings;

import io.reactivex.Maybe;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

import static com.androidstudy.andelatrackchallenge.ActivityEvent.CREATE;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.DESTROY;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.PAUSE;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.RESUME;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.START;
import static com.androidstudy.andelatrackchallenge.ActivityEvent.STOP;

/**
 * Created by anonymous on 11/2/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public static final int[] themes = {R.style.AppTheme, R.style.AppTheme_Light};

    private int themeIndex = Settings.themeIndex();
    private BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

    @Override
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, getThemeRes(), first);
    }

    protected Maybe<?> until(ActivityEvent event) {
        return lifecycle.filter(e -> e == event).firstElement();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycle.onNext(CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycle.onNext(START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycle.onNext(RESUME);
        if (themeIndex != Settings.themeIndex()) {
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        lifecycle.onNext(PAUSE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycle.onNext(STOP);
    }

    @Override
    protected void onDestroy() {
        lifecycle.onNext(DESTROY);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @StyleRes
    private int getThemeRes() {
        try {
            return themes[themeIndex];
        } catch (Exception e) {
            Timber.e(e);
            return themes[0];
        }
    }
}
