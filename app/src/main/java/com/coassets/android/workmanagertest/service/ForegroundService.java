package com.coassets.android.workmanagertest.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-20
 * UseDes:
 */
public class ForegroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ComponentName comp = new ComponentName("com.coassets.android.browserfiltertest", "com.coassets.android.browserfiltertest.MainActivity");
        Intent intent1 = new Intent();
        intent1.setComponent(comp);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent1);

        Log.d("ForegroundService","onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
