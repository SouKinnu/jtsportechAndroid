package com.jtsportech.visport.android.utils.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2023/06/01 11:27
 * Email:chenxiaobin@cloudhearing.cn
 *
 * [微信SDK接入文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html)
 */
class WechatHelper private constructor() {

    private val context: Context by lazy {
        AppProvider.get()
    }

    /**
     * IWXAPI 是第三方app和微信通信的openApi接口
     */
    private lateinit var api: IWXAPI

    companion object {
        /**
         * APP_ID 替换为你的应用从官方网站申请到的合法appID
         */
        const val APP_ID = "wx782cf7c0a139ed9c"

        /**
         * app 密钥
         */
        const val APP_SECRET = "d29e5b9b484dc019fe3a938a381d10b0"

        @JvmStatic
        val INSTANCE: WechatHelper by lazy {
            WechatHelper()
        }
    }

    init {
        regToWx()
    }

    private fun regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true)
        // 将应用的appId注册到微信
//        api.registerApp(APP_ID)

        // 建议动态监听微信启动广播进行注册到微信
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 14 或更高版本，必须加上 RECEIVER_EXPORTED 或者 RECEIVER_NOT_EXPORTED
            context.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Timber.d("注册微信 App id")
                    api.registerApp(APP_ID)
                }
            }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP), RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Timber.d("注册微信 App id")
                    api.registerApp(APP_ID)
                }
            }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
        }
    }

    fun handleIntent(intent: Intent, handler: IWXAPIEventHandler) {
        api.handleIntent(intent, handler)
    }

    fun handleResponse(resp: BaseResp?) {
        Timber.d("响应结果 openId ${resp?.openId} transaction ${resp?.transaction} type ${resp?.type} errCode ${resp?.errCode} code ${(resp as SendAuth.Resp).code}")
        mWechatHelperCallback?.handleResponse(resp.errCode, (resp as SendAuth.Resp).code)

        when (resp.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                // TODO 把后台需要 [appid] [secret] [code] [grant_type]  传过去，换取 accessToken ,再根据 accessToken 拉取个人信息，最后返回给前端
            }

            BaseResp.ErrCode.ERR_USER_CANCEL -> {

            }

            BaseResp.ErrCode.ERR_AUTH_DENIED -> {

            }

            else -> {

            }
        }
    }

    /**
     * 微信登录
     *
     */
    fun requestWeChatLogin() {
        // send oauth request
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"   // 只能填 snsapi_userinfo
        req.state = "wechat_sdk_wx_login"
        api.sendReq(req)
    }

    interface WechatHelperCallback {
        fun handleResponse(errCode: Int, code: String)
    }

    private var mWechatHelperCallback: WechatHelperCallback? = null

    fun registerWechatHelperCallback(listener: WechatHelperCallback) {
        mWechatHelperCallback = listener
    }

    fun unregisterWechatHelperCallback() {
        mWechatHelperCallback = null
    }
}