package com.cloudhearing.android.lib_common.utils.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Author: BenChen
 * Date: 2024/03/21 16:10
 * Email:chenxiaobin@cloudhearing.cn
 */
class ChannelEventProcessor<T> {
    private val channel = Channel<T>(Channel.BUFFERED)

    fun sendChannelEvent(event: T) {
        // 发送点击事件到 Channel
        channel.trySend(event)
    }

    fun observeChannelEvents(): Flow<T> {
        // 将 Channel 转换为 Flow
        return channel.receiveAsFlow()
    }
}