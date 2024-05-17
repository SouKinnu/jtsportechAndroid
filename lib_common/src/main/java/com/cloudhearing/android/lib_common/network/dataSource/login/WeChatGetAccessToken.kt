package com.cloudhearing.android.lib_common.network.dataSource.login

data class WeChatGetAccessToken(
    val access_token: String?,
    val expires_in: Int?,
    val openid: String?,
    val refresh_token: String?,
    val scope: String?,
    val unionid: String?
)