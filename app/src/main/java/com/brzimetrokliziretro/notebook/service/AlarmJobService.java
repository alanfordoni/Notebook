package com.brzimetrokliziretro.notebook.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.brzimetrokliziretro.notebook.broadcast.AlarmReceiver;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AlarmJobService extends JobIntentService {
    public static final int UNIQUE_JOB_ID = 111;
    public static final String ID = "ids";
    public static final String CHOSEN_DAYS = "days";
    public static final String ALARM_TITLE = "title";
    private int id_res;
    private long time_res;
    private NotesData mNotesData;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AlarmJobService.class, UNIQUE_JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        try {
            mNotesData = new NotesData(this);
            List<ModelMain> ids_list = new ArrayList<ModelMain>();
            ids_list.addAll(mNotesData.getAlarmNotesID());
            Calendar cal = Calendar.getInstance();
            int[] id = new int[ids_list.size()];
            long[] time = new long[ids_list.size()];

            for (int i = 0; i < ids_list.size(); i++) {
                id[i] = (int) ids_list.get(i).getId();
                time[i] = ids_list.get(i).getTime();
            }

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarm_intent = new Intent(AlarmJobService.this, AlarmReceiver.class);
            for (int i = 0; i < ids_list.size(); i++) {
                alarm_intent.putExtra(ID, id[i]);
                alarm_intent.putExtra(CHOSEN_DAYS, ids_list.get(i).getRepeatOrder());
                alarm_intent.putExtra(ALARM_TITLE, ids_list.get(i).getTitle());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmJobService.this, id[i], alarm_intent ,PendingIntent.FLAG_UPDATE_CURRENT );
                if(ids_list.get(i).getAlarm() == 2){
                    Calendar cal_two = GregorianCalendar.getInstance();
                    cal_two.setTimeInMillis(time[i]);
                    int hours = cal_two.get(Calendar.HOUR_OF_DAY);
                    int minutes = cal_two.get(Calendar.MINUTE);
                    cal_two.setTimeInMillis(System.currentTimeMillis());
                    cal_two.set(Calendar.HOUR_OF_DAY, hours);
                    cal_two.set(Calendar.MINUTE, minutes);
                    if(cal_two.getTimeInMillis() >= System.currentTimeMillis()) {
                        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal_two.getTimeInMillis()
                                , 24 * 60 * 60 * 1000, pendingIntent);
                    }else {
                        manager.setRepeating(AlarmManager.RTC_WAKEUP,
                                cal_two.getTimeInMillis() + 24 * 60 * 60 * 1000
                                , 24 * 60 * 60 * 1000, pendingIntent);
                    }
                }else {
                    if (ids_list.get(i).getTime() > System.currentTimeMillis()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time[i], pendingIntent);
                        }else{
                            manager.setExact(AlarmManager.RTC_WAKEUP, time[i], pendingIntent);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        stopSelf();
    }

}
