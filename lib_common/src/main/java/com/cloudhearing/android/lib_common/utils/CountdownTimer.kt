package com.cloudhearing.android.lib_common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Author: BenChen
 * Date: 2024/02/28 19:22
 * Email:chenxiaobin@cloudhearing.cn
 */
class CountdownTimer {


    private var countdownJob: Job? = null

    fun startCountdown(timeInSeconds: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        cancelCountdown() // 取消之前的倒计时任务

        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            var remainingTime = timeInSeconds

            while (remainingTime > 0) {
                onTick(remainingTime)
                delay(1000) // 每隔一秒触发一次倒计时回调
                remainingTime--
            }

            onFinish()
        }
    }

    fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }
}