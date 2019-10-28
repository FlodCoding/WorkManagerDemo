package com.coassets.android.workmanagertest.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.coassets.android.workmanagertest.utils.CalendarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-22
 * UseDes:
 *
 */
class CalendarEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        context ?: return
        intent ?: return


        //TODO 不可执行耗时操作，是否需要开启Service
        val startTime = intent.data?.lastPathSegment?.toLongOrNull() ?: return

        Log.d("start_time", "BroadcastReceiver:$startTime")

        val eventIds = CalendarUtil.queryEventIdByTime(context, startTime.toString())
        if (eventIds.isEmpty()) return


        //TODO 判断当前是否是熄屏状态，然后唤醒亮屏再执行解锁手势





        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context.applicationContext, "我活了", Toast.LENGTH_SHORT).show()
            }
        }


    }

}