package com.androidstudy.andelatrackchallenge.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;

import timber.log.Timber;

/**
 * Created by anonymous on 10/10/17.
 */

public class Settings {
    //Keys for Shared preferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "andela";
    //Check if the User is logged in or not
    public static final String LOGGED_IN_SHARED_PREF = "loggedin";
    //Check if its first time use
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    //Check if its Facebook or Google
    private static final String IS_FACEBOOK = "IsFacebook";

    /**
     * This class is responsible for handling the Shared Preferences, We saved the application
     * as logged here!
     * <p>
     * We are basically using this file to create and handle user sessions!
     * <p>
     * This file is cleared when the User Logs Out of the application
     */

    private static SharedPreferences settings;
    private static SharedPreferences defaultPrefs;

    public static void init(@NonNull Context context) {
        settings = context.getSharedPreferences(SHARED_PREF_NAME, 0);
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set Logged in
     */
    public static boolean isLoggedIn() {
        return settings.getBoolean(LOGGED_IN_SHARED_PREF, false);
    }

    public static void setLoggedInSharedPref(boolean loggedIn) {
        settings.edit()
                .putBoolean(LOGGED_IN_SHARED_PREF, loggedIn)
                .apply();
    }

    public static void setFirstTimeLaunch(boolean isFirstTime) {
        settings.edit()
                .putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
                .apply();
    }

    public static boolean isFirstTimeLaunch() {
        return settings.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static void setIsFacebook(boolean isFacebook) {
        settings.edit()
                .putBoolean(IS_FIRST_TIME_LAUNCH, isFacebook)
                .apply();
    }

    public static boolean isFacebook() {
        return settings.getBoolean(IS_FACEBOOK, true);
    }

    public static int themeIndex() {
        int themeIndex = 0;
        try {
            themeIndex = Integer.parseInt(defaultPrefs.getString("THEME", "0"));
        } catch (Exception e) {
            Timber.e(e);
        }
        return themeIndex;
    }

    public static void setThemeIndex(int themeIndex) {
        defaultPrefs.edit()
                .putString("THEME", String.valueOf(themeIndex))
                .apply();
    }

    public static boolean isShowColoredCards() {
        return defaultPrefs.getBoolean("COLORED_CARDS", false);
    }

    public static boolean isThreeDecimalPlaces() {
        return defaultPrefs.getBoolean("DECIMAL_PLACES", false);
    }
}
