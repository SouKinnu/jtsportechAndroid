package com.jtsportech.visport.android.components.webview


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import com.ruijin.android.rainbow.webview.BWebViewInterface
import com.jtsportech.visport.android.dataSource.webview.BWebViewLoadType
import com.ruijin.android.rainbow.webview.JsBridgeInterface
import timber.log.Timber

class BWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, val openDefaultConfig: Boolean = true
) : WebView(context, attrs), BWebViewInterface {

    private var isInitListenered = false
    private var mBWebViewLoadType: BWebViewLoadType? = null

    init {
        init()
    }

    private fun init() {
        settings.apply {
            if (openDefaultConfig) {
                // 设置true,才能让Webivew支持<meta>标签的viewport属性
                useWideViewPort = true
                // 设置可以支持缩放
                setSupportZoom(true)
                // 设置出现缩放工具
                builtInZoomControls = true
                // 设定缩放控件隐藏
                displayZoomControls = false
                // 开启 javaScript
                javaScriptEnabled = true
                // 启用 Dom 存储,不然网络链接加载不出来
                domStorageEnabled = true
            }
        }
    }

//    companion object {
//        open val ANDROID_ASSET_INDEX = "file:///android_asset/"
//    }

    override fun loadWeb(
        url: String,
        onPageStartedListenner: (() -> Unit)?,
        onPageFinishedListenner: (() -> Unit)?,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?, error: WebResourceError?) -> Unit)?,
        onProgressChangedListener: ((newProgress: Int) -> Unit)?,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)?,
        onReceivedTitleListener: ((title: String?) -> Unit)?
    ) {
        if (url.indexOf("http") == 0 || url.indexOf("https") == 0) {
            mBWebViewLoadType = BWebViewLoadType.HTTP
//        } else if (url.indexOf(ANDROID_ASSET_INDEX) == 0) {
//            mBWebViewLoadType = BWebViewLoadType.ASSETS
        }

        Timber.d("url: $url")

        post {
            loadUrl(url)
        }

        setListener(
            onPageStartedListenner,
            onPageFinishedListenner,
            onPageErrorListenner,
            onProgressChangedListener,
            onReceivedIconListener,
            onReceivedTitleListener
        )
    }

    private fun setListener(
        onPageStartedListenner: (() -> Unit)?,
        onPageFinishedListenner: (() -> Unit)?,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?, error: WebResourceError?) -> Unit)?,
        onProgressChangedListener: ((newProgress: Int) -> Unit)?,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)?,
        onReceivedTitleListener: ((title: String?) -> Unit)?
    ) {
        if (!isInitListenered) {
            webListenter(
                onPageStartedListenner,
                onPageFinishedListenner,
                onPageErrorListenner,
                onProgressChangedListener,
                onReceivedIconListener,
                onReceivedTitleListener
            )
            isInitListenered = true
        }
    }

    override fun loadDataWeb(
        htmlData: String, mimeType: String?, encoding: String?,
        onPageStartedListenner: (() -> Unit)?,
        onPageFinishedListenner: (() -> Unit)?,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?, error: WebResourceError?) -> Unit)?,
        onProgressChangedListener: ((newProgress: Int) -> Unit)?,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)?,
        onReceivedTitleListener: ((title: String?) -> Unit)?
    ) {
        post {
            loadData(htmlData, mimeType, encoding)
        }

        setListener(
            onPageStartedListenner,
            onPageFinishedListenner,
            onPageErrorListenner,
            onProgressChangedListener,
            onReceivedIconListener,
            onReceivedTitleListener
        )
    }

    override fun toJsMethod(jsMethodName: String, jsMethodParams: ArrayList<String>?) {
        var params: String = ""
        val jsMethodParamsSize = jsMethodParams?.size ?: 0
        jsMethodParams?.forEachIndexed { index, s ->
            if (index != (jsMethodParamsSize - 1)) {
                params += s
            } else {
                params += s + ","
            }
        }
        post {
            if (jsMethodParams.isNullOrEmpty()) {
                loadUrl("javascript:" + jsMethodName + "()")
            } else {
                loadUrl("javascript:" + jsMethodName + "(" + params + ")")
            }
        }


    }


    @SuppressLint("JavascriptInterface")
    override fun setJsBridge(mJsBridgeInterface: JsBridgeInterface, name: String) {
        addJavascriptInterface(mJsBridgeInterface, name)
    }

    private fun webListenter(
        onPageStartedListenner: (() -> Unit)?,
        onPageFinishedListenner: (() -> Unit)?,
        onPageErrorListenner: ((errorResponse: WebResourceResponse?, error: WebResourceError?) -> Unit)?,
        onProgressChangedListener: ((newProgress: Int) -> Unit)?,
        onReceivedIconListener: ((icon: Bitmap?) -> Unit)?,
        onReceivedTitleListener: ((title: String?) -> Unit)?
    ) {
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.e("++++++", "onPageFinished")
                if (onPageFinishedListenner != null) {
                    onPageFinishedListenner()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.e("++++++", "onPageStarted")
                if (onPageStartedListenner != null) {
                    onPageStartedListenner()
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("++++++", "onReceivedError")
                if (onPageErrorListenner != null) {
                    onPageErrorListenner(null, error)
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e("++++++++", "onReceivedHttpError" + errorResponse)
                if (onPageErrorListenner != null) {
                    onPageErrorListenner(errorResponse, null)
                }
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.e("+++++", "onPageErrorListenner")
                if (onPageErrorListenner != null) {
                    onPageErrorListenner(null, null)
                }
            }

        }
        webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.e("++++++", "onProgressChanged" + newProgress)
                if (onProgressChangedListener != null) {
                    onProgressChangedListener(newProgress)
                }
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
                Log.e("++++++", "onReceivedIcon")
                if (onReceivedIconListener != null) {
                    onReceivedIconListener(icon)
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                Log.e("++++++", "onReceivedTitle")
                if (onReceivedTitleListener != null) {
                    onReceivedTitleListener(title)
                }
            }

        }
    }

    private var mOnScrollChangeListener: OnScrollChangeListener? = null

    fun setOnScrollChangeListener(listener: OnScrollChangeListener) {
        mOnScrollChangeListener = listener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        // 获取 WebView 内容的高度
        val contentHeight = contentHeight * scale

        // 获取 WebView 的高度
        val webViewHeight = height

        // 计算阈值
        // 有些时候滑到底部没有反应，所以需要加上的阈值
        val threshold = webViewHeight * 0.1f

        if (scrollY + webViewHeight + threshold >= contentHeight) {
            // 处于底端
            Timber.d("处于底端")
            mOnScrollChangeListener?.onPageEnd(l, t, oldl, oldt)
        } else if (scrollY == 0) {
            // 处于顶端
            Timber.d("处于顶端")
            mOnScrollChangeListener?.onPageTop(l, t, oldl, oldt)
        } else {
            mOnScrollChangeListener?.onScrollChanged(l, t, oldl, oldt)
        }
    }

    interface OnScrollChangeListener {
        fun onPageEnd(l: Int, t: Int, oldl: Int, oldt: Int)
        fun onPageTop(l: Int, t: Int, oldl: Int, oldt: Int)
        fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }
}