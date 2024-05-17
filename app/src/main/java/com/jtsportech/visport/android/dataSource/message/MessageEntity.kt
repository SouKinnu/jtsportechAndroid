package com.jtsportech.visport.android.dataSource.message

import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.StringRes

/**
 * Author: BenChen
 * Date: 2024/01/03 16:07
 * Email:chenxiaobin@cloudhearing.cn
 */
data class MessageEntity(
    @MessageType
    val type: Int,
    @DrawableRes val iconRes: Int,
    val title: String,
    val subtitle: String,
    val time: String,
    val hasNewMessage: Boolean
)


@IntDef(
    MessageType.TOURNAMENT_NOTIFICATIONS,
    MessageType.APP_MESSAGES,
    MessageType.INTERACTIVE_MESSAGES,
)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class MessageType {

    companion object {
        /**
         * 比赛通知
         */
        const val TOURNAMENT_NOTIFICATIONS = 0

        /**
         * 应用消息
         */
        const val APP_MESSAGES = 1

        /**
         * 互动消息
         */
        const val INTERACTIVE_MESSAGES = 2
    }
}
