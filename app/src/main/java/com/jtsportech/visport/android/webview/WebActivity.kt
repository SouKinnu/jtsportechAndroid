package com.jtsportech.visport.android.webview

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.navArgs
import com.cloudhearing.android.lib_base.base.BaseBindingActivity
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.webview.BWebView
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.jtsportech.visport.android.databinding.ActivityWebBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class WebActivity : BaseBindingActivity<ActivityWebBinding>(ActivityWebBinding::inflate) {

    override val transparentStatusBar: Boolean
        get() = false
    override val paddingTopSystemWindowInsets: Boolean
        get() = true

    private val mArgs by navArgs<WebActivityArgs>()

    private var mWebView: BWebView? = null

    private var type: WebViewType? = null

    private var isShowSureBtn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        binding.root.fitsSystemWindows = true

        setupBundle()
        setupToolbar()
        setupWebView()

        binding.apply {
            btSure.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    setResult(RESULT_OK)
                    this@WebActivity.finish()
                }
                .launchIn(mainScope)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

    private fun setupBundle() {
        val bundle = intent.extras
        type = bundle?.getSerializable("type") as WebViewType
        isShowSureBtn = bundle.getBoolean("isShowSureBtn") ?: false
    }

    private fun setupToolbar() = binding.apply {
        when (getType()) {
            WebViewType.USER_AGREEMENT -> {
                apWebview.setTitle(getString(R.string.agreement_user_agreement))
                if (isShowSureBtn) {
                    binding.btSure.show()
                }
            }

            WebViewType.PRIVACY_POLICY -> {
                apWebview.setTitle(getString(R.string.agreement_privacy_agreement))
                if (isShowSureBtn) {
                    binding.btSure.show()
                }
            }

            else -> {}
        }

        apWebview.setOnClickLeftIconListener {
            this@WebActivity.finish()
        }
    }

    private fun setupWebView() {
        ConstraintLayout.LayoutParams(
            0,
            0
        ).apply {
            topToBottom = binding.apWebview.id
            startToStart = binding.clRoot.id
            endToEnd = binding.clRoot.id
            if (isShowSureBtn) {
                bottomToBottom = binding.clRoot.id
            }
            bottomToTop = binding.btSure.id
        }.also {
            mWebView = BWebView(this)
            mWebView!!.layoutParams = it
            mWebView!!.setInitialScale(25)
            binding.clRoot.addView(mWebView)
        }

        val url = getUri()
//        mWebView!!.loadWeb(BWebView.ANDROID_ASSET_INDEX + url)
        mWebView!!.loadWeb(url)
        mWebView!!.setOnScrollChangeListener(object : BWebView.OnScrollChangeListener {
            override fun onPageEnd(l: Int, t: Int, oldl: Int, oldt: Int) {
                Timber.d("到底部了")
                if (isShowSureBtn) {
                    binding?.apply {
                        btSure.enable()
                        btSure.text = getString(R.string.agreement_button_activated)
                    }
                }
            }

            override fun onPageTop(l: Int, t: Int, oldl: Int, oldt: Int) {

            }

            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {

            }
        })
    }

    private fun getUri(): String {
        val useLocal = PreferencesWrapper.get().getUseLocal()
        val position = if (useLocal.isEmpty() || useLocal.contains("zh")) 0 else 1

        return when (getType()) {
            WebViewType.USER_AGREEMENT, WebViewType.PRIVACY_POLICY -> mArgs.type.url[position]
            else -> mArgs.type.url[0]
        }
    }

    private fun getType(): WebViewType {
        return if (type != null) type!! else mArgs.type
    }

    private fun destroyWebView() {
        mWebView?.apply {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            (parent as ViewGroup).removeView(this)
            destroy()
        }
        mWebView = null
    }
}