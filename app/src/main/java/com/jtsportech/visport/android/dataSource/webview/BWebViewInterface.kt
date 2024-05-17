package com.ruijin.android.rainbow.webview


import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceResponse


interface BWebViewInterface {
    /**
     * @sample 加载地址
     * @param url 地址
     * @param onPageStartedListenner 页面开始加载回调
     * @param onPageFinishedListenner 页面加载完成回调
     * @param onPageErrorListenner 页面加载失败回调
     * @param onProgressChangedListener 页面加载进度回调
     * @param onReceivedIconListener 页面加载页面图标回调
     * @param onReceivedTitleListener 页面加载页面标题回调
     */
    fun loadWeb(
        url: String,
        onPageStartedListenner: (() -> Unit)? = null,
        onPageFinishedListenner: (() -> Unit)? = null,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?, error: WebResourceError?) -> Unit)? = null,
        onProgressChangedListener: ((newProgress: Int) -> Unit)? = null,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)? = null,
        onReceivedTitleListener: ((title: String?) -> Unit)? = null
    )

    /**
     * @sample 加载地址
     * @param htmlData html完整代码
     * @param mimeType html mimetype
     * @param encoding html 编码
     * @param onPageStartedListenner 页面开始加载回调
     * @param onPageFinishedListenner 页面加载完成回调
     * @param onPageErrorListenner 页面加载失败回调
     * @param onProgressChangedListener 页面加载进度回调
     * @param onReceivedIconListener 页面加载页面图标回调
     * @param onReceivedTitleListener 页面加载页面标题回调
     */
    fun loadDataWeb(
        htmlData: String,
        mimeType: String ?= "text/html",
        encoding: String ?= "utf-8",
        onPageStartedListenner: (() -> Unit)? = null,
        onPageFinishedListenner: (() -> Unit)? = null,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?,error: WebResourceError?) -> Unit)? = null,
        onProgressChangedListener: ((newProgress: Int) -> Unit)? = null,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)? = null,
        onReceivedTitleListener: ((title: String?) -> Unit)? = null
    )

    /**
     * @sample 调用js方法
     * @param jsMethodName js里面的方法名字
     * @param jsMethodParams js里面的参数
     */
    fun toJsMethod(jsMethodName: String, jsMethodParams: ArrayList<String>?)

    /**
     * @sample 设置js可以调用Android的代码方法，方法必须使用@jvascriptInterface注解
     * @param mJsBridgeInterface 接口
     * @param name 交互协议；例如：name:aos,那么js就需要window.aos.[android-method-name]()
     */
    fun setJsBridge(mJsBridgeInterface: JsBridgeInterface, name: String)
}