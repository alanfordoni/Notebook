package com.brzimetrokliziretro.notebook.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.brzimetrokliziretro.notebook.service.AlarmJobService;



public class AlarmResetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:TAG");
            // Acquire the lock
            wl.acquire();
            AlarmJobService.enqueueWork(context, intent);
            // Release the lock
            wl.release();
        }
    }
}
