package com.cloudhearing.android.lib_common.utils

import android.os.Handler
import android.os.Looper

/**
 * Author: BenChen
 * Date: 2023/12/25 10:27
 * Email:chenxiaobin@cloudhearing.cn
 */
class PeriodicTaskExecutor(private val interval: Long, private val task: Runnable) {
    private var isRunning: Boolean = false
    private val handler: Handler = Handler(Looper.getMainLooper())

    private val periodicTask = object : Runnable {
        override fun run() {
            if (!isRunning) {
                return
            }
            task.run()
            handler.postDelayed(this, interval)
        }
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