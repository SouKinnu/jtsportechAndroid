package com.jtsportech.visport.android.landing.transparent

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.addCallback
import com.cloudhearing.android.lib_base.base.BaseBindingVmActivity
import com.cloudhearing.android.lib_base.utils.handleOnBackPressedDispatcher
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.systremToast
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.databinding.ActivityTransparentLoginBinding
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import timber.log.Timber

/**
 * 1. 为了解决禁用授权页物理返回键
 * 2. 为了解决 QQ 登录需要 onActivityResult 方法回调
 */
class TransparentLoginActivity :
    BaseBindingVmActivity<ActivityTransparentLoginBinding, TransparentLoginViewModel>(
        ActivityTransparentLoginBinding::inflate
    ) {


    override fun initView() {
        viewModel.setupActivity(this)
        setupOnePixel()
//        interceptPhysicalReturnKeyEvents()
    }

    override fun initData() {
        QQHelper.INSTANCE.requestQQLogin(this)
    }

    override fun initEvent() {
        viewModel.run {
            loginResultFlowEvents.observeEvent(this@TransparentLoginActivity) {
                handleLoginResult(it)
            }

            toastFlowEvents.observeEvent(this@TransparentLoginActivity) {
                systremToast(it)
                finish()
            }
        }
    }

    /**
     * 设置一个像素大小
     *
     */
    private fun setupOnePixel() {
        window.setGravity(Gravity.START or Gravity.TOP)
        val params = window.attributes
        params.x = 0
        params.y = 0
        params.height = 1
        params.width = 1
        window.attributes = params
    }

    /**
     * 拦截物理返回键事件
     *
     */
    private fun interceptPhysicalReturnKeyEvents() {
        Timber.d("有走 interceptPhysicalReturnKeyEvents")

        handleOnBackPressedDispatcher {
            Timber.d("Activity handle on back")
        }

        // 不接收触摸事件
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun handleLoginResult(result: Boolean) {
        if (result) {
            UMHelper.INSTANCE.quitLoginPage()
            startActivity(
                Intent(this, DashboardActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        QQHelper.INSTANCE.handleActivityResult(requestCode, resultCode, data)
    }
}