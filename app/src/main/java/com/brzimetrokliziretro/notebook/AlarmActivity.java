package com.brzimetrokliziretro.notebook;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brzimetrokliziretro.notebook.broadcast.AlarmReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmActivity extends ParentActivity {
    public static final String TAG = "bel.alarm";
    public static final String USED_ID = "ids";
    public static final String DAYS = "days";
    public static final String SNOOZE_ON = "snooze";
    public static final String TITLE = "title";

    private Ringtone ringtone;
    private TextView tvTitle;
    private Button stop;
    private ImageButton snooze;
    private AlarmManager manager;
    private Intent alarmIntent;
    private Vibrator vibrator;
    private boolean timerState;
    private Timer mTimer;
    private Thread th;

    private int snoozeId = 0;
    private int id = 0;
    private int alarmType = 0;
    private String title;

    public AlarmActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, AlarmReceiver.class);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        timerState = false;
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        vibrate();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timerState = true;
                if (!ringtone.isPlaying()) {
                    ringtone.play();
                }
            }
        }, 1000*1, 1000*1);

        startShortTimer();

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null) {
            id = bd.getInt("ids_alarm_rec", 0);
            snoozeId = bd.getInt("snooze_alarm_rec", 0);
            title = bd.getString("title_alarm_rec", "wake up");

            Log.d(TAG, "onCreate id is: " + id);
            tvTitle = (TextView)findViewById(R.id.text_title);
            tvTitle.setText(title);
        }

        stop = (Button)findViewById(R.id.stop_alarm);
        snooze = (ImageButton)findViewById(R.id.btn_snooze);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.cancel();
                ringtone.stop();
                vibrator.cancel();
                try{
                    th.interrupt();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    if (alarmType == 1) {
                        if (snoozeId == 1){
                            id = 0;
                        }
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, id, alarmIntent, 0);
                        manager.cancel(pendingIntent);
                        Log.d(TAG, "onStop try if id is: " + id);
                        clearFlags();
                        finish();
                        backToMain();


                    } else {
                        Log.d(TAG, "onStop else id is: " + id);
                        clearFlags();
                        finish();
                        backToMain();
                    }
                }catch (Exception e){
                    Log.e(TAG, "problem with stopping alarm");
                    clearFlags();
                    finish();
                    backToMain();

                }

            }
        });

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSnooze();
                mTimer.cancel();
                mTimer.purge();
                ringtone.stop();
                vibrator.cancel();

                try{
                    th.interrupt();
                }catch (Exception e){
                    e.printStackTrace();
                }

                clearFlags();
                finish();
                backToMain();

            }
        });
    }

    private void startShortTimer(){
        th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AlarmActivity.this);
                    int ring_length = sp.getInt("a_length", 60 * 1000);
                    Thread.sleep(ring_length);
                    if (timerState) {
                        mTimer.cancel();
                        mTimer.purge();
                        ringtone.stop();
                        vibrator.cancel();
                        timerState = false;
                        startSnooze();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }

    private void startSnooze(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AlarmActivity.this);
        int snooze_time = sp.getInt("s_length", 600 * 1000);

        alarmIntent.putExtra(USED_ID, id);
        alarmIntent.putExtra(DAYS, "");
        alarmIntent.putExtra(SNOOZE_ON, 1);
        alarmIntent.putExtra(TITLE, title);
        PendingIntent pendingIntentSno = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + snooze_time, pendingIntentSno);
    }

    private void vibrate() {
        if (vibrator != null) {
            vibrator.vibrate(new long[] {500, 500}, 0);
        }
    }

    private void clearFlags(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void backToMain(){
        Intent intent = new Intent(AlarmActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
