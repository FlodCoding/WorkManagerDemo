package com.coassets.android.workmanagertest.keepalive.job;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-14
 * UseDes:
 */
public class AliveJobService extends JobService {


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("AliveJobService", "onStartJob");
        Toast.makeText(this,"onStartJob",Toast.LENGTH_SHORT).show();

        //如果返回true 需要手动触发jobFinished来结束当前任务
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("AliveJobService", "onStopJob");
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        JobScheduler mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(startId,
                new ComponentName(getPackageName(), AliveJobService.class.getName()));
        builder.setPeriodic((1000 * 60*15));//设置间隔时间
        builder.setPersisted(true);//设备重启之后你的任务是否还要继续执行

        if (mJobScheduler.schedule(builder.build()) == JobScheduler.RESULT_SUCCESS) {
            Log.e("AliveJobService", "onStartCommand成功");
        } else {
            Log.e("AliveJobService", "onStartCommand失败");
        }

        return START_STICKY;
    }


    // 服务是否运行
    public boolean isServiceRunning(String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        // 获取运行服务再启动
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            System.out.println(info.processName);
            if (info.processName.equals(serviceName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }
}
