package com.jtsportech.visport.android.splash

import android.content.Intent
import android.os.Bundle
import com.cloudhearing.android.lib_base.base.BaseBindingActivity
import com.cloudhearing.android.lib_common.utils.ScheduledTaskExecutor
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.jtsportech.visport.android.databinding.ActivitySplashBinding
import com.jtsportech.visport.android.guide.GuideActivity
import com.jtsportech.visport.android.landing.LandingActivity
import com.jtsportech.visport.android.dialog.UserPrivacyAgreementDialog
import com.jtsportech.visport.android.webview.WebActivity
import timber.log.Timber

class SplashActivity : BaseBindingActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val userPrivacyAgreementDialog: UserPrivacyAgreementDialog by lazy {
        UserPrivacyAgreementDialog(this@SplashActivity).apply {
            /**
             *Type=1 用户协议   Type=2 隐私协议
             *Type=3 同意按钮   Type=4 不同意按钮
             */
            setOnClickListener {
                when (it) {
                    1 -> {
                        onWebView(WebViewType.USER_AGREEMENT, null)
                    }

                    2 -> {
                        onWebView(WebViewType.PRIVACY_POLICY, null)
                    }

                    3 -> {
//                        PreferencesWrapper.get().setUseMultipleTimes(true)
//                        onGuide()
                        onWebView(WebViewType.USER_AGREEMENT, true)
                    }

                    4 -> finish()
                }
            }
        }
    }

    companion object {
        const val STAY_SPLASH_SCREEN_TIME = 2_000L
        const val USER_AGREEMENT_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Timber.d("hasFocus $hasFocus")

        if (!hasFocus) return

        ScheduledTaskExecutor(STAY_SPLASH_SCREEN_TIME) {
            handleJumpPage()
        }.start()
    }

    private fun handleJumpPage() {
        val useMultipleTimes = PreferencesWrapper.get().getUseMultipleTimes()
        if (useMultipleTimes) {
            if (PreferencesWrapper.get().getAccessToken().isEmpty()) {
                onLanding()
            } else {
                onDashboard()
            }
        } else {
            userPrivacyAgreementDialog.show()
        }
    }

    private fun onGuide() {
        startActivity(Intent(this, GuideActivity::class.java))
        finish()
    }

    private fun onLanding() {
        startActivity(Intent(this, LandingActivity::class.java))
        finish()
    }

    private fun onDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun onWebView(type: WebViewType, isShowSureBtn: Boolean?) {
        val intent = Intent(this, WebActivity::class.java).apply {
            putExtras(Bundle().apply {
                putSerializable("type", type)
                if (isShowSureBtn != null) {
                    putBoolean("isShowSureBtn", isShowSureBtn)
                }
            })
        }
        if (isShowSureBtn != null) {
            startActivityForResult(intent, USER_AGREEMENT_REQUEST_CODE)
        } else {
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == USER_AGREEMENT_REQUEST_CODE) {
            PreferencesWrapper.get().setUseMultipleTimes(true)
            onGuide()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
    }
}