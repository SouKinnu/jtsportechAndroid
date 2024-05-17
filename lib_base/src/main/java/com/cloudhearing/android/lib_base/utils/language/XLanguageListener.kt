package com.cloudhearing.android.lib_base.utils.language

/**
 * Author: BenChen
 * Date: 2024/01/02 10:57
 * Email:chenxiaobin@cloudhearing.cn
 */
interface XLanguageListener {
    fun restartPage(): Class<*>

    fun getUseLocal(): String

    fun setUseLocal(str: String)
}