package com.coassets.android.workmanagertest.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-17
 * UseDes:
 */
object DeviceUtil {


    fun isScreenOn(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.isInteractive
    }

    fun newWakeLock(context: Context): PowerManager.WakeLock {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                PowerManager.ACQUIRE_CAUSES_WAKEUP, "DeviceUtil:wakeLock")
    }

    fun isDeviceLocked(context: Context): Boolean {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km.isDeviceLocked
    }

    fun isDeviceSecure(context: Context): Boolean {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return km.isDeviceSecure
    }


    /**
     * 尝试解锁，仅适应没有Pin 图案、密码屏幕锁时
     */
    fun tryWakeUpAndUnlock(context: Context) {

        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (km.isDeviceSecure) return


        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //如果时熄屏,唤醒屏幕
        if (!pm.isInteractive) {
            val wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP, "DeviceUtil:WakeLock")
            wakeLock.acquire(1000)
            wakeLock.release()
        }

        @Suppress("DEPRECATION")
        if (km.isKeyguardLocked)
            km.newKeyguardLock("DeviceUtil:KeyguardLock").disableKeyguard()
    }


}
