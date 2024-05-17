package com.jtsportech.visport.android.landing.login.oneclick

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.ActivityUtils
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.utils.JUMP_ACCOUNT_LOGIN
import com.cloudhearing.android.lib_base.utils.JUMP_PHONE_LOGIN
import com.cloudhearing.android.lib_base.utils.JUMP_QQ_LOGIN
import com.cloudhearing.android.lib_base.utils.LOGIN_RESULTS
import com.cloudhearing.android.lib_base.utils.TOAST_MESSAGE
import com.cloudhearing.android.lib_base.utils.systremToast
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.databinding.FragmentOneClickLoginBinding
import com.jtsportech.visport.android.landing.transparent.TransparentLoginActivity
import com.jtsportech.visport.android.utils.helper.UMHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class OneClickLoginFragment :
    BaseBindingVmFragment<FragmentOneClickLoginBinding, OneClickLoginViewModel>(
        FragmentOneClickLoginBinding::inflate
    ) {



    override fun initView() {
        viewModel.setupActivity(requireActivity())
        if (UMHelper.INSTANCE.supportOneClick()) {
            UMHelper.INSTANCE.getLoginToken()
//            onTransparentLogin()
//            mainScope.launch {
//                delay(50L)
//                onTransparentLogin()
//            }
        } else {
            onAccountLogin()
        }
    }


    override fun initData() {
        UMHelper.INSTANCE.accelerateLoginPage()
    }

    override fun initEvent() {
        LiveEventBus.get<String>(TOAST_MESSAGE).observe(this) {
            systremToast(it)
            UMHelper.INSTANCE.accelerateLoginPage()
        }

        LiveEventBus.get<Boolean>(LOGIN_RESULTS).observe(this) {
            if (it) {
                UMHelper.INSTANCE.quitLoginPage()
//                onCloseTransparentLogin()
                onDashboard()
            }
        }

        LiveEventBus.get<String>(JUMP_ACCOUNT_LOGIN).observe(this) {
//            onCloseTransparentLogin()
            onAccountLogin()
        }

        LiveEventBus.get<String>(JUMP_PHONE_LOGIN).observe(this) {
//            onCloseTransparentLogin()
            onPhoneLogin()
        }

//        LiveEventBus.get<String>(JUMP_QQ_LOGIN).observe(this) {
//            onTransparentLogin()
//        }
    }

    private fun onAccountLogin() {
        findNavController().navigate(OneClickLoginFragmentDirections.actionGlobalAccountLoginFragment())
    }

    private fun onPhoneLogin() {
        findNavController().navigate(OneClickLoginFragmentDirections.actionGlobalPhoneLoginFragment())
    }

//    private fun onTransparentLogin() {
//        startActivity(Intent(requireActivity(), TransparentLoginActivity::class.java))
//    }
//
//    private fun onCloseTransparentLogin() {
//        ActivityUtils.finishActivity(TransparentLoginActivity::class.java)
//    }

    private fun onDashboard() {
        startActivity(Intent(requireActivity(), DashboardActivity::class.java))
        requireActivity().finish()
    }


}