package com.jtsportech.visport.android.utils.startup

import android.content.Context
import com.cloudhearing.android.lib_base.utils.language.XLanguageListener
import com.cloudhearing.android.lib_base.utils.language.XLanguageUtils
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.rousetime.android_startup.AndroidStartup

/**
 * Author: BenChen
 * Date: 2024/01/02 11:04
 * Email:chenxiaobin@cloudhearing.cn
 */
class LanguageStartup : AndroidStartup<Void>() {
    override fun callCreateOnMainThread(): Boolean = false

    override fun create(context: Context): Void? {
        XLanguageUtils.setXLanguageListener(mXLanguageListener)

        return null
    }

    override fun waitOnMainThread(): Boolean = false

    private val mXLanguageListener = object : XLanguageListener {
        override fun restartPage(): Class<*> = DashboardActivity::class.java

        override fun getUseLocal(): String = PreferencesWrapper.get().getUseLocal()

        override fun setUseLocal(str: String) {
            PreferencesWrapper.get().setUseLocal(str)
        }
    }

}