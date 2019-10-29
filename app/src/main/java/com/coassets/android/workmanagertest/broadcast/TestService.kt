package com.coassets.android.workmanagertest.broadcast

import android.app.Service
import android.content.Intent
import android.graphics.Rect
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import com.coassets.android.workmanagertest.data.Gesture
import com.coassets.android.workmanagertest.data.GestureBundle
import com.coassets.android.workmanagertest.service.GestureAccessibility
import com.coassets.android.workmanagertest.service.GestureWatcher
import com.coassets.android.workmanagertest.utils.DeviceUtil
import com.coassets.android.workmanagertest.utils.PrefsUtil

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-29
 * UseDes:
 *
 */
class TestService :Service(){
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (DeviceUtil.tryUnlockDevice(this)) {
            taskStart()
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
                            unlockGesture.checkOffset = false
                            unlockGesture.gestureBundleBytes = GestureBundle(gestureList).toBytes()
                            PrefsUtil.putSerializable("gesture", unlockGesture)


                            GestureAccessibility.startGestures(this@TestService, unlockGesture)

                        }

                    }
                }

                override fun onGesturesComplete(service: Service) {
                    GestureAccessibility.removeGlobalGestureWatcher()
                    taskStart()
                }

            })

            if (checkOffset) {
                GestureAccessibility.startService(this)
            } else {
                GestureAccessibility.startGestures(this, unlockGesture)
            }


        }


        return super.onStartCommand(intent, flags, startId)
    }

    fun taskStart() {

    }
}