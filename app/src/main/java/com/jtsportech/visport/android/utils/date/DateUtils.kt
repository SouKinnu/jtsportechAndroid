package com.jtsportech.visport.android.utils.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Author: BenChen
 * Date: 2024/03/02 15:08
 * Email:chenxiaobin@cloudhearing.cn
 */
object DateUtils {
    private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputFormat1 = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val outputFormat2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat3 = SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())
    private val outputFormat4 = SimpleDateFormat("yyyy·MM月", Locale.getDefault())
    private val outputFormat5 = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    /**
     * 转成 yyyy/MM/dd
     *
     * @param dateTimeString
     * @return
     */
    fun convertToFormat1(dateTimeString: String): String {
        val date = inputFormat.parse(dateTimeString)
        return outputFormat1.format(date)
    }

    /**
     * 转成 yyyy-MM-dd
     *
     * @param dateTimeString
     * @return
     */
    fun convertToFormat2(dateTimeString: String): String {
        val date = inputFormat.parse(dateTimeString)
        return outputFormat2.format(date)
    }

    fun convertToFormat2(dateTime: Date): String {
        return outputFormat2.format(dateTime)
    }

    /**
     * 转成 MM月dd日 HH:mm
     *
     * @param dateTimeString
     * @return
     */
    fun convertToFormat3(dateTimeString: String): String {
        val date = inputFormat.parse(dateTimeString)
        return outputFormat3.format(date)
    }

    /**
     * 转成 yyyy·MM月
     *
     * @param date
     * @return
     */
    fun convertToFormat4(date: Date): String {
        val dateString = inputFormat.format(date)
        return try {
            outputFormat4.format(inputFormat.parse(dateString))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun convertToFormat5(dateTime: Date): String {
        return outputFormat5.format(dateTime)
    }
}