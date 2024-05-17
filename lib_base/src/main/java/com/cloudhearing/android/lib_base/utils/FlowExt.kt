package com.cloudhearing.android.lib_base.utils

import android.view.View
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

/**
 * Author: BenChen
 * Date: 2023/05/26 16:09
 * Email:chenxiaobin@cloudhearing.cn
 */
fun View.clickFlow() = callbackFlow {
    setOnClickListener { this.trySend(Unit).isSuccess }
    awaitClose { setOnClickListener(null) }
}

/**
 *
 *
 * @param T
 * @param thresholdMillis   毫秒
 * @return
 */
fun <T> Flow<T>.throttleFirst(thresholdMillis: Long): Flow<T> = flow {
    var lastTime = 0L // 上次发射数据的时间
    //收集数据
    collect { upstream ->
        // 当前时间
        val currentTime = System.currentTimeMillis()
        // 时间差超过阈值则发送数据并记录时间
        if (currentTime - lastTime > thresholdMillis) {
            lastTime = currentTime
            emit(upstream)
        }
    }
}