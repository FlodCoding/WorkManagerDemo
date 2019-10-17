package com.coassets.android.workmanagertest.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import com.coassets.android.workmanagertest.R
import com.coassets.android.workmanagertest.data.Task
import java.util.*
import kotlin.collections.ArrayList


/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-08-21
 * UseDes:
 */
object CalendarUtil {

    private val Rule_ByDay = arrayOf("MO", "TU", "WE", "TH", "FR", "SA", "SU")


    /**
     * 检查日历账户，没有就添加
     */
    private fun checkCalendarAccount(context: Context): Int {
        val accountName = context.getString(R.string.app_name)
        val accountIds = getCalendarAccountByName(context, accountName)
        if (accountIds.isEmpty()) {
            return addCalendarAccount(context, accountName, "${accountName}日历触发")
        }

        return accountIds[0]
    }


    fun addEvent(context: Context, task: Task): String? {

        val calendarId = checkCalendarAccount(context)
        if (calendarId == -1) return null


        val eventValues = buildEventContentValues(calendarId, task)
        val cr = context.contentResolver
        //插入日历中
        val eventId = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues)?.lastPathSegment

        eventId ?: return null

        val remindersValues = buildReminderContentValues(eventId)
        cr.insert(CalendarContract.Reminders.CONTENT_URI, remindersValues)
        return eventId

    }


    fun updateEvent(context: Context, task: Task) {
        val calendarId = checkCalendarAccount(context)
        if (calendarId == -1) return

        val contentValues = buildEventContentValues(calendarId, task)

        context.contentResolver.update(ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI,
                task.time.eventId),
                contentValues, null, null)
    }


    fun deleteEvent(context: Context, eventId: Long) {
        context.contentResolver.delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,
                eventId), null, null)
    }


    private fun buildEventContentValues(calendarId: Int, task: Task): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        contentValues.put(CalendarContract.Events.TITLE, "打开 " + task.appInfo.appName)
        contentValues.put(CalendarContract.Events.DESCRIPTION, "此事件用来触发打开软件，请勿修改或删除")
        //闹钟无效
        //contentValues.put(CalendarContract.Events.HAS_ALARM, true)

        contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        contentValues.put(CalendarContract.Events.DTSTART, task.time.dateTime)
        contentValues.put(CalendarContract.Events.DTEND, task.time.dateTime)

        Log.d("start_time", "addtime:${task.time.dateTime}")
        if (task.time.repeat) {
            //重复规则
            contentValues.put(CalendarContract.Events.RRULE, getFreqRule(task.time.repeatInWeek))
        }

        return contentValues
    }

    private fun buildReminderContentValues(eventId: String): ContentValues {
        val contentValues = ContentValues()

        contentValues.put(CalendarContract.Reminders.EVENT_ID, eventId)
        contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        //MINUTES = 0 或不添加无通知提醒
        contentValues.put(CalendarContract.Reminders.MINUTES, CalendarContract.Reminders.MINUTES_DEFAULT)
        //contentValues.put(CalendarContract.Reminders.MINUTES, 1)

        return contentValues
    }


    private fun getFreqRule(repeatInWeek: List<Boolean>): String {
        val rule = StringBuilder("FREQ=WEEKLY;WKST=MO;BYDAY=")
        for ((index, repeat) in repeatInWeek.withIndex()) {
            if (repeat) {
                rule.append(Rule_ByDay[index])
                if (index != repeatInWeek.size - 1)
                    rule.append(",")
            }

        }
        return rule.toString()

    }


    fun queryEventIdByTime(context: Context, alertTime: String): ArrayList<Long> {
        val selection = "${CalendarContract.CalendarAlerts.ALARM_TIME} = ?"
        val eventIdList = ArrayList<Long>()
        //通过提醒的时间查询到相应的EventId
        val cursor = context.contentResolver.query(
                CalendarContract.CalendarAlerts.CONTENT_URI_BY_INSTANCE,
                arrayOf(CalendarContract.CalendarAlerts.EVENT_ID), selection, arrayOf(alertTime), null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                eventIdList.add(cursor.getLong(0))
            }
            cursor.close()
        }

        return eventIdList

    }


    /**
     * 通过日历账户名获取本地日历ID，可以有多个
     */
    private fun getCalendarAccountByName(context: Context, accountName: String): ArrayList<Int> {
        val cursor = context.contentResolver.query(
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


    /**
     * 添加一个本地的日历账号
     * [accountName]账号名
     * [displayName]账号内显示的项目名
     * CalendarContract
     * @return Calendars._ID
     * @see CalendarContract.Calendars._ID
     */
    private fun addCalendarAccount(context: Context, accountName: String, displayName: String): Int {
        val contentValues = ContentValues()
        contentValues.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
        contentValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName)
        contentValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName)
        contentValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        contentValues.put(CalendarContract.Calendars.VISIBLE, 1)
        @Suppress("DEPRECATION")
        //TODO Color
        //contentValues.put(CalendarContract.Calendars.CALENDAR_COLOR, context.resources.getColor(R.color.colorPrimary))
        contentValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        contentValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)

        var uri = CalendarContract.Calendars.CONTENT_URI

        uri = uri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build()

        return context.contentResolver.insert(uri, contentValues)?.lastPathSegment?.toInt()
                ?: return -1

    }

    private fun deleteCalendarAccountByName(context: Context, accountName: String): Boolean {
        return context.contentResolver.delete(
                CalendarContract.Calendars.CONTENT_URI,
                "${CalendarContract.Calendars.ACCOUNT_NAME} = ?",
                arrayOf(accountName)
        ) > 0
    }


}
