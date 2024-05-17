package com.jtsportech.visport.android.utils.startup


import android.content.Context
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.rousetime.android_startup.AndroidStartup

/**
 * Author: BenChen
 * Date: 2023/12/26 15:56
 * Email:chenxiaobin@cloudhearing.cn
 */
class UMStartup : AndroidStartup<Void>() {
    override fun callCreateOnMainThread(): Boolean = true

    override fun create(context: Context): Void? {
        UMHelper.INSTANCE

        return null
    }

    override fun waitOnMainThread(): Boolean = false

}