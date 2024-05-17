package com.cloudhearing.android.lib_common.utils

import com.cloudhearing.android.lib_base.utils.TOKEN_INVALID
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * Author: BenChen
 * Date: 2023/05/24 14:11
 * Email:chenxiaobin@cloudhearing.cn
 */
internal fun reLoginRequired() {
    LiveEventBus.get<Boolean>(TOKEN_INVALID).post(true)
}