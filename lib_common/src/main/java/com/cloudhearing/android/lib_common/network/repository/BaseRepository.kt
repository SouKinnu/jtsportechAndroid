package com.cloudhearing.android.lib_common.network.repository

import com.blankj.utilcode.BuildConfig
import com.cloudhearing.android.lib_base.concurrency.CoroutineDispatchers
import com.cloudhearing.android.lib_common.network.client.RetrofitClient
import com.cloudhearing.android.lib_network.utils.*
import com.cloudhearing.android.lib_common.network.exception.TokenInvalidException
import com.cloudhearing.android.lib_common.utils.GsonUtil
import com.cloudhearing.android.lib_common.utils.reLoginRequired
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2023/05/24 13:59
 * Email:chenxiaobin@cloudhearing.cn
 */
open class BaseRepository {

    private val mDispatchers: CoroutineDispatchers = CoroutineDispatchers()

    val mJTSportechService by lazy {
        RetrofitClient.jtSportechService
    }

    val mWeChatService by lazy {
        RetrofitClient.weChatService
    }

    suspend fun <T : Any> request(call: suspend () -> ApiResponse<T>): ApiResponse<T> {
        return withContext(mDispatchers.io) {
            call.invoke()
        }.apply {
            Timber.d("接口返回数据-----> $this")
        }
    }

    suspend fun <T : Any> requestHttp(block: suspend () -> T): T? {
        runCatching {
            block.invoke()
        }.onSuccess {
            Timber.i("$it")
            return it
        }.onFailure {
            return null
        }
        return null
    }

    suspend fun <T> executeHttp(
        block: suspend () -> ApiResponse<T>
    ): ApiResponse<T> {
        runCatching {
            block.invoke()
        }.onSuccess { data: ApiResponse<T> ->
            return handleHttpOk(data)
        }.onFailure {
            Timber.e("$it")
            return handleHttpException(it)
        }
        return ApiEmptyResponse()
    }


    /**
     * 非后台返回错误，捕获到的异常
     *
     * @param T
     * @param e
     * @return
     */
    private fun <T> handleHttpError(e: Throwable): ApiErrorResponse<T> {
        if (BuildConfig.DEBUG) e.printStackTrace()
//        handlingExceptions(e)
        return ApiErrorResponse(e)
    }


    /**
     * 非后台返回错误，捕获到的异常
     *
     * @param T
     * @param e
     * @return
     */
    private fun <T> handleHttpError(e: HttpException): ApiFailedResponse<T> {
        val responseBody = e.response()?.errorBody()?.string()
        val data = GsonUtil.gsonToBean(responseBody, ApiResponse::class.java)
        return ApiFailedResponse(data?.code, data?.msg)
    }


    /**
     * 非后台返回错误，捕获到的异常
     *
     * @param T
     * @param t
     * @return
     */
    private fun <T> handleHttpException(t: Throwable): ApiResponse<T> {
        return when (t) {
            is HttpException -> handleHttpError(t)
            is TokenInvalidException -> {
                reLoginRequired()
                ApiErrorResponse(t)
            }
            else -> ApiErrorResponse(t)
        }
    }


    /**
     * 判断是否请求成功
     *
     * @param T
     * @param data
     * @return
     */
    private fun <T> handleHttpOk(data: ApiResponse<T>): ApiResponse<T> =
        if (data.isSuccess) {
            getHttpSuccessResponse(data)
        } else {
            ApiFailedResponse(data.code, data.msg)
        }


    /**
     * 处理成功和数据为空
     *
     * @param T
     * @param response
     * @return
     */
    private fun <T> getHttpSuccessResponse(response: ApiResponse<T>): ApiResponse<T> {
        val data = response.data
        return if (data == null || data is List<*> && (data as List<*>).isEmpty()) {
            ApiEmptyResponse(
                response.code,
                response.msg
            )
        } else
            ApiSuccessApiResponse(data)
    }

    fun generateRequestBody(kv: HashMap<String, Any>): RequestBody {
        val requestData = JSONObject()

        kv.forEach {
            requestData.put(it.key, it.value)
        }

        return RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            requestData.toString()
        )
    }
}