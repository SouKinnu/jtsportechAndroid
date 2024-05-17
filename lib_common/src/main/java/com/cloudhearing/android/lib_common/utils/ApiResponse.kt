package com.cloudhearing.android.lib_network.utils

import java.io.Serializable

/**
 * Author: BenChen
 * Date: 2023/05/24 10:47
 * Email:chenxiaobin@cloudhearing.cn
 */

/**
 * 网络请求最外层视图
 *
 * @param T         具体的实体
 * @property code   响应的状态
 * @property msg    响应的消息
 * @property data   响应的具体数据
 * @property error
 */
open class ApiResponse<out T>(
    open val code: String? = null,
    open val msg: String? = null,
    open val data: T? = null,
    open val error: Throwable? = null
) : Serializable {
    val isSuccess: Boolean
        get() = code == "200" || code == "00000"
}

data class ApiSuccessApiResponse<T>(val response: T) : ApiResponse<T>(data = response)

data class ApiEmptyResponse<T>(
    override val code: String? = null,
    override val msg: String? = null,
) : ApiResponse<T>(
    code = code,
    msg = msg,
    data = null
)

data class ApiFailedResponse<T>(
    override val code: String?,
    override val msg: String?
) : ApiResponse<T>(code = code, msg = msg, data = null)

data class ApiErrorResponse<T>(val throwable: Throwable) : ApiResponse<T>(error = throwable)

