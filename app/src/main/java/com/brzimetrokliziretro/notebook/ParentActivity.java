package com.brzimetrokliziretro.notebook;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.brzimetrokliziretro.notebook.helpers.LocaleHelper;

import static android.content.pm.PackageManager.GET_META_DATA;

public abstract class ParentActivity extends AppCompatActivity {
    private static final String TAG = "BEL.base";

    @Override
    protected void attachBaseContext(Context new_base) {
        super.attachBaseContext(LocaleHelper.setLocale(new_base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // resetTitles();
    }

    private void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
