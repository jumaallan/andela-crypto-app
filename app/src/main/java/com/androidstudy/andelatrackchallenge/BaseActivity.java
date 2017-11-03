package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
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

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRestart()) {
            startActivity(new Intent(this, this.getClass()));
            finish();

            // using recreate() causes a bug where the back button does not respond to being
            // clicked!
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean shouldRestart() {
        return false;
    }
}
