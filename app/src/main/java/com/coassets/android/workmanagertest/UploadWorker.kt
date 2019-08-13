package com.coassets.android.workmanagertest

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-13
 * UseDes:
 *
 */
class UploadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    @ExperimentalCoroutinesApi
    override fun doWork(): Result {
        //开始任务
        Log.d("UploadWorker", "我开始干活了")
        makeStatusNotification("我开始干活了", applicationContext)

        return Result.success();
    }
}