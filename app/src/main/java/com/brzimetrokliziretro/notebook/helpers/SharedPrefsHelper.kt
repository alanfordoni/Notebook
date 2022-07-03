package com.brzimetrokliziretro.notebook.helpers

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsHelper {

    const val SETTINGS_PREFS = "settings_preferences"

    private lateinit var sharedPrefs: SharedPreferences

    fun init(context: Context) {
        sharedPrefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }


    fun clearSettings() {
        sharedPrefs.edit().clear().apply()
    }

}