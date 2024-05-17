package com.cloudhearing.android.lib_common.network.interceptor

import com.cloudhearing.android.lib_common.network.client.BaseRetrofitClient
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber


/**
 * 设置 Token
 * 使用 @Headers({"mp-token:true"})
 */
class JTSportechAuthenticatorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authorization = request.headers[BaseRetrofitClient.X_Auth_Token]

        if (!authorization.isNullOrEmpty()) {
            return chain.proceed(buildRequest(PreferencesWrapper.get().getAccessToken(), request))
        }

        return chain.proceed(request)
    }


    /**
     * 构建请求体
     *
     * @param token
     * @param request
     * @return
     */
    private fun buildRequest(token: String, request: Request): Request {
        val newHeaders = Headers.Builder()

        // 拿到除了 [BaseRetrofitClient.MP_TOKEN] 之外的 headers
        val beforeOtherHeaders = request.headers.filter {
            it.first != BaseRetrofitClient.X_Auth_Token
        }

        // 添加到新 headers
        beforeOtherHeaders.forEach {
            newHeaders.add(it.first, it.second)
        }

        Timber.d("X-Auth-Token $token")

        val headers =
            newHeaders
                .add(BaseRetrofitClient.X_Auth_Token, "Bearer $token")
                .build()

        return request
            .newBuilder()
            .headers(headers)
            .build()
    }
}