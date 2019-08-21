package com.coassets.android.workmanagertest.calendar

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey



/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-08
 * UseDes:
 */
@Entity
data class Task(@PrimaryKey(autoGenerate = true) var id: Long = 0, var isStart: Boolean, @Embedded var appInfo: AppInfo, @Embedded var time: Time) {


    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "isStart" to isStart,
            "appInfo" to appInfo.toMap(),
            "time" to time.toMap()
        )
    }
}

@Suppress("ArrayInDataClass")
@Entity
data class AppInfo(var appName: String, var appIconBytes: ByteArray) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "appName" to appName,
            "appIconBytes" to appIconBytes
        )
    }
}

@Entity
data class Time(
    var repeat: Boolean,
    var repeatInWeek: List<Boolean>,
    val dateTime: Long
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "repeat" to repeat,
            "repeatInWeek" to repeatInWeek,
            "dateTime" to dateTime
        )
    }
}

