package com.brzimetrokliziretro.notebook.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

public class LocaleHelper {
    private static final String TAG = "BEL.localehelper";
    private static final String LANGUAGE_KEY = "language_key";
    private static final String LANGUAGE_ENGLISH = "en";

    public static Context setLocale(Context context) {

        return updateResources(context, getLanguage(context));
    }

    public LocaleHelper(Context context){}

    public static Context setNewLocale(Context context, String language) {
      //  storeLanguage(context, language);

        return updateResources(context, language);
    }

    public static Context updateResources(Context context, String language) {
        Log.d(TAG, "updateResources: lan: " + language);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration configuration = new Configuration(res.getConfiguration());
        configuration.setLocale(locale);
        res.updateConfiguration(configuration, dm);

        //configuration.setLocale(locale);
        //context = context.createConfigurationContext(configuration);

        Log.d(TAG, "updateResources: ");
        return context;
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("language_key", LANGUAGE_ENGLISH);
    }

    @SuppressLint("ApplySharedPref")
    private static void storeLanguage(Context c, String language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
       prefs.edit().putString(LANGUAGE_KEY, language).apply();
    }

    public static Locale getLocale(Resources res) {
        Configuration configuration = res.getConfiguration();
        return Build.VERSION.SDK_INT >= 24 ? configuration.getLocales().get(0) : configuration.locale;
    }

}
