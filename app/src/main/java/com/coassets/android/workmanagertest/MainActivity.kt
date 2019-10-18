package com.coassets.android.workmanagertest

import android.app.KeyguardManager
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.coassets.android.workmanagertest.service.GestureAccessibility
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        fab.setOnClickListener { view ->
            Toast.makeText(applicationContext, "创建", Toast.LENGTH_SHORT).show()
            //addNewWork()
            // startForegroundService(Intent(this, DeskService::class.java))
            /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show()*/
            //startJobService()

            // startKeepLive()
            // createCalendar()

            //makeStatusNotification("shit",application)


            //val comp = ComponentName("com.coassets.android.browserfiltertest", "com.coassets.android.browserfiltertest.MainActivity")
            //val alarms = Intent().setComponent(comp)
            //alarms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            //startActivity(alarms)

            // createCalendar()
            //createCalendarAccount()

            /* val appName = getString(R.string.app_name)

             var calendarAccountId = getCalendarAccountByName(appName)
             if (calendarAccountId.isEmpty()) {
                 val id = createCalendarAccount(appName, "${appName}日历触发")
                 if (id != -1) {
                     calendarAccountId.add(id)
                 }
             }
             if (calendarAccountId.isNotEmpty()) {
                 createCalendar(calendarAccountId[0])
             }*/

            // deleteCalendarAccountByName(appName)

            //val calendarAccountId = createCalendarAccount(appName)
            //deleteCalendarAccountByName("DISPLAY_NAME")

            // createCalendar()

            //queryEvent()

            // createCalendar(5)
            /* view.postDelayed({
                 tryWakeUpAndUnlock(this)

             }, 5000)*/
            // unlockTest()

            confirmDeviceCredential()
            GestureAccessibility.startGestures(this, ArrayList())
          /*  view.postDelayed({
                GestureAccessibility.startGestures(this, ArrayList())

            }, 5000)*/
        }


        // startService(Intent(this, DeskService::class.java))


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun addNewWork() {
        val uploadWorkReq = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()


        WorkManager.getInstance(this).enqueue(uploadWorkReq)
    }


    private fun getCalendarAccountByName(accountName: String): ArrayList<Int> {
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME),
            "${CalendarContract.Calendars.ACCOUNT_NAME} = ?",
            arrayOf(accountName),
            null
        )

        val ids = ArrayList<Int>()

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                ids.add(cursor.getInt(0))
            }
            cursor.close()
        }
        return ids

    }


    fun createCalendar(CalendarId: Int) {
        val contentValues = ContentValues()

        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime.set(2019, 9, 15, 16, 43, 0)

        Log.d("Main", beginTime.time.toLocaleString())
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2019, 9, 15, 17, 20, 0)
        endMillis = endTime.timeInMillis

        Log.d("Main", endTime.time.toLocaleString())


        contentValues.put(CalendarContract.Events.CALENDAR_ID, CalendarId)
        contentValues.put(CalendarContract.Events.DTSTART, startMillis)
        contentValues.put(CalendarContract.Events.DTEND, startMillis)

        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        contentValues.put(CalendarContract.Events.TITLE, "任务")
        contentValues.put(CalendarContract.Events.DESCRIPTION, "丢雷楼某")

        //contentValues.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;WKST=SU;BYDAY=SU,MO;")


        val cr = contentResolver
        val eventId = cr.insert(CalendarContract.Events.CONTENT_URI, contentValues)?.lastPathSegment


        val contentValues2 = ContentValues()

        contentValues2.put(CalendarContract.Reminders.EVENT_ID, eventId)
        contentValues2.put(
            CalendarContract.Reminders.METHOD,
            CalendarContract.Reminders.METHOD_ALERT
        )
        contentValues2.put(CalendarContract.Reminders.MINUTES, CalendarContract.Reminders.MINUTES_DEFAULT)
        cr.insert(CalendarContract.Reminders.CONTENT_URI, contentValues2)
    }


    private fun createCalendarAccount(accountName: String, displayName: String): Int {
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName)
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName)
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1)
        @Suppress("DEPRECATION")
        contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, resources.getColor(R.color.colorPrimary))
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)

        var uri = CalendarContract.Calendars.CONTENT_URI

        uri = uri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
            .build()

        return contentResolver.insert(uri, contentValues)?.lastPathSegment?.toInt() ?: return -1
    }

    private fun deleteCalendarAccountByName(accountName: String): Boolean {
        return contentResolver.delete(
            CalendarContract.Calendars.CONTENT_URI,
            "${CalendarContract.Calendars.ACCOUNT_NAME} = ?",
            arrayOf(accountName)
        ) > 0
    }


    private fun queryEvent() {
        val INSTANCE_PROJECTION: Array<String> = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.STATUS,
            CalendarContract.Events.HAS_ALARM,
            CalendarContract.Events.DURATION
        )


        var cursor =
            contentResolver.query(CalendarContract.Events.CONTENT_URI, INSTANCE_PROJECTION, "${CalendarContract.Events.TITLE} = ?", arrayOf("555"), null)
                ?: return


        //eventID 80

        if (cursor.moveToFirst()) {
            cursor.getLong(0)


            cursor.close()
        }

    }


    fun wakeTest() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP, "DeviceUtil:wakeLock"
        )
        wakelock.acquire(30000)

        //wakelock.release()
    }

    fun unlockTest() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager


        val newKeyguardLock = keyguardManager.newKeyguardLock("DeviceUtil:keyguardManager")
        newKeyguardLock.disableKeyguard()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun tryWakeUpAndUnlock(context: Context) {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        /* if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&& km.isDeviceSecure)
             ||km.isKeyguardSecure
         ) return*/


        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //如果时熄屏,唤醒屏幕
        if (!pm.isInteractive) {
            val wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "DeviceUtil:WakeLock"
            )
            wakeLock.acquire(1000)
            wakeLock.release()
        }

        if (km.isKeyguardLocked)

            km.newKeyguardLock("DeviceUtil:KeyguardLock").reenableKeyguard()
    }


    fun confirmDeviceCredential() {
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val intent = km.createConfirmDeviceCredentialIntent("", "")

        startActivity(intent)
    }

}
