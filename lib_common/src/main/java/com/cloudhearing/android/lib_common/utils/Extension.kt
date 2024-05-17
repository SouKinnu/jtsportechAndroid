package com.cloudhearing.android.lib_common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.cloudhearing.android.lib_network.utils.ApiEmptyResponse
import com.cloudhearing.android.lib_network.utils.ApiErrorResponse
import com.cloudhearing.android.lib_network.utils.ApiFailedResponse
import com.cloudhearing.android.lib_network.utils.ApiResponse
import com.cloudhearing.android.lib_network.utils.ApiSuccessApiResponse


/**
 * Author: BenChen
 * Date: 2023/05/24 15:58
 * Email:chenxiaobin@cloudhearing.cn
 */

fun <T> ApiResponse<T>.parseData(listenerBuilder: ResultBuilder<T>.() -> Unit) {
    val listener = ResultBuilder<T>().also(listenerBuilder)
    when (this) {
        is ApiSuccessApiResponse -> listener.onSuccess(this.response)
//        is ApiEmptyResponse -> listener.onDataEmpty()
        is ApiEmptyResponse -> listener.onSuccess(null)
        is ApiFailedResponse -> listener.onFailed(this.code, this.msg)
        is ApiErrorResponse -> listener.onError(this.throwable)
    }
    listener.onComplete()
}


fun Context.startCallPhoneActivity(number: String) {
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
}