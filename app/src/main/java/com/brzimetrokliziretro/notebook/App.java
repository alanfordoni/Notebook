package com.brzimetrokliziretro.notebook;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base /*LocaleHelper.setLocale(base)*/);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //LocaleHelper.setLocale(this);
    }
}
