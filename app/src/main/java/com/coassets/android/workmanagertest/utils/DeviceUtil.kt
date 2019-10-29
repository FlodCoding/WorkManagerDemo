package com.coassets.android.workmanagertest.utils

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.coassets.android.workmanagertest.KeyguardDismissActivity
import com.coassets.android.workmanagertest.service.GestureAccessibility

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
        return pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
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
    @Suppress("DEPRECATION")
    fun tryUnlockDevice(context: Context): Boolean {
        val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        //熄屏下亮屏
        if (!pm.isInteractive) {

            val wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "${GestureAccessibility.TAG}:WakeLock")
            //唤醒亮屏30s
            wakeLock.acquire(30000)
        }

        //有滑动锁
        return if (km.isKeyguardLocked) {
            //有密码锁,需要解锁密码
            if (km.isDeviceSecure) {
                //解开滑动锁
                context.startActivity(Intent(context, KeyguardDismissActivity::class.java))
                false
            } else {
                //解开滑动锁后直接进入
                km.newKeyguardLock("DeviceUtil:keyguardManager").disableKeyguard()
                true
            }

        } else {
            true
        }


    }
}
