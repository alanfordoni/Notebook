package com.brzimetrokliziretro.notebook;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.brzimetrokliziretro.notebook.helpers.LocaleHelper;

public class MainActivity extends ParentActivity {
    private static final String TAG = "BEL.mainactivity";
    private static long backPressedTime = 0;

    public static final String LANGUAGE = "language_key";
    public static final String THEME = "theme";
    public static final String ALARM_LENGTH = "a_length";
    public static final String SNOOZE_LENGTH = "s_length";
    public static final String ALARM_NUM = "alarm_num";
    public static final String SNOOZE_NUM = "snooze_num";

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int theme = mPrefs.getInt("theme", 2);
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(this, getSupportFragmentManager());

        if (theme == 0) {
            mViewPager.setBackground(ContextCompat.getDrawable(this, R.drawable.bluecurves));
        } else {
            mViewPager.setBackground(ContextCompat.getDrawable(this, R.drawable.violetcurves));
        }

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_archive) {
            startActivity(new Intent(this, ArchiveActivity.class));
            return true;
        } else if (id == R.id.calendar) {
            startActivity(new Intent(this, CalendarActivity.class));
            return true;
        } else if (id == R.id.menu_language) {
            setLanguageDialog();
        } else if (id == R.id.menu_theme) {
            setThemeDialog();
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        } else if (id == R.id.menu_alarm_length) {
            setAlarmLengthDialog();
        } else if (id == R.id.menu_snooze) {
            setSnoozeDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context new_base) {
        //super.attachBaseContext(LocaleHelper.setLocale(new_base));

        String language = LocaleHelper.getLanguage(new_base);
        super.attachBaseContext(MyContextWrapper.wrap(new_base, language));
    }

    private boolean changeLocale(String language) {
        LocaleHelper.setNewLocale(this, language);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        return true;
    }

    private boolean setLanguageDialog() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String old_selected_string = mPrefs.getString("language_key", "en");
        int old_selected = 0;
        switch (old_selected_string) {
            case "en":
                old_selected = 0;
                break;
            case "de":
                old_selected = 1;
                break;
            case "sr":
                old_selected = 2;
                break;
            case "hr":
                old_selected = 3;
                break;
            case "mk":
                old_selected = 4;
                break;
            default:
                old_selected = 0;
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_language);
        builder.setSingleChoiceItems(R.array.languages, old_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                switch (selected) {
                    case 0:
                        mPrefs.edit().putString(LANGUAGE, "en").apply();
                        changeLocale("en");
                        break;
                    case 1:
                        mPrefs.edit().putString(LANGUAGE, "de").apply();
                        changeLocale("de");
                        break;

                    case 2:
                        mPrefs.edit().putString(LANGUAGE, "sr").apply();
                        changeLocale("sr");
                        break;

                    case 3:
                        mPrefs.edit().putString(LANGUAGE, "hr").apply();
                        changeLocale("hr");
                        break;
                    case 4:
                        mPrefs.edit().putString(LANGUAGE, "mk").apply();
                        changeLocale("mk");
                    default:
                        break;
                }

                dialog.dismiss();

                Log.d(TAG, "onClick: selected lan: " + mPrefs.getString(LANGUAGE, "default"));
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private boolean setAlarmLengthDialog() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        int old_selected = mPrefs.getInt("alarm_num", 3);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_alarm_length);
        builder.setSingleChoiceItems(R.array.alarm_length, old_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                switch (selected) {
                    case 0:
                        mPrefs.edit().putInt(ALARM_LENGTH, 15 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 0).apply();
                        dialog.dismiss();
                        break;
                    case 1:
                        mPrefs.edit().putInt(ALARM_LENGTH, 30 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 1).apply();

                        dialog.dismiss();
                        break;
                    case 2:
                        mPrefs.edit().putInt(ALARM_LENGTH, 60 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 2).apply();

                        dialog.dismiss();
                        break;
                    case 3:
                        mPrefs.edit().putInt(ALARM_LENGTH, 3 * 60 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 3).apply();
                        dialog.dismiss();
                        break;
                    case 4:
                        mPrefs.edit().putInt(ALARM_LENGTH, 5 * 60 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 4).apply();
                        dialog.dismiss();
                        break;
                    case 5:
                        mPrefs.edit().putInt(ALARM_LENGTH, 10 * 60 * 1000).apply();
                        mPrefs.edit().putInt(ALARM_NUM, 5).apply();
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private boolean setSnoozeDialog() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        int old_selected = mPrefs.getInt("snooze_num", 2);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_snooze_length);
        builder.setSingleChoiceItems(R.array.snooze_length, old_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                switch (selected) {
                    case 0:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 3 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 0).apply();
                        dialog.dismiss();
                        break;
                    case 1:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 5 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 1).apply();
                        dialog.dismiss();
                        break;
                    case 2:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 10 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 2).apply();
                        dialog.dismiss();
                        break;
                    case 3:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 15 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 3).apply();
                        dialog.dismiss();
                        break;
                    case 4:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 30 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 4).apply();
                        dialog.dismiss();
                        break;
                    case 5:
                        mPrefs.edit().putInt(SNOOZE_LENGTH, 60 * 60 * 1000).apply();
                        mPrefs.edit().putInt(SNOOZE_NUM, 5).apply();
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return true;
    }

    private boolean setThemeDialog() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final int old_selected = mPrefs.getInt("theme", 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_theme);
        builder.setSingleChoiceItems(R.array.themes, old_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selected = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                switch (selected) {
                    case 0:
                        if (old_selected != 0) {
                            mPrefs.edit().putInt(THEME, 0).apply();
                            recreate();
                        }
                        dialog.dismiss();
                        break;
                    case 1:
                        if (old_selected != 1) {
                            mPrefs.edit().putInt(THEME, 1).apply();
                            recreate();
                        }
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 3000) {
            backPressedTime = t;
            Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
