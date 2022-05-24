package com.brzimetrokliziretro.notebook;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.brzimetrokliziretro.notebook.broadcast.AlarmReceiver;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


public class NoteActivity extends ParentActivity {
    public static final String TAG = "BEL.noteactivity";
    public static final String ALARM_TIME = "time";
    public static final String ALARM_ID = "ids";
    public static final String ALARM_REPEAT = "days";
    public static final String ALARM_TYPE = "type";
    public static final String ALARM_TITLE = "title";

    private CheckBox cbox_personal, cbox_business;
    private Button btn_save, btn_date, btn_time;
    private TextView tv_alarm;
    private RadioGroup rg_alarm_type;
    private Switch swAlarm;
    private TextInputEditText tiet_note, tiet_note_title;
    private NotesData mNotesData;
    private ModelMain mModel;
    private GregorianCalendar mCal;

    private static int selectedId;
    private long saved_id;
    private int new_id;
    private static int exit_id;
    private boolean[] chosen_days;
    private int rv_parent_num;
    private String temp_date;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int note_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int theme = prefs.getInt("theme", 1);

        if (theme == 0) {
            setContentView(R.layout.activity_note_simple);
        } else {
            setContentView(R.layout.activity_note);

        }

        temp_date = "";
        selectedId = 0;
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_add_note);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            saved_id = bd.getLong("extra_id", 0);
            rv_parent_num = bd.getInt("rv_parent", 0);
            note_type = bd.getInt("note_type", 0);
        }
        chosen_days = new boolean[7];
        cbox_business = (CheckBox) findViewById(R.id.cbox2);
        cbox_personal = (CheckBox) findViewById(R.id.cbox);
        swAlarm = findViewById(R.id.sw_alarm);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_date = (Button) findViewById(R.id.btn_add_note_date);
        btn_time = (Button) findViewById(R.id.btn_add_note_time);
        tv_alarm = (TextView) findViewById(R.id.tv_alarm);
        rg_alarm_type = (RadioGroup) findViewById(R.id.radio_group_alarm);
        tiet_note = (TextInputEditText) findViewById(R.id.tiet_input_text);
        tiet_note_title = (TextInputEditText) findViewById(R.id.tiet_input_title);

        mCal = new GregorianCalendar();
        mCal.setTimeInMillis(GregorianCalendar.getInstance().getTimeInMillis());
        mYear = mCal.get(GregorianCalendar.YEAR);
        mMonth = mCal.get(GregorianCalendar.MONTH);
        mDay = mCal.get(GregorianCalendar.DAY_OF_MONTH);
        mHour = mCal.get(GregorianCalendar.HOUR_OF_DAY);
        mMinute = mCal.get(GregorianCalendar.MINUTE);

        cbox_personal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    cbox_business.setChecked(false);
                }
            }
        });
        cbox_business.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    cbox_personal.setChecked(false);
                }
            }
        });

        if (note_type == 1) {
            cbox_personal.setChecked(true);
        } else if (note_type == 2) {
            cbox_business.setChecked(true);
        }

        rg_alarm_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case (R.id.rb_once):
                        selectedId = 1;
                        updateDateButton(temp_date);
                        break;
                    case (R.id.rb_weekly):
                        selectedId = 2;
                        updateDateButton(temp_date);
                        break;
                }

            }
        });
        mModel = new ModelMain();
        mNotesData = new NotesData(this);

        if (saved_id != 0) {
            mModel = mNotesData.getToDo(saved_id);
            openNote();
        } else {
            swAlarm.setChecked(false);
            rg_alarm_type.clearCheck();
            rg_alarm_type.setVisibility(View.INVISIBLE);
            updateDateAndTimeButtons();
        }

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateSet();
            }
        });
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSet();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rv_parent_num == 5) {
                    Toast.makeText(NoteActivity.this, R.string.archived_items_message, Toast.LENGTH_SHORT).show();
                } else {
                    saveNote();
                }
            }
        });

        swAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rg_alarm_type.setVisibility(View.VISIBLE);
                    btn_date.setVisibility(View.VISIBLE);
                    btn_time.setVisibility(View.VISIBLE);
                } else {
                    rg_alarm_type.setVisibility(View.INVISIBLE);
                    rg_alarm_type.clearCheck();
                    btn_date.setVisibility(View.INVISIBLE);
                    btn_time.setVisibility(View.INVISIBLE);
                    selectedId = 0;
                }
            }
        });
    }

    private void dateSet() {
        final boolean[] days = new boolean[7];
        if (selectedId != 2) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
            String lan = sp.getString("language_key", "en");
            if (lan.equalsIgnoreCase("sr") || lan.equalsIgnoreCase("hr")) {
                Locale.setDefault(Locale.forLanguageTag("bs"));
            }
            final Calendar c = GregorianCalendar.getInstance();
            final int year = c.get(Calendar.YEAR);
            final int month = c.get(Calendar.MONTH);
            final int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(NoteActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar cal = GregorianCalendar.getInstance();
                            NoteActivity.this.mDay = day;
                            NoteActivity.this.mMonth = month;
                            NoteActivity.this.mYear = year;
                            cal.set(GregorianCalendar.DAY_OF_MONTH, NoteActivity.this.mDay);
                            cal.set(GregorianCalendar.MONTH, NoteActivity.this.mMonth);
                            cal.set(GregorianCalendar.YEAR, NoteActivity.this.mYear);

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
                            String language = sp.getString("language_key", "en");
                            DateTime dt = new DateTime(cal.getTime());
                            String chosen_date = "";
                            if (language.equalsIgnoreCase("sr") || language.equalsIgnoreCase("hr")) {
                                chosen_date = dt.toString("EEE   dd.MM.yyyy", Locale.forLanguageTag("hr"));
                            } else {
                                chosen_date = dt.toString("EEE   dd.MM.yyyy");
                            }
                            //   SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy");
                            //   String chosen_date = sdf.format(cal.getTime());
                            btn_date.setText(chosen_date);
                            temp_date = chosen_date;
                        }
                    }, year, month, day);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), datePickerDialog);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);

            datePickerDialog.show();
        } else {
            btn_date.setText(R.string.week_days);
            for (int i = 0; i < 7; i++) {
                days[i] = chosen_days[i];
                //   Log.d(TAG, "chosen days in dateSet(): " + days[0] + days[1] + days[2]
                //   + days[3] + days[4] + days[5] + days[6]);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(NoteActivity.this);
            builder.setTitle(R.string.choose_days);
            builder.setMultiChoiceItems(R.array.week_days_names, days, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                }
            });
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    for (int i = 0; i < days.length; i++) {
                        chosen_days[i] = days[i];
                    }
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void timeSet() {

        Calendar c = GregorianCalendar.getInstance();
        int hour = 0;
        int minute = 0;
        if (saved_id != 0) {
            hour = this.mHour;
            minute = this.mMinute;
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
        final TimePickerDialog timePickerDialog = new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar cal = GregorianCalendar.getInstance();
                NoteActivity.this.mMinute = minute;
                NoteActivity.this.mHour = hourOfDay;
                Log.d(TAG, "min: " + NoteActivity.this.mMinute);
                cal.set(Calendar.HOUR_OF_DAY, NoteActivity.this.mHour);
                cal.set(Calendar.MINUTE, NoteActivity.this.mMinute);
                cal.set(Calendar.SECOND, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String chosen_time = sdf.format(cal.getTime());
                btn_time.setText(chosen_time);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(R.string.select_time_add_note);
        timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), timePickerDialog);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), timePickerDialog);
        timePickerDialog.show();
    }

    private void openNote() {
        invalidateOptionsMenu();
        tiet_note_title.setText(mModel.getTitle());
        tiet_note.setText(mModel.getNote());
        Log.d(TAG, "opennote min: " + mMinute);

        if (mModel.getTime() != 0) {
            swAlarm.setChecked(true);
            rg_alarm_type.setVisibility(View.VISIBLE);
            Calendar calen = GregorianCalendar.getInstance();

            if (mModel.getAlarm() == 2) {
                rg_alarm_type.check(R.id.rb_weekly);
                btn_date.setText(R.string.week_days);
                String week_days = mModel.getRepeatOrder();
                String[] week_days_split = week_days.split("Q");
                for (int i = 0; i < 7; i++) {
                    chosen_days[i] = Boolean.valueOf(week_days_split[i]);
                }
                calen.setTimeInMillis(mModel.getTime());
                mYear = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
                mMonth = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH);
                mDay = GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH);

            } else if (mModel.getAlarm() == 1) {
                rg_alarm_type.check(R.id.rb_once);
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
                String language = sp.getString("language_key", "en");
                DateTime dt = new DateTime(mModel.getTime());
                String saved_state = "";
                if (language.equalsIgnoreCase("sr") || language.equalsIgnoreCase("hr")) {
                    saved_state = dt.toString("EEE   dd.MM.yyyy", Locale.forLanguageTag("hr"));
                } else {
                    saved_state = dt.toString("EEE   dd.MM.yyyy");
                }
                //   SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy");
                //   String saved_state = sdf.format(mModel.getTime());
                btn_date.setText(saved_state);
                calen.setTimeInMillis(mModel.getTime());
                mYear = calen.get(GregorianCalendar.YEAR);
                mMonth = calen.get(GregorianCalendar.MONTH);
                mDay = calen.get(GregorianCalendar.DAY_OF_MONTH);
                temp_date = saved_state;
            }
            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
            String saved_time = sdf_time.format(mModel.getTime());
            btn_time.setText(saved_time);
            mHour = calen.get(GregorianCalendar.HOUR_OF_DAY);
            mMinute = calen.get(GregorianCalendar.MINUTE);
            Log.d(TAG, "opennote 2.5 min: " + mMinute);


        } else {
            swAlarm.setChecked(false);
            rg_alarm_type.clearCheck();
            rg_alarm_type.setVisibility(View.INVISIBLE);
            updateDateAndTimeButtons();
            temp_date = DateTime.now().toString("EEE   dd.MM.yyyy");
        }
        if (mModel.getNoteType() == 1) {
            cbox_personal.setChecked(true);
        } else {
            cbox_business.setChecked(true);
        }
        Log.d(TAG, "opennote 3 min: " + mMinute);
    }

    private int addNote(String title, String note, int note_type, long alarm_time) {
        int type = 1;

        ModelMain modelMain = new ModelMain();
        modelMain.setViewType(type);
        modelMain.setNote(note);
        modelMain.setNoteType(note_type);
        modelMain.setTitle(title);
        if (selectedId == 1) {
            modelMain.setTime(alarm_time);
            modelMain.setAlarm(1);
            modelMain.setRepeatOrder(null);
        } else if (selectedId == 2) {
            modelMain.setTime(alarm_time);
            modelMain.setAlarm(2);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < chosen_days.length; i++) {
                String day = String.valueOf(chosen_days[i]);
                builder.append(day + "Q");
            }
            String days = builder.toString();
            modelMain.setRepeatOrder(days);
        } else {
            modelMain.setTime(0);
            modelMain.setAlarm(0);
            modelMain.setRepeatOrder(null);
        }
        mNotesData = new NotesData(this);
        mNotesData.addToDo(modelMain);
        new_id = (int) mNotesData.getLastToDo().getId();
        return new_id;
    }

    private void updatenote(String title, String note, int note_type, long alarm_time) {
        int type = 1;
        ModelMain modelMain = new ModelMain();
        modelMain.setViewType(type);
        modelMain.setNote(note);
        modelMain.setNoteType(note_type);
        modelMain.setTitle(title);
        if (selectedId == 1) {
            modelMain.setTime(alarm_time);
            modelMain.setAlarm(1);
            modelMain.setRepeatOrder("");
        } else if (selectedId == 2) {
            modelMain.setTime(alarm_time);
            modelMain.setAlarm(2);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < chosen_days.length; i++) {
                String day = String.valueOf(chosen_days[i]);
                builder.append(day + "Q");
            }
            String days = builder.toString();
            modelMain.setRepeatOrder(days);
        } else {
            if (saved_id > 0 && mModel.getTime() > 0) {
                int cancel_id = (int) saved_id;
                Intent cancel_intent = new Intent(NoteActivity.this, AlarmReceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(NoteActivity.this, cancel_id, cancel_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) NoteActivity.this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pi);
            }
            modelMain.setTime(0);
            modelMain.setAlarm(0);
            modelMain.setRepeatOrder("");
        }
        mNotesData = new NotesData(this);
        mNotesData.updateNote(saved_id, modelMain);
    }

    private boolean saveNote() {

        String add_title = "";
        String add_note = "";
        int note_type = 0;
        long time_new = 0;
        long time_repeat = 0;
        boolean test = false;
        Log.d(TAG, "min: " + mMinute);

        Calendar gc = GregorianCalendar.getInstance();
        gc.set(GregorianCalendar.YEAR, mYear);
        gc.set(GregorianCalendar.MONTH, mMonth);
        gc.set(GregorianCalendar.DAY_OF_MONTH, mDay);
        gc.set(GregorianCalendar.HOUR_OF_DAY, mHour);
        gc.set(GregorianCalendar.MINUTE, mMinute);
        gc.set(GregorianCalendar.SECOND, 0);
        time_new = gc.getTimeInMillis();
        Calendar rc = GregorianCalendar.getInstance();
        rc.setTimeInMillis(0);
        rc.set(GregorianCalendar.HOUR_OF_DAY, mHour);
        rc.set(GregorianCalendar.MINUTE, mMinute);
        time_repeat = rc.getTimeInMillis();

        Log.d(TAG, "time_rep is: " + time_repeat);

        Log.d(TAG, "AlarmActivity is not set" + time_new);

        Log.d(TAG, "min: " + mMinute);


        if (TextUtils.isEmpty(tiet_note_title.getText().toString())) {
            Toast.makeText(NoteActivity.this, R.string.add_note_title_warning, Toast.LENGTH_SHORT).show();

        } else if (cbox_personal.isChecked() && cbox_business.isChecked()
                || !cbox_personal.isChecked() && !cbox_business.isChecked()) {
            Toast.makeText(NoteActivity.this, R.string.add_note_checkbox_warning, Toast.LENGTH_SHORT).show();
        } else if (swAlarm.isChecked() && rg_alarm_type.getCheckedRadioButtonId() == -1) {
            Toast.makeText(NoteActivity.this, R.string.choose_alarm_type, Toast.LENGTH_SHORT).show();
        } else {
            if (selectedId == 2) {
                for (boolean b : chosen_days) {
                    if (b) {
                        test = true;
                        break;
                    }

                    Log.d(TAG, "saveNote: test = " + test);
                }
                if (!test) {
                    Toast.makeText(this, R.string.week_days_warning, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            add_title = tiet_note_title.getText().toString();

            if (TextUtils.isEmpty(tiet_note.getText().toString())) {
                // unos beleske nije obavezan jer u slucaju alarma naslov je dovoljan
                tiet_note.setText("");
                add_note = tiet_note.getText().toString();
            } else {
                add_note = tiet_note.getText().toString();
            }
            if (cbox_personal.isChecked()) {
                note_type = 1;
            } else if (cbox_business.isChecked()) {
                note_type = 2;
            }

            if (saved_id != 0) {
                if (selectedId == 1) {
                    Log.d(TAG, "chosen time is: " + time_new);
                    updatenote(add_title, add_note, note_type, time_new);
                } else if (selectedId == 2) {
                    Log.d(TAG, "saveNote: poavljajuci");
                    updatenote(add_title, add_note, note_type, time_repeat);
                } else {
                    Log.d(TAG, "saveNote: iskljucen alarm");
                    updatenote(add_title, add_note, note_type, 0);
                }
            } else {
                if (selectedId == 1) {
                    Log.d(TAG, "saveNote: jednokratan alarm");
                    addNote(add_title, add_note, note_type, time_new);
                } else if (selectedId == 2) {
                    Log.d(TAG, "saveNote: ponavljajuci alarm");
                    addNote(add_title, add_note, note_type, time_repeat);
                } else {
                    Log.d(TAG, "saveNote: iskljucen alarm");
                    addNote(add_title, add_note, note_type, 0);
                }
            }
            Log.d(TAG, "savenote min: " + mMinute);
            if (time_new != 0) {
                int ids;
                ids = (int) saved_id;
                Log.d(TAG, "saved_id: " + ids);
                if (ids == 0) {
                    ids = new_id;
                }
                if (selectedId == 1) {
                    if (time_new > GregorianCalendar.getInstance().getTimeInMillis()) {
                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent alarm_intent = new Intent(NoteActivity.this, AlarmReceiver.class);
                        alarm_intent.putExtra(ALARM_ID, ids);
                        alarm_intent.putExtra(ALARM_REPEAT, "");
                        alarm_intent.putExtra(ALARM_TITLE, add_title);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ids, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time_new, pendingIntent);
                        } else {
                            manager.setExact(AlarmManager.RTC_WAKEUP, time_new, pendingIntent);
                        }
                        Log.d(TAG, "AlarmActivity is set" + time_new + " id: " + ids);
                    }
                } else if (selectedId == 2) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < chosen_days.length; i++) {
                        String day = String.valueOf(chosen_days[i]);
                        builder.append(day + "Q");
                    }
                    String days = builder.toString();

                    Log.d(TAG, "days in save note: " + days);

                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent alarm_intent = new Intent(NoteActivity.this, AlarmReceiver.class);
                    alarm_intent.putExtra(ALARM_ID, ids);
                    alarm_intent.putExtra(ALARM_REPEAT, days);
                    alarm_intent.putExtra(ALARM_TITLE, add_title);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(NoteActivity.this, ids, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar cal_repeat = GregorianCalendar.getInstance();
                    cal_repeat.setTimeInMillis(System.currentTimeMillis());
                    cal_repeat.set(GregorianCalendar.MINUTE, mMinute);
                    cal_repeat.set(GregorianCalendar.HOUR_OF_DAY, mHour);
                    long cal_time = cal_repeat.getTimeInMillis();
                    if (cal_time <= GregorianCalendar.getInstance().getTimeInMillis()) {
                        manager.setRepeating(AlarmManager.RTC_WAKEUP
                                , cal_time + 24 * 60 * 60 * 1000
                                , 24 * 60 * 60 * 1000, pendingIntent);
                        Log.d(TAG, "Repeating 1 alarm is set: " + cal_time + " id: " + ids
                                + " current time: " + GregorianCalendar.getInstance().getTimeInMillis());
                    } else {
                        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal_time, 24 * 60 * 60 * 1000, pendingIntent);
                        Log.d(TAG, "Repeating 2 alarm is set " + cal_time + " id: " + ids
                                + " current time: " + GregorianCalendar.getInstance().getTimeInMillis());
                    }
                }
            }
            Log.d(TAG, "saveNote: selectedID = " + selectedId);

            backToMain();

        }
        return true;
    }

    private void updateDateAndTimeButtons() {
        long update_time = GregorianCalendar.getInstance().getTimeInMillis();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
        String language = sp.getString("language_key", "en");
        DateTime dt = new DateTime(update_time);
        String date = "";
        if (language.equalsIgnoreCase("sr") || language.equalsIgnoreCase("hr")) {
            date = dt.toString("EEE   dd.MM.yyyy", Locale.forLanguageTag("hr"));
        } else {
            date = dt.toString("EEE   dd.MM.yyyy");
        }

        if (selectedId != 2) {
            //  SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy");
            //  String date = sdf.format(update_time);
            btn_date.setText(date);
        } else {
            btn_date.setText(getString(R.string.week_days));
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String current_time = sdf2.format(update_time);
        btn_time.setText(current_time);
    }

    private void updateDateButton(String date_o) {
        long update_time = GregorianCalendar.getInstance().getTimeInMillis();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
        String language = sp.getString("language_key", "en");
        DateTime dt = new DateTime(update_time);
        String date = "";
        if (language.equalsIgnoreCase("sr") || language.equalsIgnoreCase("hr")) {
            date = dt.toString("EEE   dd.MM.yyyy", Locale.forLanguageTag("hr"));
        } else {
            date = dt.toString("EEE   dd.MM.yyyy");
        }
        //  SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd.MM.yyyy");
        //  String date = sdf.format(update_time);
        if (selectedId != 2) {
            if (saved_id == 0) {
                if (TextUtils.isEmpty(date_o)) {
                    btn_date.setText(date);
                } else {
                    btn_date.setText(date_o);
                }
            } else {
                if (TextUtils.isEmpty(date_o)) {
                    try {
                        String date_saved = "";
                        DateTime dt_two = new DateTime(mModel.getTime());
                        if (language.equalsIgnoreCase("sr") || language.equalsIgnoreCase("hr")) {
                            date_saved = dt_two.toString("EEE   dd.MM.yyyy", Locale.forLanguageTag("hr"));
                        } else {
                            date_saved = dt_two.toString("EEE   dd.MM.yyyy");
                        }

                        //   String date_saved = sdf.format(mModel.getTime());
                        btn_date.setText(date_saved);
                    } catch (Exception e) {
                        btn_date.setText(date);
                    }
                } else {
                    btn_date.setText(date_o);
                }
            }
        } else {
            btn_date.setText(getString(R.string.week_days));
        }
    }

    public void backToMain() {
        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (saved_id > 0) {
            MenuItem item = menu.findItem(R.id.trash_sn);
            item.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        exit_id = 1;
        if (rv_parent_num == 5) {
            Intent intent = new Intent(NoteActivity.this, ArchiveActivity.class);
            startActivity(intent);

        } else if (rv_parent_num == 10) {
            Intent intent = new Intent(NoteActivity.this, CalendarActivity.class);
            startActivity(intent);

        } else {
            backToMain();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == R.id.trash_sn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_note_message);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (rv_parent_num == 5) {
                        mNotesData.deleteNoteById(saved_id);
                        Intent intent = new Intent(NoteActivity.this, ArchiveActivity.class);
                        startActivity(intent);
                    } else {
                        if (saved_id > 0) {
                            int cancel_id = (int) saved_id;
                            Intent cancel_intent = new Intent(NoteActivity.this, AlarmReceiver.class);
                            PendingIntent pi = PendingIntent.getBroadcast(NoteActivity.this, cancel_id, cancel_intent, 0);
                            AlarmManager alarmManager = (AlarmManager) NoteActivity.this.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pi);
                            mNotesData.deleteNoteById(saved_id);
                            backToMain();
                        }
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }
}