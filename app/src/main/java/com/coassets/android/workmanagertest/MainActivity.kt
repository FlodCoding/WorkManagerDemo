package com.coassets.android.workmanagertest

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.coassets.android.workmanagertest.keepalive.job.AliveJobService
import com.coassets.android.workmanagertest.service.TestService
import com.fanjun.keeplive.KeepLive
import com.fanjun.keeplive.config.ForegroundNotification
import kotlinx.android.synthetic.main.activity_main.*
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

            startKeepLive()

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
    fun startJobService(){
        val intent  = Intent()
        intent.setClass(this, AliveJobService::class.java)
        startService(intent)

    }

    fun startKeepLive(){
      /*  var builder: Notification.Builder? = null
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel("12", "app", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)

        builder = Notification.Builder(this, "12")*/

        /*builder.setContentTitle("KeepAppAlive")
        builder.setContentText("DaemonService is runing...")*/
        KeepLive.startWork(application,KeepLive.RunMode.ROGUE, ForegroundNotification.ini().title(""),  TestService())

    }


}
