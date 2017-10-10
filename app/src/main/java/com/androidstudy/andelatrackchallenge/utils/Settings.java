package com.androidstudy.andelatrackchallenge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by anonymous on 10/10/17.
 */

public class Settings {
    //Keys for Shared preferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "andela";
    //Check if the User is logged in or not
    public static final String LOGGED_IN_SHARED_PREF = "loggedin";

    /**
     * This class is responsible for handling the Shared Preferences, We saved the application
     * as logged here!
     * <p>
     * We are basically using this file to create and handle user sessions!
     * <p>
     * This file is cleared when the User Logs Out of the application
     */

    private final SharedPreferences settings;
    private final Context context;

    public Settings(@NonNull Context context) {
        this.context = context;
        settings = this.context.getSharedPreferences(SHARED_PREF_NAME, 0);
    }

    /**
     * Set Logged in
     */
    public boolean isLoggedIn() {
        return settings.getBoolean(LOGGED_IN_SHARED_PREF, false);
    }

    public void setLoggedInSharedPref(boolean loggedIn) {
        settings.edit()
                .putBoolean(LOGGED_IN_SHARED_PREF, loggedIn)
                .apply();
    }
}
