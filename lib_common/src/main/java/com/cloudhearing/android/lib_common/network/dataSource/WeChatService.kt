package com.cloudhearing.android.lib_common.network.dataSource

import com.cloudhearing.android.lib_common.network.dataSource.login.WeChatGetAccessToken
import com.cloudhearing.android.lib_network.utils.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Author: BenChen
 * Date: 2024/03/26 17:05
 * Email:chenxiaobin@cloudhearing.cn
 */

/**
 * [微信登录文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html)
 *
 */
interface WeChatService {

    @Headers("is-Data-Definitions:true")
    @GET("/sns/oauth2/access_token?")
    suspend fun getAccessToken(
        @Query("appid") appid: String,
        @Query("secret") secret: String,
        @Query("code") code: String,
        @Query("grant_type") grant_type: String,
    ): ApiResponse<WeChatGetAccessToken>
}