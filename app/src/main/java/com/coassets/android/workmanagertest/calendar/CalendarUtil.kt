package com.coassets.android.workmanagertest.calendar

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.*


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-21
 * UseDes:
 */
object CalendarUtil {

    private val Rule_ByDay = arrayOf("MO", "TU", "WE", "TH", "FR", "SA", "SU")

    fun insertTask(context: Context, task: Task) {
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Events.CALENDAR_ID, 580)
        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

        contentValues.put(CalendarContract.Events.TITLE, "打开 " + task.appInfo.appName)
        contentValues.put(CalendarContract.Events.DESCRIPTION, "此事件用来触发打开软件，请勿删除")


        contentValues.put(CalendarContract.Events.DTSTART, task.time.dateTime)
        if (task.time.repeat) {
            contentValues.put(CalendarContract.Events.RRULE, getFreqRule(task.time.repeatInWeek))
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = task.time.dateTime
            calendar.add(Calendar.YEAR, 20)  //重复20年后结束 - -
            contentValues.put(CalendarContract.Events.DTEND, calendar.timeInMillis)
        } else
            contentValues.put(CalendarContract.Events.DTEND, task.time.dateTime)

        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI,contentValues)
    }


    fun updateTask(){

    }


    private fun getFreqRule(repeatInWeek: List<Boolean>): String {
        val rule = StringBuilder("FREQ=WEEKLY;WKST=MO;BYDAY=")
        for ((index, repeat) in repeatInWeek.withIndex()) {
            if (repeat)
                rule.append(Rule_ByDay[index])
        }
        return rule.toString()

    }


}
