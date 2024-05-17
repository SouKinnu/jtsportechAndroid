package com.cloudhearing.android.lib_common.network.interceptor

import com.cloudhearing.android.lib_common.network.client.BaseRetrofitClient
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Author: BenChen
 * Date: 2024/01/05 17:29
 * Email:chenxiaobin@cloudhearing.cn
 */
class ManagerDataDefinitionsIntercepter : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val is_Data_Definitions = request.headers[BaseRetrofitClient.is_Data_Definitions]

        if (!is_Data_Definitions.isNullOrEmpty()) {
            BaseRetrofitClient.addDataDefinitionsPath(request.url.encodedPath)
        }

        return chain.proceed(request)
    }
}