package com.coassets.android.workmanagertest.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-15
 * UseDes:
 *
 */
class CalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("example", "Calendar changed (or the phone just booted)");
    }
}