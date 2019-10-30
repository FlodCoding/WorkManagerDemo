@file:Suppress("ArrayInDataClass")

package com.coassets.android.workmanagertest.data

import java.io.Serializable

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-10-30
 * UseDes:
 *
 */
data class AppSettings(var unlockGesture: UnlockGesture) : Serializable {

}


data class UnlockGesture(
    val gestureBundle: GestureBundle,
    var checkOffset: Boolean = false,
    val originX: Float = 0f, val originY: Float = 0f,
    val lockType: Int
) : Serializable {

    companion object {

        const val SETTINGS_WIDGET_ID_PATTERN = "com.android.settings:id/lockPattern"
        const val SETTINGS_WIDGET_ID_PIN_PW = "com.android.settings:id/password_entry"


    }

}


class LockType{
    companion object{
        const val LOCK_TYPE_PATTERN = 1
        const val LOCK_TYPE_PIN = 2
        const val LOCK_TYPE_PASSWORD = 3
    }

}






