package com.jtsportech.visport.android.utils.startup

import android.content.Context


import com.rousetime.android_startup.AndroidStartup
import timber.log.Timber
import java.util.logging.Logger

/**
 * Author: BenChen
 * Date: 2023/12/26 15:56
 * Email:chenxiaobin@cloudhearing.cn
 */
class TimberStartup : AndroidStartup<Void>() {
    override fun callCreateOnMainThread(): Boolean = false

    override fun create(context: Context): Void? {
        initTimber()

        return null
    }

    override fun waitOnMainThread(): Boolean = false

    private fun initTimber() {
        Timber.plant(LoggingTree())
//        if (BuildConfig.DEBUG) Timber.plant(LoggingTree()) else Timber.plant(NotLoggingTree())
    }

    /**
     * Used to generate Logging tree prepended with cloudhearing as a custom TAG so it is easier to find.
     */
    inner class LoggingTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return "cloudhearing (${element.fileName}:${element.lineNumber})${element.methodName}()"
        }
    }

    /**
     * Used to generate NotLogging tree which is used in release build.
     */
    inner class NotLoggingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            when (priority) {
            }
        }
    }
}