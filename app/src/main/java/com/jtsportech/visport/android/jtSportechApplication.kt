package com.jtsportech.visport.android

import android.app.Application
import com.jtsportech.visport.android.utils.helper.SDKInitializationHelper
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager


/**
 * Author: BenChen
 * Date: 2024/01/02 10:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class jtSportechApplication : Application() {

//    override fun attachBaseContext(base: Context) {
//        Timber.d("attachBaseContext")
//        super.attachBaseContext(XLanguageUtils.attachBaseContext(base))
//    }

    override fun onCreate() {
        super.onCreate()
        SDKInitializationHelper.INSTANCE
    }
}