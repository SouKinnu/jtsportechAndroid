package com.cloudhearing.android.lib_base.utils.language

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.startActivity
import com.blankj.utilcode.util.AppUtils
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import timber.log.Timber
import java.util.Locale

/**
 * Author: BenChen
 * Date: 2023/12/29 17:23
 * Email:chenxiaobin@cloudhearing.cn
 */
object XLanguageUtils {

    private val handler = Handler(Looper.getMainLooper())

    private var mListener: XLanguageListener? = null

    fun setXLanguageListener(listener: XLanguageListener) {
        mListener = listener
    }

    /**
     * @sample applyLanguage(Locale.ENGLISH, false)
     * @param locale
     * @param isRelaunchApp
     */
    fun applyLanguage(locale: Locale, isRelaunchApp: Boolean) {
        if (mListener == null) {
            throw Exception("no config XLanguageListener")
        }

        applyLanguageReal(locale, isRelaunchApp)
    }

    /**
     * 一定要在 Activity 的基类或者 Application 里面配置，不然不会生效的
     *
     *
     * @param context
     * @return
     */
    fun attachBaseContext(context: Context): Context {
        val useLocal = mListener?.getUseLocal() ?: ""

        if (useLocal.isEmpty()) {
            return context
        }

        val settingsLocale = string2Locale(useLocal) ?: return context

        val resources = context.resources
        val config = resources.configuration

        setLocal(config, settingsLocale)

        return context.createConfigurationContext(config)
    }

    private fun applyLanguageReal(locale: Locale, isRelaunchApp: Boolean) {
        mListener?.setUseLocal(locale2String(locale))

        pollCheckAppContextLocal(locale, 0) {
            if (it) {
                restart(isRelaunchApp)
            } else {
                AppUtils.relaunchApp()
            }
        }
    }

    private fun restart(isRelaunchApp: Boolean) {
        Timber.d("isRelaunchApp:$isRelaunchApp")
        if (isRelaunchApp) {
            AppUtils.relaunchApp()
        } else {
            val restartPage = mListener?.restartPage()

            if (restartPage != null) {
                startActivity(
                    AppProvider.get().applicationContext,
                    Intent(AppProvider.get().applicationContext, restartPage).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    },
                    null
                )

                return
            }

            Timber.e("no config restartPage!")
        }
    }

    private fun locale2String(locale: Locale): String {
        return "${locale.language}$${locale.country}"
    }

    private fun string2Locale(str: String): Locale? {
        val locale = stringToLocaleReal(str)
        if (locale == null) {
            Timber.e("The string of $str  is not in the correct format.")
            mListener?.setUseLocal("")
        }
        return locale
    }

    private fun stringToLocaleReal(str: String): Locale? {
        if (!isRightFormatLocalStr(str)) {
            return null
        }

        try {
            val splitIndex = str.indexOf("$")
            return Locale(str.substring(0, splitIndex), str.substring(splitIndex + 1))
        } catch (ignore: Exception) {
            return null
        }
    }

    private fun isRightFormatLocalStr(localStr: String): Boolean {
        val chars = localStr.toCharArray()
        var count = 0
        for (c in chars) {
            if (c == '$') {
                if (count >= 1) {
                    return false
                }
                ++count
            }
        }
        return count == 1
    }

    private fun getLocal(configuration: Configuration): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            configuration.locale
        }
    }

    private fun setLocal(configuration: Configuration, locale: Locale) {
        configuration.setLocale(locale)
    }

    private fun pollCheckAppContextLocal(
        destLocale: Locale,
        index: Int,
        callback: (Boolean) -> Unit
    ) {
        val appResources = AppProvider.get().resources
        val appConfig = appResources.configuration
        val appLocal = getLocal(appConfig)

        setLocal(appConfig, destLocale)

        appResources.updateConfiguration(appConfig, appResources.displayMetrics)

        if (isSameLocale(appLocal, destLocale)) {
            Timber.d("isSameLocale index:$index")
            callback.invoke(true)
        } else {
            if (index < 20) {
                handler.postDelayed({
                    pollCheckAppContextLocal(destLocale, index + 1, callback)
                }, 16)
                return
            }
            Timber.e("appLocal didn't update.")
            callback.invoke(false)
        }
    }

    private fun isSameLocale(l0: Locale, l1: Locale): Boolean {
        return (l0.language === l1.language) && (l0.country === l1.country)
    }

}