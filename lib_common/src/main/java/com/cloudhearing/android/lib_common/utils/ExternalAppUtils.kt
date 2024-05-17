package com.cloudhearing.android.lib_common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/04/10 11:46
 * Email:chenxiaobin@cloudhearing.cn
 */
object ExternalAppUtils {

    fun openUrlInBrowser(activity: FragmentActivity, url: String) {
        Timber.d("url $url")

        if (url.isEmpty()){
            return
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(intent)
    }
}