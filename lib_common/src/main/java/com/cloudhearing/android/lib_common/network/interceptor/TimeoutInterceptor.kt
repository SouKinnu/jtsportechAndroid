package com.cloudhearing.android.lib_common.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


/**
 * 设置单独的超时时间
 * 使用 @Headers({"CONNECT_TIMEOUT:1800000", "READ_TIMEOUT:1800000", "WRITE_TIMEOUT:1800000"})
 */

class TimeoutInterceptor : Interceptor {

    private companion object {
        const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "READ_TIMEOUT"
        const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        val request = chain.request()
        val connectNew = request.header(CONNECT_TIMEOUT)
        val readNew = request.header(READ_TIMEOUT)
        val writeNew = request.header(WRITE_TIMEOUT)

        if (!connectNew.isNullOrEmpty()) {
            connectTimeout = connectNew.toInt()
        }

        if (!readNew.isNullOrEmpty()) {
            readTimeout = readNew.toInt()
        }

        if (!writeNew.isNullOrEmpty()) {
            writeTimeout = writeNew.toInt()
        }

        return chain
            .withConnectTimeout(connectTimeout, TimeUnit.SECONDS)
            .withReadTimeout(readTimeout, TimeUnit.SECONDS)
            .withWriteTimeout(writeTimeout, TimeUnit.SECONDS)
            .proceed(request)
    }
}