package com.androidstudy.andelatrackchallenge;

import android.app.Application;
import android.os.Environment;

import com.androidstudy.andelatrackchallenge.models.MyObjectBox;

import java.io.File;

import io.objectbox.BoxStore;

/**
 * Created by anonymous on 10/10/17.
 */

public class AndelaTrackChallenge extends Application {
    public static final boolean EXTERNAL_DIR = false;

    private BoxStore boxStore;
    private static AndelaTrackChallenge instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        if (EXTERNAL_DIR) {
            // Example how you could use a custom dir in "external storage"
            // (Android 6+ note: give the app storage permission in app info settings)
            File directory = new File(Environment.getExternalStorageDirectory(), "objectbox-andelatrackchallenge");
            boxStore = MyObjectBox.builder().androidContext(AndelaTrackChallenge.this).directory(directory).build();
        } else {
            // This is the minimal setup required on Android
            boxStore = MyObjectBox.builder().androidContext(AndelaTrackChallenge.this).build();
        }
    }

    public static synchronized AndelaTrackChallenge getInstance() {
        return instance;
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
