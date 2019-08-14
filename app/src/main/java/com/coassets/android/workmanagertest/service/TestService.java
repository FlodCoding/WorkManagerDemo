package com.coassets.android.workmanagertest.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.fanjun.keeplive.config.KeepLiveService;

import java.util.Timer;
import java.util.TimerTask;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-14
 * UseDes:
 */
public class TestService implements KeepLiveService {
    int callTime=0;
    @Override
    public void onWorking() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                callTime++;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TestService", "我还活着:" + callTime);
                        //Toast.makeText(getApplicationContext(), "我还活着:" + callTime, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,
                1000,//延迟1秒执行
                3000);//周期时间
    }

    @Override
    public void onStop() {

    }
}
