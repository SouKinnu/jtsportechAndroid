package com.jtsportech.visport.android.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2023/06/01 11:40
 * Email:chenxiaobin@cloudhearing.cn
 *
 *
 * [微信SDK接入文档](https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/Android.html)
 */
class WXEntryActivity : Activity(), IWXAPIEventHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WechatHelper.INSTANCE.handleIntent(intent, this)
    }

    /**
     * 微信发送的请求将回调到 [onReq] 方法
     *
     * @param p0
     */
    override fun onReq(p0: BaseReq?) {
        // 必须关闭此页面，不然会有透明页面遮挡原本的页面，无法操作
        finish()
    }


    /**
     * 发送到微信请求的响应结果将回调到 [onResp] 方法
     *
     * @param p0
     */
    override fun onResp(p0: BaseResp?) {
        // 必须关闭此页面，不然会有透明页面遮挡原本的页面，无法操作
        finish()

        WechatHelper.INSTANCE.handleResponse(p0)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        WechatHelper.INSTANCE.handleIntent(intent, this)
        // 必须关闭此页面，不然会有透明页面遮挡原本的页面，无法操作
        finish()
    }
}