package com.cloudhearing.android.lib_base.utils.context

import android.content.Context


object AppProvider {

    private val instance: Context by lazy {
        AppContentProvider.autoContext!!
    }

    @JvmStatic
    fun get() = instance
}