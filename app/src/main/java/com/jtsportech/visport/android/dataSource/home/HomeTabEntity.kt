package com.jtsportech.visport.android.dataSource.home

/**
 * Author: BenChen
 * Date: 2024/01/08 16:45
 * Email:chenxiaobin@cloudhearing.cn
 */
data class HomeTabEntity(
    val tabText: String,
    val selected: Boolean,
    val isMore: Boolean,
    val isOpenMorePop: Boolean
)