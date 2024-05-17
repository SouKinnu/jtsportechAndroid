package com.cloudhearing.android.lib_common.utils

class ResultBuilder<T> {
    var onSuccess: (data: T?) -> Unit = {}
    var onDataEmpty: () -> Unit = {}
    var onFailed: (errorCode: String?, errorMsg: String?) -> Unit = { _, errorMsg ->
        errorMsg?.let { /*toast(it)*/ }
    }
    var onError: (e: Throwable) -> Unit = { e ->
        e.message?.let { /*toast(it)*/ }
    }
    var onComplete: () -> Unit = {}
}