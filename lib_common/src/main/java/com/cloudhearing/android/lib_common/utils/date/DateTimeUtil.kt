package com.cloudhearing.android.lib_common.utils.date

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtil private constructor(){

    companion object {
        private val instance: DateTimeUtil by lazy {
            DateTimeUtil()
        }

        fun get() = instance
    }

    private val yyyyMMddFormatWithUnit = SimpleDateFormat("yyyy年MM月dd日")

    private val yyyyMMddFormatWithHorizontalLine = SimpleDateFormat("yyyy-MM-dd")

    private val MMddFormatWithBackslash = SimpleDateFormat("MM/dd")

    private val MMddFormatWithUnderline = SimpleDateFormat("MM-dd")

    private val MMddFormatWithUnit = SimpleDateFormat("MM月dd日")

    private val yyyyMMddEFormatWithBackslash = SimpleDateFormat("yyyy/MM/dd EEEE")

    /**
     * 例如 [yyyy年MM月dd日]
     *
     * @param date
     * @return
     */
    fun generateyyyyMMddFormatWithUnit(date: Date): String {
        return yyyyMMddFormatWithUnit.format(date)
    }

    /**
     * 例如 [yyyy/MM/dd EEEE]
     *
     * @param date
     * @return
     */
    fun generateyyyyMMddEFormatWithBackslash(date: Date): String {
        return yyyyMMddEFormatWithBackslash.format(date)
    }

    /**
     * 例如 [MM月dd日]
     *
     * @param date
     * @return
     */
    fun generateMMddFormatWithUnit(date: Date): String {
        return MMddFormatWithUnit.format(date)
    }

    /**
     * 例如 MM-dd
     *
     * @param date
     * @return
     */
    fun generateMMddFormatWithUnderline(date: Date): String {
        return MMddFormatWithUnderline.format(date)
    }

    /**
     * 例如 [yyyy-MM-dd]
     *
     * @param date
     * @return
     */
    fun generateyyyyMMddFormatWithHorizontalLine(date: Date): String {
        return yyyyMMddFormatWithHorizontalLine.format(date)
    }

    /**
     * 例如 [MM/dd]
     *
     * @param date
     * @return
     */
    fun generateMMddFormatWithBackslash(date: Date): String {
        return MMddFormatWithBackslash.format(date)
    }

    /**
     * 例如 [MM/dd]
     *
     * @param date
     * @return
     */
    fun generateMMddFormatWithBackslash(date: String): String {
        return MMddFormatWithBackslash.format(parseyyyyMMddFormatWithHorizontalLine(date))
    }

    /**
     * 解析时间 例如 [yyyy-MM-dd]
     *
     * @param source
     * @return
     */
    fun parseyyyyMMddFormatWithHorizontalLine(source: String): Date {
        return yyyyMMddFormatWithHorizontalLine.parse(source)
    }

    /**
     * 计算天数
     *
     * @param date
     * @return
     */
    fun calculateDays(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 计算时间差
     *
     * @param originDate
     * @param currentDate
     * @return
     */
    fun calculateTimeDifferenceInDays(originDate: Date, currentDate: Date): Int {
        return ((currentDate.time - originDate.time) / (1000 * 60 * 60 * 24)).toInt()
    }


    /**
     * 判断时间是否一致
     *
     * @param date
     * @return
     */
    fun checkDateIsToday(date: Date): Boolean {
        val selectTime = generateyyyyMMddFormatWithHorizontalLine(date)
        val todayTime = generateyyyyMMddFormatWithHorizontalLine(Date())

        return selectTime == todayTime
    }

    fun convertMillis(duration: Int): String {
        val i = duration / 1000
        val hour = i / 3600
        val minute = (i % 3600) / 60
        val second = (i % 3600) % 60

        val hourStr =
            if (hour == 0) {
                "00"
            } else if (hour > 10) {
                hour.toString()
            } else {
                "0$hour"
            }
        val minuteStr =
            if (minute == 0) {
                "00"
            } else if (minute > 10) {
                minute.toString()
            } else {
                "0$minute"
            }
        val secondStr =
            if (second == 0) {
                "00"
            } else if (second > 10) {
                second.toString()
            } else {
                "0$second"
            }
        return "$hourStr:$minuteStr:$secondStr"
    }
}