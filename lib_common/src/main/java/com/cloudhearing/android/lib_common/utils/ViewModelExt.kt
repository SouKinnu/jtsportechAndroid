package com.cloudhearing.android.lib_common.utils

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_network.utils.ApiResponse
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2023/05/24 15:49
 * Email:chenxiaobin@cloudhearing.cn
 */


/**
 * 封装的网络请求
 *
 * @param T
 * @param isLoading
 * @param block
 * @param success
 * @param failed
 * @param error
 */
fun <T> BaseViewModel.launchRequest(
    isLoading: Boolean = false,
    block: suspend () -> ApiResponse<T>,
    success: suspend (T?) -> Unit,
    failed: suspend (errorCode: String?, errorMsg: String?) -> Unit = { _, _ -> },
    error: suspend (throwable: Throwable) -> Unit
) {
    launchUi {
        if (isLoading) {
            loadState.setEvent(LoadState.Start())
//            loadState.value = LoadState.Start()
        }

        val result = withContext(coroutineDispatchers.io) {
            block()
        }

        Timber.d("请求结果 $result")

        result.parseData {
            onSuccess = {
                launchUi {
                    success.invoke(it)
                }
            }
            onFailed = { errorCode, errorMsg ->
                launchUi {
                    failed.invoke(errorCode, errorMsg)
                    loadState.setEvent(LoadState.Error(errorCode.orEmpty(), errorMsg.orEmpty()))
//                    loadState.value = LoadState.Error(errorMsg.orEmpty())
                }
            }
            onError = {
                launchUi {
                    error.invoke(it)

                    loadState.setEvent(LoadState.Error("", it.message.orEmpty()))
//                    loadState.value = LoadState.Error(it.message.orEmpty())
                }
            }
        }

        if (isLoading) {
            loadState.setEvent(LoadState.Finish)
//            loadState.value = LoadState.Finish
        }
    }
}


/**
 * 封装的网络请求
 *
 * @param T
 * @param block
 * @param start
 * @param success
 * @param failed
 * @param error
 * @param complete
 */
//fun <T> BaseViewModel.launchRequest(
//    isLoading: Boolean = false,
//    block: suspend () -> ApiResponse<T>,
//    start: () -> Unit,
//    success: (T?) -> Unit,
//    failed: (errorCode: String?, errorMsg: String?) -> Unit = { _, _ -> },
//    error: (throwable: Throwable) -> Unit,
//    complete: () -> Unit
//) {
//    launchUi {
//        if (isLoading) {
//            loadState.value = LoadState.Start()
//        }
//
//        start.invoke()
//
//        val result = withContext(coroutineDispatchers.io) {
//            block()
//        }
//
//        result.parseData {
//            onSuccess = {
//                success.invoke(it)
//            }
//            onFailed = { errorCode, errorMsg ->
//                failed.invoke(errorCode, errorMsg)
//            }
//            onError = {
//                error.invoke(it)
//            }
//        }
//
//        if (isLoading) {
//            loadState.value = LoadState.Finish
//        }
//
//        complete.invoke()
//    }
//}