package com.cloudhearing.android.lib_common.network.repository

import com.cloudhearing.android.lib_common.network.dataSource.messages.MessageNotice
import com.cloudhearing.android.lib_common.network.dataSource.messages.Version
import com.cloudhearing.android.lib_network.utils.ApiResponse

/**
 * Author: BenChen
 * Date: 2024/03/29 14:36
 * Email:chenxiaobin@cloudhearing.cn
 */
class MessageRepository : BaseRepository() {

    suspend fun getMessageNotice(
        keyword: String,
        msgType: String
    ): ApiResponse<List<MessageNotice>> {
        return executeHttp {
            mJTSportechService.getMessageNotice(keyword, msgType)
        }
    }

    suspend fun getAppVersion(): ApiResponse<Version> {
        return executeHttp {
            mJTSportechService.getAppVersion()
        }
    }

    suspend fun updateMsgNotice(
        ids: String,
        msgStatus: String,
        msgType: String
    ): ApiResponse<Unit> {
        return executeHttp {
            mJTSportechService.updateMsgNotice(ids, msgStatus, msgType)
        }
    }
}