package com.jtsportech.visport.android.utils.helper

import android.content.Context
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.jtsportech.visport.android.BuildConfig
import com.tencent.bugly.crashreport.CrashReport

/**
 * Author: BenChen
 * Date: 2024/04/03 10:22
 * Email:chenxiaobin@cloudhearing.cn
 */
class SDKInitializationHelper private constructor(){

    private val context: Context by lazy {
        AppProvider.get()
    }

    companion object {

        @JvmStatic
        val INSTANCE: SDKInitializationHelper by lazy {
            SDKInitializationHelper()
        }
    }

    init {
        init()
    }

    private fun init() {
        UMHelper.INSTANCE.init()
        WechatHelper.INSTANCE
        QQHelper.INSTANCE
        CrashReport.initCrashReport(context, "e65e107514", BuildConfig.DEBUG)
    }
}