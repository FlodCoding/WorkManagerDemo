@file:Suppress("unused")

package com.coassets.android.workmanagertest.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.KeyguardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import androidx.annotation.RequiresApi
import com.coassets.android.workmanagertest.data.Gesture
import com.flod.view.GestureInfo


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-26
 * UseDes:
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class GestureAccessibility : AccessibilityService(), GestureWatcher.Recorder {

    private var mMode: Int = 0

    companion object {
        const val TAG = "GestureAccessibility"
        private const val KEY_MODE = "KEY_MODE"
        private const val MODE_DISPATCH_GESTURE = 1
        private const val MODE_GESTURE_RECORD_DEFAULT = 2 //启动后手动开启录制
        private const val MODE_GESTURE_RECORD_AUTO = 3    //启动后立刻开始录制

        private const val KEY_GESTURE = "KEY_GESTURE"


        private var mGlobalWatcher: GestureWatcher.Accessibility? = null

        //由于onBind 在AccessibilityService中被设置为final，无法使用bindService与外界通信
        //目前就先使用静态接口，通过接口将数据回调出去，不太好的做法，后面是否考虑别的方式
        fun setGlobalGestureWatcher(watcher: GestureWatcher.Accessibility) {
            mGlobalWatcher = watcher
        }

        fun startGestures(context: Context, gestures: Gesture) {
            val intent = Intent(context, GestureAccessibility::class.java)
            intent.putExtra(KEY_MODE, MODE_DISPATCH_GESTURE)
            intent.putExtra(KEY_GESTURE, gestures)
            context.startService(intent)
        }

        fun startService(context: Context) {
            context.startService(Intent(context, GestureAccessibility::class.java))
        }

        fun startRecordService(context: Context) {
            val intent = Intent(context, GestureAccessibility::class.java)
            intent.putExtra(KEY_MODE, MODE_GESTURE_RECORD_DEFAULT)
            context.startService(intent)
        }

        fun startRecord(context: Context) {
            val intent = Intent(context, GestureAccessibility::class.java)
            intent.putExtra(KEY_MODE, MODE_GESTURE_RECORD_AUTO)
            context.startService(intent)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mGlobalWatcher = null
        return super.onUnbind(intent)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")


        mMode = intent.getIntExtra(KEY_MODE, 0)
        when (mMode) {
            MODE_DISPATCH_GESTURE -> {
                val gestures = intent.getSerializableExtra(KEY_GESTURE)
                if (gestures is Gesture) {
                    dispatchGestures(gestures)
                }
            }
            //RecordGesture
            MODE_GESTURE_RECORD_DEFAULT or
                    MODE_GESTURE_RECORD_AUTO -> bindGestureRecordService()
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun dispatchGestures(gesture: Gesture) {
        val gestures = gesture.buildGestureBundle().gestureInfoList


        //检查当前设备状况
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        //熄屏
        if (!pm.isInteractive) {
            @Suppress("DEPRECATION")
            val wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "$TAG:WakeLock"
            )

            //acquire 的timeout作用是如果不手动release 则以超时时间release
            //唤醒亮屏30s
            wakeLock.acquire(30000)

            //有滑动锁
            if (km.isKeyguardLocked) {
                km.newKeyguardLock("$TAG:KeyguardLock").disableKeyguard()
            }

            //有屏幕锁
            if (km.isDeviceSecure) {
                //TODO 拿到屏幕解锁手势，添加到List里面

                //gestures.add(0,GestureInfo())
            }

        }


        var index = 0
        val callBack =
            object : AccessibilityService.GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    Log.d(TAG, "onCancelled")
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    Log.d(TAG, "onCompleted")

                    index++

                    if (gestures.size > index) {
                        startGesture(gestures[index], this, false)
                    } else {
                        index = 0
                    }

                }
            }

        for (item in gestures) {
            startGesture(item, callBack, false)
        }
    }


    private fun startGesture(
        gestureInfo: GestureInfo,
        gestureResultCallback: GestureResultCallback,
        immediately: Boolean
    ) {

        val builder = GestureDescription.Builder()
        val gesture = gestureInfo.gesture

        var duration = gestureInfo.duration
        if (duration > GestureDescription.getMaxGestureDuration()) {
            duration = GestureDescription.getMaxGestureDuration()
        }

        for (stroke in gesture.strokes.withIndex()) {
            if (stroke.index == GestureDescription.getMaxStrokeCount() - 1)
                break
            val description =
                GestureDescription.StrokeDescription(
                    stroke.value.path,
                    if (immediately) 0 else gestureInfo.delayTime,
                    duration
                )

            builder.addStroke(description)
        }

        dispatchGesture(builder.build(), gestureResultCallback, null)
    }


    //=============================== RecordService Part===========================================//
    private var mRecordServiceBinder: GestureRecorderService.IGestureRecordBinder? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
            mRecordServiceBinder?.setOnGestureRecordedListener(null)
            mRecordServiceBinder = null


        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            if (service is GestureRecorderService.IGestureRecordBinder) {
                service.setOnGestureRecordedListener(this@GestureAccessibility)
                mRecordServiceBinder = service

                //自动开始
                if (mMode == MODE_GESTURE_RECORD_AUTO) {
                    service.performStart()
                }

            }

        }
    }

    private fun bindGestureRecordService() {
        val intent = Intent(this, GestureRecorderService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }


    //==========================Recorder==================================//


    override fun onStartRecord() {
        mGlobalWatcher?.onStartRecord(this)
    }


    override fun onRecording(gestureInfo: GestureInfo) {
        mGlobalWatcher?.onRecording(this, gestureInfo)
        startGesture(
            gestureInfo,
            object : GestureResultCallback() {
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    mRecordServiceBinder?.onResult(true)
                }

                override fun onCompleted(gestureDescription: GestureDescription?) {
                    mRecordServiceBinder?.onResult(false)
                }
            }, true
        )

    }

    override fun onStopRecord(gestureInfoList: ArrayList<GestureInfo>) {
        mGlobalWatcher?.onStopRecord(this, gestureInfoList)
        unbindService(mServiceConnection)
    }

    override fun onCancelRecord() {
        mGlobalWatcher?.onCancelRecord(this)
        unbindService(mServiceConnection)
    }

    //=======================================================================================//


    override fun onInterrupt() {


    }


    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        mGlobalWatcher?.onAccessibilityEvent(this, event)
    }


}