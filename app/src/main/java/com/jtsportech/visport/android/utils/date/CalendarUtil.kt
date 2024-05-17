package com.jtsportech.visport.android.utils.date

import java.util.Calendar

/**
 * Author: BenChen
 * Date: 2024/01/12 14:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class CalendarUtil private constructor(){
    companion object {
        private val instance: CalendarUtil by lazy {
            CalendarUtil()
        }

        fun get() = instance
    }

    /**
     * 判断是否是同一天
     *
     * @param calendar1
     * @param calendar2
     * @return
     */
    fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }
}