package com.cloudhearing.android.lib_common.network.repository

import com.cloudhearing.android.lib_common.network.dataSource.login.WeChatGetAccessToken
import com.cloudhearing.android.lib_network.utils.ApiResponse

/**
 * Author: BenChen
 * Date: 2024/03/26 17:08
 * Email:chenxiaobin@cloudhearing.cn
 */
class WeChatRepository : BaseRepository() {

    suspend fun getAccessToken(
        appid: String,
        secret: String,
        code: String,
        grant_type: String
    ): ApiResponse<WeChatGetAccessToken> {
        return executeHttp {
            mWeChatService.getAccessToken(appid, secret, code, grant_type)
        }
    }
}