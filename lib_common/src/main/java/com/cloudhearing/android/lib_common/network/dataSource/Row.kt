package com.cloudhearing.android.lib_common.network.dataSource

/**
 * Author: BenChen
 * Date: 2024/03/05 19:48
 * Email:chenxiaobin@cloudhearing.cn
 */
open class Row<out T>(
    open val list: List<T>,
    open val total: Int
)
