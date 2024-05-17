package com.cloudhearing.android.lib_common.utils

import android.os.Handler
import android.os.Looper

/**
 * Author: BenChen
 * Date: 2023/12/25 10:32
 * Email:chenxiaobin@cloudhearing.cn
 */
class ScheduledTaskExecutor(private val interval: Long, private val task: Runnable) {
    private var isRunning: Boolean = false
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val periodicTask = Runnable {
        task.run()
        isRunning = false
    }

    fun start() {
        if (isRunning) {
            return
        }
        isRunning = true
        handler.postDelayed(periodicTask, interval)
    }

    fun stop() {
        if (!isRunning) {
            return
        }
        isRunning = false
        handler.removeCallbacks(periodicTask)
    }
}