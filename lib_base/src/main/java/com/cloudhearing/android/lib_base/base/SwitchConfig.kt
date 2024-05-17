package com.cloudhearing.android.lib_base.base

import com.cloudhearing.android.lib_base.R


/**
 * Author: BenChen
 * Date: 2023/12/19 17:45
 * Email:chenxiaobin@cloudhearing.cn
 */
interface SwitchConfig {
    val transparentStatusBar: Boolean
        get() = true
    val paddingTopSystemWindowInsets: Boolean
        get() = false

    val paddingBottomSystemWindowInsets: Boolean
        get() = false

    val enterAnimation: Int
        get() = R.anim.slide_in_right
    val exitAnimation: Int
        get() = R.anim.slide_out_left
    val popEnterAnimation: Int
        get() = R.anim.slide_in_left
    val popExitAnimation: Int
        get() = R.anim.slide_out_right
}