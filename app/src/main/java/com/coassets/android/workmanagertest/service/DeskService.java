package com.coassets.android.workmanagertest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-13
 * UseDes:
 */
public class DeskService extends Service {

    private static final String TAG = "DaemonService";
    public static final int NOTICE_ID = 100;
    private int callTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d(TAG, "DaemonService---->onCreate被调用，启动前台service");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("12", "app", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this, "12");

            builder.setContentTitle("KeepAppAlive");
            builder.setContentText("DaemonService is runing...");
            startForeground(NOTICE_ID, builder.build());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);


            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    callTime++;
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "我还活着:" + callTime, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask,
                    1000,//延迟1秒执行
                    3000);//周期时间

        } else {
            startForeground(NOTICE_ID, new Notification());
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(NOTICE_ID);
        }
        Log.d(TAG, "DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), DeskService.class);
        startService(intent);
    }

}