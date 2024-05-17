package com.cloudhearing.android.lib_base.utils

import androidx.annotation.StringRes


interface HasLoadingOverlay {
    fun showLoadingScreen(@StringRes messageId: Int)
    fun showLoadingScreen(message: String)
    fun showLoadingScreenWithNoMessage()
    fun hideLoadingScreen()
}