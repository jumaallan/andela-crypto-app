package com.androidstudy.andelatrackchallenge;

import android.app.Application;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.androidstudy.andelatrackchallenge.models.MyObjectBox;
import com.androidstudy.andelatrackchallenge.settings.Settings;
import com.facebook.FacebookSdk;

import java.io.File;

import io.objectbox.BoxStore;
import timber.log.Timber;

/**
 * Created by anonymous on 10/10/17.
 */

public class AndelaTrackChallenge extends MultiDexApplication {
    /**
     * This class is necessary to initialize the following
     * Facebook SDK
     * ObjectBox ORM
     */
    public static final boolean EXTERNAL_DIR = false;

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // plant a new debug tree
        Timber.plant(new Timber.DebugTree());
        // init Settings
        Settings.init(this);
        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        if (EXTERNAL_DIR) {
            // Example how you could use a custom dir in "external storage"
            // (Android 6+ note: give the app storage permission in app info settings)
            File directory = new File(Environment.getExternalStorageDirectory(), "objectbox-andelatrackchallenge");
            boxStore = MyObjectBox.builder().androidContext(this).directory(directory).build();
        } else {
            // This is the minimal setup required on Android
            boxStore = MyObjectBox.builder().androidContext(this).build();
        }
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
