package com.jtsportech.visport.android.dataSource.home.event

import java.util.Calendar
import java.util.Date

/**
 * Author: BenChen
 * Date: 2024/01/12 13:49
 * Email:chenxiaobin@cloudhearing.cn
 */
data class CalendarPanelEntity(
    val date: Date,
    val topTime: String,
    val bottomTime: String,
    val isToday: Boolean,
    var selected: Boolean
)
