package com.brzimetrokliziretro.notebook.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.brzimetrokliziretro.notebook.AlarmActivity;
import com.brzimetrokliziretro.notebook.database.NotesData;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ONE_TIME = "onetime";
    public static final String SENT_ID = "ids_alarm_rec";
    public static final String SENT_TIME = "time";
    public static final int NOTIFICATION_ID = 111;
    public static final String ALARM_TYPE = "type";
    public static final String SNOOZE_ID = "snooze_alarm_rec";
    public static final String TAG = "bel.alarm_rec";
    public static final String ALARM_TITLE = "title_alarm_rec";

    private NotesData mNotesData;
    private int received_id = 0;
    private String days = "";
    private int snooze = 0;
    private String title = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG:wakelock");

        wl.acquire();
        mNotesData = new NotesData(context);

        try {
            Bundle bd = intent.getExtras();
            received_id = bd.getInt("ids", 2);
            days = bd.getString("days", "");
            Log.d(TAG, "chosen days after getting bundle: " + days);
            snooze = bd.getInt("snooze", 0);
            title = bd.getString("title", "wake up");
            Log.d(TAG, "id in alarm rec: " + received_id);
        }catch(Exception e){
            e.getMessage();
            Log.e(TAG, "Error while getting bundle extras: " + e.getMessage());
            received_id = 0;
            days = "";
            snooze = 0;
        }
        if(!TextUtils.isEmpty(days)) {
            String[] chosen_days = days.split("Q");
            Calendar cal = Calendar.getInstance();

            int today = cal.get(Calendar.DAY_OF_WEEK);
            if(chosen_days[today - 1].equalsIgnoreCase("true")){
                startAlarm(context);
                wl.release();
            }
        }else {
            startAlarm(context);
            wl.release();
        }
    }

    private void startAlarm(Context mContext){

        Intent inti = new Intent(mContext, AlarmActivity.class);
        inti.putExtra(SENT_ID, received_id);
        inti.putExtra(ALARM_TITLE, title);
        Log.d(TAG, "id is: " + received_id + " title: " + title);
        if(snooze == 1){
            inti.putExtra(SNOOZE_ID, 1);
        }else{
            inti.putExtra(SNOOZE_ID, 0);
        }
        mContext.startActivity(inti);
    }
}

