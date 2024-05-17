package com.cloudhearing.android.lib_base.base.delegate.listener

import androidx.lifecycle.Lifecycle
import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Author: BenChen
 * Date: 2023/12/19 17:59
 * Email:chenxiaobin@cloudhearing.cn
 */
interface CoroutineDelegate {
    val job: Job
    val mainScope: CoroutineScope
    val coroutineContext: CoroutineContext
    val dispatchers: CoroutineDispatchers
    fun registerCoroutine(lifecycle: Lifecycle)
}