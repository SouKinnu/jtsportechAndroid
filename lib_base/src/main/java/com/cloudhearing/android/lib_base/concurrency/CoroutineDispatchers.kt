package com.cloudhearing.android.lib_base.concurrency

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Author: BenChen
 * Date: 2023/12/19 18:12
 * Email:chenxiaobin@cloudhearing.cn
 */
class CoroutineDispatchers : ThreadDispatcher<CoroutineDispatcher> {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val computation: CoroutineDispatcher
        get() = Dispatchers.Default
    override val ui: CoroutineDispatcher
        get() = Dispatchers.Main
}