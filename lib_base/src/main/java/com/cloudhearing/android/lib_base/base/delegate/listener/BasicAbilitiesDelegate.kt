package com.cloudhearing.android.lib_base.base.delegate.listener

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle

/**
 * Author: BenChen
 * Date: 2023/12/19 17:12
 * Email:chenxiaobin@cloudhearing.cn
 */
interface BasicAbilitiesDelegate {
    fun registerBasicAbilitiesDelegate(
        activity: AppCompatActivity,
        lifecycle: Lifecycle,
        savedInstanceState: Bundle?
    )

    fun registerTransparentStatusBar(activity: AppCompatActivity, transparentStatusBar: Boolean)
    fun registerImmersiveStatusBar(activity: AppCompatActivity, transparentStatusBar: Boolean)
    fun registerPaddingSystemWindowInsets(
        activity: AppCompatActivity,
        marginTopSystemWindowInsets: Boolean,
        marginBottomSystemWindowInsets: Boolean
    )

    fun registerMarginBottomSystemWindowInsets(
        activity: AppCompatActivity,
        marginBottomSystemWindowInsets: Boolean
    )

    fun registerTransitionAnimation(
        activity: AppCompatActivity,
        enterAnimation: Int,
        exitAnimation: Int,
        popEnterAnimation: Int,
        popExitAnimation: Int
    )

    fun registerBaseContext(context: Context): Context
}