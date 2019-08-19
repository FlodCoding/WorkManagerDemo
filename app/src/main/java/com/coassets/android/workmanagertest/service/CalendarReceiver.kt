package com.coassets.android.workmanagertest.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.coassets.android.workmanagertest.MainActivity
import com.coassets.android.workmanagertest.makeStatusNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-15
 * UseDes:
 *
 */
class CalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("example", "Calendar changed (or the phone just booted)")

        //val comp = ComponentName("com.coassets.android.browserfiltertest", "com.coassets.android.browserfiltertest.MainActivity")
        val alarms = Intent(context,MainActivity::class.java)
        alarms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.applicationContext.startActivity(alarms)

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                makeStatusNotification("shit",context!!.applicationContext)
                Toast.makeText(context!!.applicationContext, "我活了", Toast.LENGTH_SHORT).show()


            }
        }


    }
}