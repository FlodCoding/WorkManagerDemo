package com.coassets.android.workmanagertest.broadcast

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.coassets.android.workmanagertest.data.Gesture
import com.coassets.android.workmanagertest.data.GestureBundle
import com.coassets.android.workmanagertest.service.GestureAccessibility
import com.coassets.android.workmanagertest.service.GestureWatcher
import com.coassets.android.workmanagertest.utils.CalendarUtil
import com.coassets.android.workmanagertest.utils.DeviceUtil
import com.coassets.android.workmanagertest.utils.PrefsUtil
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
        if (DeviceUtil.tryUnlockDevice(context)) {
            taskStart()
        } else {
            //需要密码解锁
            val unlockGesture =
                (PrefsUtil.getSerializable("gesture") as Gesture)

            val checkOffset = unlockGesture.checkOffset

            Settings.ACTION_BLUETOOTH_SETTINGS
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
                            unlockGesture.checkOffset = false
                            unlockGesture.gestureBundleBytes = GestureBundle(gestureList).toBytes()
                            PrefsUtil.putSerializable("gesture", unlockGesture)


                            GestureAccessibility.startGestures(context, unlockGesture)

                        }

                    }
                }

                override fun onGesturesComplete(service: Service) {
                    GestureAccessibility.removeGlobalGestureWatcher()
                    taskStart()
                }

            })

            if (checkOffset) {
                GestureAccessibility.startService(context)
            } else {
                GestureAccessibility.startGestures(context, unlockGesture)
            }


        }


        /*val targets =
            rootInActiveWindow.findAccessibilityNodeInfosByViewId("com.android.systemui:id/lockPatternView")

        if (targets.isNotEmpty()) {
            val bounds = Rect()
            targets[0].getBoundsInScreen(bounds)

            Log.d(TAG, bounds.toShortString())
            val offsetX = bounds.left - unlockGesture.originX
            val offsetY = bounds.top - unlockGesture.originY
            val gestureList = unlockGesture.buildGestureBundle().gestureInfoList
            for (item in gestureList) {
                item.offsetX = offsetX
                item.offsetY = offsetY
                Log.d(TAG, "offsetX:$offsetX")
                Log.d(TAG, "offsetY:$offsetY")
            }
            gestures.addAll(0, gestureList)
        }*/


        /*runBlocking {
            val task = TaskModel.queryByEventId(eventIds[0])
            if (task != null) {
                context.startActivity(AppUtil.getLaunchAppIntent(task.appInfo.launchPackage, task.appInfo.launchName))

                if (task.gesture != null) {
                    val gestures = task.gesture?.buildGestureBundle()?.gestureInfoList!!
                    GestureAccessibility.startGestures(context, ArrayList(gestures))
                }


            }


        }*/

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                Toast.makeText(context.applicationContext, "我活了", Toast.LENGTH_SHORT).show()
            }
        }


    }


    fun taskStart() {

    }

}