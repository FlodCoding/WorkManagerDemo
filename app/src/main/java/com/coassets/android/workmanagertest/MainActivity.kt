package com.coassets.android.workmanagertest

import android.content.ContentValues
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit


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
            //createNewCalendar()
            queryCalendarAccount()
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


    fun queryCalendarAccount() {
        val selection: String = CalendarContract.Calendars.ACCOUNT_NAME
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            arrayOf(CalendarContract.Calendars.ACCOUNT_NAME),
            null,
            null,
            null
        )

        cursor ?: return

        while (cursor.moveToNext()) {
            
            //var stringBuilder = StringBuilder()
           /* for (index in 0 until cursor.columnCount) {
                stringBuilder.append()
            }*/

            print(cursor.extras.toString())

        }
        cursor.close()
    }


    fun createCalendar() {
        val contentValues = ContentValues()

        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime.set(2019, 9, 12, 17, 10, 0)

        Log.d("Main", beginTime.time.toLocaleString())
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2019, 9, 12, 17, 20, 0)
        endMillis = endTime.timeInMillis

        Log.d("Main", endTime.time.toLocaleString())



        contentValues.put(CalendarContract.Events.DTSTART, startMillis)
        contentValues.put(CalendarContract.Events.DTEND, startMillis + 600000)
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 1)
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
        cr.insert(CalendarContract.Reminders.CONTENT_URI, contentValues2)


    }


    fun createNewCalendar() {
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, "ACCOUNT_NAME")
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "DISPLAY_NAME")
        contentValues.put(CalendarContract.Calendars.NAME, "NAME")
        contentValues.put(
            CalendarContract.Calendars.ACCOUNT_TYPE,
            CalendarContract.ACCOUNT_TYPE_LOCAL
        )
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, "ACCOUNT_NAME")
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1)
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        contentValues.put(
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CAL_ACCESS_OWNER
        )
        contentValues.put(CalendarContract.Calendars.DIRTY, 1)

        var uri = CalendarContract.Calendars.CONTENT_URI
        uri = uri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "ACCOUNT_NAME")
            .appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL
            )
            .build()


        val id = contentResolver.insert(uri, contentValues)

    }


}
