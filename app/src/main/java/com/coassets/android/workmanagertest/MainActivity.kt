package com.coassets.android.workmanagertest

import android.content.ContentValues
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.coassets.android.workmanagertest.keepalive.job.AliveJobService
import com.coassets.android.workmanagertest.service.CalendarReceiver
import com.coassets.android.workmanagertest.service.TestService
import com.fanjun.keeplive.KeepLive
import com.fanjun.keeplive.config.ForegroundNotification
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit




class MainActivity : AppCompatActivity() {
    var callTime = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //addNewWork()
            // startForegroundService(Intent(this, DeskService::class.java))
            /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show()*/
            //startJobService()

            // startKeepLive()
            createCalendar()
        }


        val filter = IntentFilter(CalendarContract.ACTION_EVENT_REMINDER)
        filter.addDataScheme("content");
        registerReceiver(CalendarReceiver(), filter)
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

    fun startJobService() {
        val intent = Intent()
        intent.setClass(this, AliveJobService::class.java)
        startService(intent)

    }

    fun startKeepLive() {
        /*  var builder: Notification.Builder? = null
          val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          val channel = NotificationChannel("12", "app", NotificationManager.IMPORTANCE_HIGH)
          manager.createNotificationChannel(channel)

          builder = Notification.Builder(this, "12")*/

        /*builder.setContentTitle("KeepAppAlive")
        builder.setContentText("DaemonService is runing...")*/
        KeepLive.startWork(application, KeepLive.RunMode.ROGUE, ForegroundNotification.ini().title(""), TestService())

    }


    fun createCalendar() {
        val contentValues = ContentValues()

        val calID: Long = 1
        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime.set(2019, 8, 15, 16, 50)
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2020, 9, 14, 8, 45)
        endMillis = endTime.timeInMillis

        contentValues.put(CalendarContract.Events.DTSTART, startMillis)
        contentValues.put(CalendarContract.Events.DTEND, endMillis)

        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calID);
        contentValues.put(CalendarContract.Events.TITLE, "Jazzercise")

        contentValues.put(CalendarContract.Events.DESCRIPTION, "丢雷楼某")

        val cr = contentResolver
        //val uri = cr.insert(Events.CONTENT_URI, contentValues)




        //contentValues.put(CalendarContract.Events.CALENDAR_ACCESS_LEVEL,CalendarContract.Events.CAL_ACCESS_ROOT)
        /*contentValues.put(CalendarContract.Events.HAS_ALARM, 1)
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        contentValues.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);
        contentValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, packageName);
        contentValues.put(CalendarContract.Events.CUSTOM_APP_URI, "flod://a/b");*/


        //contentValues.put(CalendarContract.Reminders.MINUTES, 10);

        //contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

    }

    fun asSyncAdapter(uri: Uri, account: String, accountType: String): Uri {
        return uri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(Calendars.ACCOUNT_NAME, account)
            .appendQueryParameter(Calendars.ACCOUNT_TYPE, accountType).build()
    }

}
