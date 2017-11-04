package com.androidstudy.andelatrackchallenge;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.evernote.android.state.StateSaver;

import io.reactivex.Maybe;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;


/**
 * Created by anonymous on 11/2/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateSaver.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

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
