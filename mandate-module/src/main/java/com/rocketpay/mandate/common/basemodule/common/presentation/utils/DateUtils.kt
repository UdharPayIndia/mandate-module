package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

internal object DateUtils {

    const val SLASH_DATE_FORMAT = "yyyy-MM-dd"
    const val SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR = "dd/MM/yy"
    const val SLASH_DATE_FORMAT_WITH_FOUR_DIGIT_YEAR = "dd/MM/yyyy"
    const val DOT_DATE_AND_TIME_FORMAT = "dd/MM/yyyy • hh:mm a"
    const val DAY_MONTH_DATE_FORMAT = "dd MMM"
    const val MONTH_DATE_FORMAT = "dd MMM yyyy"
    const val DOT_DATE_AND_TIME_FORMAT_WITH_TEXT = "dd MMM yy • hh:mm a"
    const val HOURS_FORMAT = "hh:mm a"
    const val DATE_FORMAT_WITHOUT_SEPARATOR = "yyyyMMdd"
    const val MONTH_DATE_FORMAT_WITH_NAME = "dd MMM yyyy • EEEE"
    const val TIME_WITH_DAY_NAME = "HH:mm • EEEE"
    const val MONTH_WITH_YEAR = "MMMM yyyy"

    fun getDate(millis: Long, format: String): String {
        return getDate(millis, format, Locale.ENGLISH)
    }


    private fun getDate(millis: Long, format: String, locale: Locale): String {
        val date = Date(millis)
        val dateFormat = SimpleDateFormat(format, locale)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    fun getDateWithUtc(millis: Long, format: String): String {
        return getDateWithUtc(millis, format, Locale.ENGLISH)
    }

    private fun getDateWithUtc(millis: Long, format: String, locale: Locale): String {
        val date = Date(millis)
        val dateFormat = SimpleDateFormat(format, locale)
        dateFormat.timeZone = TimeZone.getDefault()
        return dateFormat.format(date)
    }

    fun getDate(year: Int, month: Int, day: Int): String {
        return "${String.format(Locale.US, "%04d", year)}-${String.format(Locale.US, "%02d", month)}-${String.format(Locale.US, "%02d", day)}"
    }

    fun getNumberOfDays(millisecond: Long): Int {
        val seconds = millisecond / 1000L
        val minute =  seconds / 60L
        val hours = minute / 60L
        val days =  hours / 24L
        return days.toInt()
    }

    fun getMilliSecondsFromYearMonthDate(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }

    fun getCurrentDate(): String {
        return getDate(System.currentTimeMillis())
    }

    fun getCurrentDateWithoutTimeInMillis(): Long {
        val date = getCurrentDate()
        val format = SimpleDateFormat(SLASH_DATE_FORMAT, Locale.ENGLISH)
        return format.parse(date)?.time ?: 0
    }

    fun getDate(date: String?, fromFormat: String, toFormat: String): String {
        if(date.isNullOrEmpty()) return ""
        val originalFormat = SimpleDateFormat(fromFormat, Locale.ENGLISH)
        originalFormat.timeZone = TimeZone.getTimeZone("Etc/UTC")
        val targetFormat: DateFormat = SimpleDateFormat(toFormat, Locale.ENGLISH)
        targetFormat.timeZone = TimeZone.getDefault()
        val date = originalFormat.parse(date)
        return targetFormat.format(date)
    }


    fun getDate(date: String?, fromFormat: String): Long {
        if(date.isNullOrEmpty()) return 0L
        val originalFormat = SimpleDateFormat(fromFormat, Locale.ENGLISH)
        originalFormat.timeZone = TimeZone.getTimeZone("Etc/UTC")
        return originalFormat.parse(date)?.time ?: 0L
    }

    fun getDate(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat(SLASH_DATE_FORMAT, Locale.ENGLISH)
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }

    fun addDay(date: Long, days: Int): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(date)
        cal.add(Calendar.DATE, days)
        return cal.timeInMillis
    }

    fun addMonth(date: Long, months: Int): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(date)
        cal.add(Calendar.MONTH, months)
        return cal.timeInMillis
    }

    fun addYear(date: Long, years: Int): Long {
        val cal = Calendar.getInstance()
        cal.time = Date(date)
        cal.add(Calendar.YEAR, years)
        return cal.timeInMillis
    }

    fun getTimeInHours(hours: Int, hoursFormat: String): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hours)
        cal.set(Calendar.MINUTE, 0)

        val date = Date(cal.timeInMillis)
        val format = SimpleDateFormat(hoursFormat, Locale.ENGLISH)
        format.timeZone = TimeZone.getDefault()
        return format.format(date)
    }
}
