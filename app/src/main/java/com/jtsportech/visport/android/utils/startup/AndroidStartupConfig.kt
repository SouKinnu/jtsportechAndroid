package com.jtsportech.visport.android.utils.startup

import com.blankj.utilcode.BuildConfig
import com.rousetime.android_startup.model.LoggerLevel
import com.rousetime.android_startup.model.StartupConfig
import com.rousetime.android_startup.provider.StartupProviderConfig


/**
 * Author: BenChen
 * Date: 2021/11/19 14:37
 * Email:chenxiaobin@cloudhearing.cn
 */
class AndroidStartupConfig : StartupProviderConfig {

    override fun getConfig(): StartupConfig =
        StartupConfig.Builder()
//            .setLoggerLevel(if (BuildConfig.DEBUG) LoggerLevel.DEBUG else LoggerLevel.NONE)
            .setLoggerLevel(LoggerLevel.DEBUG)
            .build()
}