package com.cloudhearing.android.lib_common.utils

/**
 * Author: BenChen
 * Date: 2024/02/27 16:25
 * Email:chenxiaobin@cloudhearing.cn
 */
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadPoolManager {
    private var executorService: ThreadPoolExecutor? = null

    fun getThreadPool(): ThreadPoolExecutor {
        if (executorService == null) {
            synchronized(ThreadPoolManager::class.java) {
                if (executorService == null) {
                    executorService = ThreadPoolExecutor(
                        5, // 核心线程数
                        10, // 最大线程数
                        0L, // 线程空闲时间
                        TimeUnit.MILLISECONDS, // 时间单位
                        LinkedBlockingQueue<Runnable>() // 任务队列
                    )
                }
            }
        }
        return executorService!!
    }
}