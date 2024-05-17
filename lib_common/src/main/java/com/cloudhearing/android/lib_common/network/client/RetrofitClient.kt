package com.cloudhearing.android.lib_common.network.client

import com.cloudhearing.android.lib_common.network.dataSource.JTSportechService
import com.cloudhearing.android.lib_common.network.dataSource.WeChatService
import okhttp3.OkHttpClient

/**
 * Author: BenChen
 * Date: 2023/05/24 15:42
 * Email:chenxiaobin@cloudhearing.cn
 */
object RetrofitClient : BaseRetrofitClient() {

    // 域名
    const val JTSPORTECH_DEV_BASE_URL = "https://test.sztcpay.com:10443"
    const val JTSPORTECH_PRO_BASE_URL = "https://visport.jtvisport.com"

    // 图片域名
    const val JTSPORTECH_IMAGE_DEV_BASE_URL = "https://test.sztcpay.com:10443/jtsport-file/image"
    const val JTSPORTECH_IMAGE_PRO_BASE_URL =
        "https://jtsport-1324787784.cos.ap-beijing.myqcloud.com/image"

    // 音频域名
    const val JTSPORTECH_AUDIO_DEV_BASE_URL =
        "https://jtsport-1324787784.cos.ap-beijing.myqcloud.com/audio"
    const val JTSPORTECH_AUDIO_PRO_BASE_URL =
        "https://jtsport-1324787784.cos.ap-beijing.myqcloud.com/audio"

    // 视频域名
    const val JTSPORTECH_VIDEO_DEV_BASE_URL =
        "https://jtsport-1324787784.cos.ap-beijing.myqcloud.com/video"
    const val JTSPORTECH_VIDEO_PRO_BASE_URL =
        "https://jtsport-1324787784.cos.ap-beijing.myqcloud.com"

    // 微信
    const val WECHAT_BASE_URL = "https://api.weixin.qq.com"

    val jtSportechService by lazy {
        getService(
            JTSportechService::class.java,
            JTSPORTECH_PRO_BASE_URL
        )
    }

    val weChatService by lazy {
        getService(
            WeChatService::class.java,
            WECHAT_BASE_URL
        )
    }

    override val isCertificateAuthentication: Boolean
        get() = true


    override fun handleBuilder(builder: OkHttpClient.Builder) = Unit
}