package com.jtsportech.visport.android.landing.login.phone

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.onTextChange
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_base.utils.toSpannableString
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.DashboardActivity
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.jtsportech.visport.android.databinding.FragmentPhoneLoginBinding
import com.jtsportech.visport.android.databinding.IncludeLoginBinding
import com.jtsportech.visport.android.landing.login.oneclick.OneClickLoginFragmentDirections
import com.jtsportech.visport.android.utils.USER_AGREEMENT
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class PhoneLoginFragment :
    BaseBindingVmFragment<FragmentPhoneLoginBinding, PhoneLoginViewModel>(FragmentPhoneLoginBinding::inflate) {

    private lateinit var mIncludeLoginBinding: IncludeLoginBinding
    override fun initView() {
        mIncludeLoginBinding = IncludeLoginBinding.bind(binding.root)

        binding.apply {
            etAccount.onTextChange {
                viewModel.accountNameValue = it
            }

            etValidCode.onTextChange {
                viewModel.verificationCodeValue = it
            }

            etInvitationCode.onTextChange {
                viewModel.invitationCodeValue = it
            }

            tvCodeState.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.getVerificationCode()
                }
                .launchIn(mainScope)

            mIncludeLoginBinding.apply {
                etInvitationCode.onTextChange {

                }

                tvAgreement.toSpannableString {
                    onAgreement(it)
                }

                ivWechat.clickFlow()
                    .throttleFirst(ANTI_SHAKE_THRESHOLD)
                    .onEach {
                        weChatLogin()
                    }
                    .launchIn(mainScope)

                ivQq.clickFlow()
                    .throttleFirst(ANTI_SHAKE_THRESHOLD)
                    .onEach {
                        qqLogin()
                    }
                    .launchIn(mainScope)

                btRadio.clickFlow()
                    .onEach {
                        handleCheckAgreement()
                    }
                    .launchIn(mainScope)
            }

            btLogin.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    handleLogin()
                }
                .launchIn(mainScope)

            tvAccountLogin.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onAccountLogin()
                }
                .launchIn(mainScope)

            tvOneClickLogin.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onOneClickLogin()
                }
                .launchIn(mainScope)
        }

        setupOtherLoginMethod()
    }

    private fun onAgreement(agreement: String) {
        val type =
            if (agreement == USER_AGREEMENT) WebViewType.USER_AGREEMENT else WebViewType.PRIVACY_POLICY

        findNavController().navigate(
            PhoneLoginFragmentDirections.actionPhoneLoginFragmentToWebActivity(type)
        )
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            satisfyStepFlow.observeState(this@PhoneLoginFragment) {
                if (it) binding.btLogin.enable() else binding.btLogin.disable()
            }

            toastFlowEvents.observeEvent(this@PhoneLoginFragment) {
                systremToast(it)
            }

            isSmsCodeCountdownTimerEvents.observeEvent(this@PhoneLoginFragment) {
                if (it) {
                    binding.tvCodeState.isEnabled = false
                } else {
                    binding.tvCodeState.isEnabled = true
                    binding.tvCodeState.text = getString(R.string.phone_get_a_verification_code)
                }

            }

            smsCodeCountdownNumberEvents.observeEvent(this@PhoneLoginFragment) {
                binding.tvCodeState.text = "${it}s"
            }

            loadState.observeEvent(this@PhoneLoginFragment) {
                handleLoadState(it)
            }

            loginResultFlowEvents.observeEvent(this@PhoneLoginFragment) {
                Timber.d("loginResult $it")
                if (it) {
                    hideLoadingScreen()
                    onDashboard()
                }
            }
        }
    }

    private fun handleLoadState(state: LoadState) {
        when (state) {
            is LoadState.Start -> {
                showLoadingScreen(state.tip)
            }

            is LoadState.Error -> {
                hideLoadingScreen()
                systremToast("${state.code}:${state.msg}")
            }

            is LoadState.Finish -> {
                hideLoadingScreen()
            }

            else -> {}
        }
    }

    private fun onDashboard() {
        startActivity(Intent(requireActivity(), DashboardActivity::class.java))
        requireActivity().finish()
    }


    private fun onAccountLogin() {
        findNavController().navigate(PhoneLoginFragmentDirections.actionGlobalAccountLoginFragment())
    }

    private fun onOneClickLogin() {
        findNavController().navigate(PhoneLoginFragmentDirections.actionGlobalOneClickLoginFragment())
    }

    private fun setupOtherLoginMethod() {
        val supportOneClick = UMHelper.INSTANCE.supportOneClick()

        if (!supportOneClick){
            binding.v1.hide()
            binding.tvOneClickLogin.hide()
        }
    }

    private fun handleCheckAgreement() {
        val isEnable = mIncludeLoginBinding.btRadio.isEnable

        if (isEnable) {
            mIncludeLoginBinding.btRadio.disable()
        } else {
            mIncludeLoginBinding.btRadio.enable()
        }
    }

    private fun handleLogin() {
        val isEnable = mIncludeLoginBinding.btRadio.isEnable

        if (!isEnable) {
            systremToast(getString(R.string.phone_please_agree_to_the_privacy_policy_first))
            return
        }

        viewModel.smsLogin()
    }

    private fun weChatLogin() {
        val isEnable = mIncludeLoginBinding.btRadio.isEnable

        if (!isEnable) {
            systremToast(getString(R.string.phone_please_agree_to_the_privacy_policy_first))
            return
        }
        WechatHelper.INSTANCE.requestWeChatLogin()
    }

    private fun qqLogin() {
        val isEnable = mIncludeLoginBinding.btRadio.isEnable

        if (!isEnable) {
            systremToast(getString(R.string.phone_please_agree_to_the_privacy_policy_first))
            return
        }
        QQHelper.INSTANCE.requestQQLogin(requireActivity())
    }


}