package com.mylexz.utils

import java.text.SimpleDateFormat
import java.util.*

object MyDate {
    fun getCalendarInGMT7(
        strTime: String?,
        strDate: String?,
        patternDate: String,
        patternTime: String = "HH:mmm:sss"
    ): Calendar {
        val concatedString = "$strDate $strTime"
        val formattedPattern = "$patternDate $patternTime"
        val calendar = Calendar.getInstance()
        val timeZone = TimeZone.getDefault()
        val simpleDate = SimpleDateFormat(formattedPattern, Locale.UK).parse(concatedString)
        //calendar.timeZone = TimeZone.getTimeZone("GMT+07:00")
        calendar.time = Date(simpleDate.time + timeZone.rawOffset)
        return calendar
    }

    fun getDateFromCalendar(calendar: Calendar): String {
        val day = getNameOfDay(calendar.get(Calendar.DAY_OF_WEEK))
        val month = getNameOfMonth(calendar.get(Calendar.MONTH))
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)

        return "$day, $date $month $year"
    }

    fun getTimeFromCalendar(calendar: Calendar): String {
        val storedHours = calendar.get(Calendar.HOUR_OF_DAY)
        val storedMinutes = calendar.get(Calendar.MINUTE)
        return "${if (storedHours < 10) "0$storedHours" else "$storedHours"}:${if (storedMinutes < 10) "0$storedMinutes" else "$storedMinutes"}"
    }

    private fun getNameOfMonth(monthVal: Int): String = when (monthVal) {
        Calendar.JANUARY -> "Jan"
        Calendar.FEBRUARY -> "Feb"
        Calendar.MARCH -> "March"
        Calendar.APRIL -> "April"
        Calendar.MAY -> "May"
        Calendar.JUNE -> "June"
        Calendar.JULY -> "July"
        Calendar.AUGUST -> "August"
        Calendar.SEPTEMBER -> "Sep"
        Calendar.OCTOBER -> "Oct"
        Calendar.NOVEMBER -> "Nov"
        Calendar.DECEMBER -> "Dec"
        else -> "nan"
    }

    private fun getNameOfDay(dayId: Int): String = when (dayId) {
        Calendar.SUNDAY -> "Minggu"
        Calendar.MONDAY -> "Senin"
        Calendar.THURSDAY -> "Selasa"
        Calendar.WEDNESDAY -> "Rabu"
        Calendar.SATURDAY -> "Sabtu"
        Calendar.TUESDAY -> "Kamis"
        Calendar.FRIDAY -> "Jum'at"
        else -> "nan"
    }
}