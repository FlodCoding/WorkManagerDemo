package com.coassets.android.workmanagertest

import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.Service
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.CalendarContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.coassets.android.workmanagertest.data.Gesture
import com.coassets.android.workmanagertest.data.GestureBundle
import com.coassets.android.workmanagertest.service.GestureAccessibility
import com.coassets.android.workmanagertest.service.GestureWatcher
import com.coassets.android.workmanagertest.utils.DeviceUtil
import com.coassets.android.workmanagertest.utils.PrefsUtil
import com.flod.gesture.GestureInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        PrefsUtil.init(application)


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
                 //startActivity(Intent(this, KeyguardDismissActivity::class.java))
                // tryWakeUpAndUnlock(this)


             }, 3000)*/

            recordGesture()
            // unlockTest()

        }

        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //activityManager.killBackgroundProcesses()


        text.setOnClickListener {
            val gesture = PrefsUtil.getSerializable("gesture") as Gesture?
            it.postDelayed({
                unLockGestureTest()

            }, 5000)
        }
    }


    fun unLockGestureTest() {
        if (DeviceUtil.tryUnlockDevice(this)) {
        } else {
            //需要密码解锁
            val unlockGesture =
                (PrefsUtil.getSerializable("gesture") as Gesture)

            val checkOffset = unlockGesture.checkOffset

            GestureAccessibility.setGlobalGestureWatcher(object :
                GestureWatcher.SimpleAccessibility() {
                private var findTarget = true
                private val LOCK_PATTERN_ID = "com.android.systemui:id/lockPatternView"

                override fun onAccessibilityEvent(service: Service, event: AccessibilityEvent) {

                    if (checkOffset && findTarget && service is GestureAccessibility) {
                        val source = event.source ?: return
                        val targets = source.findAccessibilityNodeInfosByViewId(LOCK_PATTERN_ID)
                        //TODO 找不到错误完善
                        if (targets.isNotEmpty()) {
                            findTarget = false

                            val bounds = Rect()
                            targets[0].getBoundsInScreen(bounds)

                            val offsetX = bounds.left - unlockGesture.originX
                            val offsetY = bounds.top - unlockGesture.originY
                            val gestureList = unlockGesture.buildGestureBundle().gestureInfoList
                            for (item in gestureList) {
                                item.offsetX = offsetX
                                item.offsetY = offsetY
                            }

                            //TODO 重新保存
                            //unlockGesture.checkOffset = false
                            unlockGesture.gestureBundleBytes = GestureBundle(gestureList).toBytes()
                            PrefsUtil.putSerializable("gesture", unlockGesture)


                            GestureAccessibility.startGestures(this@MainActivity, unlockGesture)

                        }

                    }
                }

                override fun onGesturesComplete(service: Service) {
                    GestureAccessibility.removeGlobalGestureWatcher()
                }

            })

            if (checkOffset) {
                GestureAccessibility.startService(this)
            } else {
                GestureAccessibility.startGestures(this, unlockGesture)
            }


        }

    }


    fun getLaunchAppIntent(launchPackage: String, launchName: String): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val cn = ComponentName(launchPackage, launchName)
        intent.component = cn
        return intent
    }

    fun recordGesture() {
        confirmDeviceCredential()

        GestureAccessibility.startService(this)
        GestureAccessibility.setGlobalGestureWatcher(object :

            GestureWatcher.SimpleAccessibility() {
            private val LOCK_PATTERN_ID = "com.android.settings:id/lockPattern"
            private var originX: Float = 0f
            private var originY: Float = 0f
            private var findTarget = true

            override fun onAccessibilityEvent(service: Service, event: AccessibilityEvent) {
                if (findTarget) {
                    val source = event.source ?: return
                    val targets = source.findAccessibilityNodeInfosByViewId(LOCK_PATTERN_ID)
                    //TODO 找不到错误完善
                    if (targets.isNotEmpty()) {
                        findTarget = false
                        Log.d(GestureAccessibility.TAG, "onAccessibilityEvent")
                        countOriginPoint(targets[0])

                        //找到这个元素，启动Service
                        GestureAccessibility.startRecord(this@MainActivity)

                    }
                }
            }

            override fun onStopRecord(service: Service, gestureInfoList: ArrayList<GestureInfo>) {
                val gesture =
                    Gesture(
                        checkOffset = true,
                        gestureBundleBytes = GestureBundle(gestureInfoList).toBytes(),
                        originX = originX, originY = originY
                    )

                PrefsUtil.putSerializable("gesture", gesture)

                GestureAccessibility.setGlobalGestureWatcher(null)
            }


            fun countOriginPoint(target: AccessibilityNodeInfo) {
                val bound = Rect()
                target.getBoundsInScreen(bound)
                originX = bound.left.toFloat()
                originY = bound.top.toFloat()
            }

        })


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
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //如果时熄屏,唤醒屏幕
        if (!pm.isInteractive) {
            val wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "DeviceUtil:WakeLock"
            )
            wakeLock.acquire(30000)
        }


        /*km.requestDismissKeyguard(this, @RequiresApi(Build.VERSION_CODES.O)
        object :KeyguardManager.KeyguardDismissCallback(){
            override fun onDismissCancelled() {
                super.onDismissCancelled()
            }

            override fun onDismissError() {
                super.onDismissError()
            }

            override fun onDismissSucceeded() {
                super.onDismissSucceeded()
            }
        })*/

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )


    }


    fun confirmDeviceCredential() {
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val intent = km.createConfirmDeviceCredentialIntent("", "")
        //TODO intent null

        startActivityForResult(intent, 0x51)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 0x51 && resultCode == Activity.RESULT_OK) {
            GestureAccessibility.stopRecord(this)
        }
    }


    //=====================================================================================================================================================================
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

}
