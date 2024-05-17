package com.cloudhearing.android.lib_common.utils.date

import timber.log.Timber
import java.util.*

class CalendarUtil {

    companion object {
        private val instance: CalendarUtil by lazy {
            CalendarUtil()
        }

        fun get() = instance
    }

    /**
     * 获取这个月星期的开始和结束时间，例如 2022-12-26 到 2023-02-05
     *
     * @return
     */
    fun getMonthWeekStartAndEndTime(): Pair<String?, String?> {
        val lastMonthDay = getLastMonthDay()
        val nextMonthDay = getNextMonthDay()
        Timber.d("lastMonthDay $lastMonthDay nextMonthDay $nextMonthDay")

        return Pair(lastMonthDay, nextMonthDay)
    }

    /**
     * 获取当月周数
     *
     * @return
     */
    fun getCurrentWeekOfMonthQuantity(): Int {
        val calendar = Calendar.getInstance()

        setFirstDayOfWeek(calendar)

        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    /**
     * 获取月份周数
     *
     * @param date
     * @return
     */
    fun getWeekOfMonthQuantity(date: String): Int {
        val time =
            DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(date)

        val calendar = Calendar.getInstance()
        calendar.time = time

        setFirstDayOfWeek(calendar)

        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    /**
     * 获取当月第几周
     *
     * @return
     */
    fun getCurrentWeekOfMonth(): Int {
        val calendar = Calendar.getInstance()

        setFirstDayOfWeek(calendar)

        return calendar.get(Calendar.WEEK_OF_MONTH)
    }

    /**
     * 周几
     *
     * @param formatTime
     * @return
     */
    fun getWhichDay(formatTime: String): Int {
        val calendar = Calendar.getInstance()
        calendar.time = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(formatTime)
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 周几
     *
     * @param date
     * @return
     */
    fun getWhichDay(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 周几
     *
     * @param calendar
     * @return
     */
    fun getWhichDay(calendar: Calendar): Int {
        return calendar.get(Calendar.DAY_OF_WEEK)
    }

    /**
     * 日期加一天
     *
     * @param formatTime  [yyyy-mm-dd]
     * @param addDays
     * @return
     */
    fun datePlusDay(formatTime: String, addDays: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(formatTime)
        calendar.add(Calendar.DATE, addDays)
        return DateTimeUtil.get().generateyyyyMMddFormatWithHorizontalLine(calendar.time)
    }

    /**
     * 日期加一天
     *
     * @param date
     * @param addDays
     * @return
     */
    fun datePlusDay(date: Date, addDays: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, addDays)
        return DateTimeUtil.get().generateyyyyMMddFormatWithHorizontalLine(calendar.time)
    }

    /**
     * 日期加一月
     *
     * @param formatTime
     * @param addMonth
     * @return
     */
    fun datePlusMonth(formatTime: String, addMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(formatTime)
        calendar.add(Calendar.MONTH, addMonth)
        return DateTimeUtil.get().generateyyyyMMddFormatWithHorizontalLine(calendar.time)
    }


    /**
     * 获取日历上的周一到周日
     *
     * @param calendar
     * @return
     */
    fun getMondayToSundayThisWeek(calendar: Calendar): Pair<Date, Date> {
        // 定位到周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val mondayDateText = DateTimeUtil.get().generateyyyyMMddFormatWithUnit(calendar.time)
        val mondayDate = calendar.time
        Timber.d("周一的日期 $mondayDateText")

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val sundayDateText = DateTimeUtil.get().generateyyyyMMddFormatWithUnit(calendar.time)
        val sundayDate = calendar.time
        Timber.d("周日的日期 $sundayDateText")

        return Pair(mondayDate, sundayDate)
    }

    fun getMonthFirstDayToLastDay(calendar: Calendar): Pair<Date, Date> {
        //设置当月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayDateText = DateTimeUtil.get().generateyyyyMMddFormatWithUnit(calendar.time)
        val firstDayDate = calendar.time
        Timber.d("当前月份的第一天 $firstDayDateText")

        //设置当月的最后一天
        calendar.add(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE) - 1)

        val lastDayDateText = DateTimeUtil.get().generateyyyyMMddFormatWithUnit(calendar.time)
        val lastDayDate = calendar.time
        Timber.d("当前月份的最后一天 $lastDayDateText")

        return Pair(firstDayDate, lastDayDate)
    }

    /**
     * 当前日期的周一
     *
     * @param originFormatDate
     * @return
     */
    fun currentMondayDate(originFormatDate: String): Date {
        val calendar = Calendar.getInstance()

        val originDate = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(originFormatDate)

        calendar.time = originDate

        setFirstDayOfWeek(calendar)

        // 设置为周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        return calendar.time
    }

    /**
     * 当前日期的周一
     *
     * @param originDate
     * @return
     */
    fun currentMondayDate(originDate:Date):Date{
        val calendar = Calendar.getInstance()

        calendar.time = originDate

        setFirstDayOfWeek(calendar)

        // 设置为周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        return calendar.time
    }

    /**
     * 当前日期的星期日
     *
     * @param originFormatDate
     * @return
     */
    fun currentSundayDate(originFormatDate: String): Date {
        val calendar = Calendar.getInstance()

        val originDate = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(originFormatDate)

        calendar.time = originDate

        setFirstDayOfWeek(calendar)

        // 设置为周日
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        return calendar.time
    }

    /**
     * 当前日期的星期日
     *
     * @param originDate
     * @return
     */
    fun currentSundayDate(originDate: Date): Date {
        val calendar = Calendar.getInstance()

        calendar.time = originDate

        setFirstDayOfWeek(calendar)

        // 设置为周日
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        return calendar.time
    }

    /**
     * 当前日期加一周
     *
     * @param calendar
     */
    fun currentDatePlusOneWeek(calendar: Calendar) {
        setFirstDayOfWeek(calendar)

        // 加一周
        calendar.add(Calendar.WEEK_OF_MONTH, 1)
        //调整到周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }

    /**
     * 当前日期减一周
     *
     * @param calendar
     */
    fun currentDateReduceOneWeek(calendar: Calendar) {
        setFirstDayOfWeek(calendar)

        // 减一周
        calendar.add(Calendar.WEEK_OF_MONTH, -1)
        //调整到周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }

    /**
     * 当前日期加一个月
     *
     * @param calendar
     */
    fun currentDatePlusOneMonth(calendar: Calendar) {
        // 加一周
        calendar.add(Calendar.MONTH, 1)
    }

    /**
     * 当前日期减一个月
     *
     * @param calendar
     */
    fun currentDateReduceOneMonth(calendar: Calendar) {
        // 加一周
        calendar.add(Calendar.MONTH, -1)
    }

    /**
     * 当前月份
     *
     * @param date
     * @return
     */
    fun currentMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date

        return calendar.get(Calendar.MONTH)
    }

    /**
     * 计算日期
     *
     * @param date
     * @param dayOfWeek
     * @return
     */
    fun calculationDate(date: String, dayOfWeek: Int): Date {
        val calendar = Calendar.getInstance()
        setFirstDayOfWeek(calendar)

        calendar.time = DateTimeUtil.get().parseyyyyMMddFormatWithHorizontalLine(date)

        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)

        return calendar.time
    }


    /**
     * 获取上一个月需要补全的天数
     *
     * @return  有值代表需要补
     */
    private fun getLastMonthDay(): String? {
        //获取当前月第一天
        val calendarFirstDay = Calendar.getInstance()
        calendarFirstDay.set(Calendar.DAY_OF_MONTH, 1)
        //当月第一天是星期几
        val firstDayOfWeek = calendarFirstDay.get(Calendar.DAY_OF_WEEK).run {
            // 周一 到 周日 1-7
            when (this) {
                Calendar.SUNDAY -> 7
                Calendar.SATURDAY -> 6
                else -> this - 1
            }
        }
        //firstDayOfWeek =2时，就是星期一，当前月第一天已经处于星期一，不需要添加上一个月补充天数
        if (firstDayOfWeek != Calendar.MONDAY) {
            //计算需要补的天数,因为是周一，所以需要减 1
            val needAddDayNumber = firstDayOfWeek - 1
            //取出上一个月需要补全天数的第一天
            calendarFirstDay.add(Calendar.DATE, -needAddDayNumber)

            return DateTimeUtil.get()
                .generateyyyyMMddFormatWithHorizontalLine(calendarFirstDay.time)
        }

        return null
    }

    /**
     * 获取下一个月需要补全的天数
     *
     * @return  有值代表需要补
     */
    private fun getNextMonthDay(): String? {
        //获取当前月最后一天
        val calendarLastDay = Calendar.getInstance()
        //设置下个月开始时间
        calendarLastDay.add(Calendar.MONTH, 1)
        calendarLastDay.set(Calendar.DAY_OF_MONTH, 0)
        //获取当前月最后一天是星期几
        val nextDayOfWeek = calendarLastDay.get(Calendar.DAY_OF_WEEK)
        //nextDayOfWeek =1时，就是星期日，当前月最后一天已经处于星期日，不需要添加上一个月补充天数
        if (nextDayOfWeek != Calendar.SUNDAY) {
            val adjustNextDayOfWeek = nextDayOfWeek.run {
                // 周一 到 周日 1-7
                when (this) {
                    Calendar.SUNDAY -> 7
                    Calendar.SATURDAY -> 6
                    else -> this - 1
                }
            }
            val needAddDayNumber = 7 - adjustNextDayOfWeek
            //取出下一个月需要补全的天数
            calendarLastDay.add(Calendar.DAY_OF_MONTH, needAddDayNumber)

            return DateTimeUtil.get()
                .generateyyyyMMddFormatWithHorizontalLine(calendarLastDay.time)
        }

        return null
    }

    /**
     * 设置一周的第一个星期类别，因为 jdk里面的周，不是像我们认为的周一是开始,是周日开始的
     *
     * @param calendar
     */
    private fun setFirstDayOfWeek(calendar: Calendar) {
        calendar.firstDayOfWeek = Calendar.MONDAY
    }
}