package com.jtsportech.visport.android.landing.login.account

import android.content.Intent
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
import com.jtsportech.visport.android.databinding.FragmentAccountLoginBinding
import com.jtsportech.visport.android.databinding.IncludeLoginBinding
import com.jtsportech.visport.android.utils.USER_AGREEMENT
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class AccountLoginFragment :
    BaseBindingVmFragment<FragmentAccountLoginBinding, AccountLoginViewModel>(
        FragmentAccountLoginBinding::inflate
    ) {

    private lateinit var mIncludeLoginBinding: IncludeLoginBinding


    override fun initView() {
        mIncludeLoginBinding = IncludeLoginBinding.bind(binding.root)

        binding.apply {
            etAccount.onTextChange {
                viewModel.updateUserame(it)
            }

            etPassword.onTextChange {
                viewModel.updatePassword(it)
            }

            mIncludeLoginBinding.apply {
                etInvitationCode.onTextChange {
                    viewModel.updateInvitationCode(it)
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

            tvPhoneLogin.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onPhoneLogin()
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
            AccountLoginFragmentDirections.actionAccountLoginFragmentToWebActivity(type)
        )
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            validityStateFlow.observeState(this@AccountLoginFragment) {
                Timber.d("validity $it")
            }


            loginResultFlowEvents.observeEvent(this@AccountLoginFragment) {
                Timber.d("loginResult $it")
                if (it) {
                    hideLoadingScreen()
                    onDashboard()
                }
            }

            toastFlowEvents.observeEvent(this@AccountLoginFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@AccountLoginFragment) {
                handleLoadState(it)
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

        viewModel.login()
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


    private fun setupOtherLoginMethod() {
        val supportOneClick = UMHelper.INSTANCE.supportOneClick()

        if (!supportOneClick){
            binding.v1.hide()
            binding.tvOneClickLogin.hide()
        }
    }

    private fun onPhoneLogin() {
        findNavController().navigate(AccountLoginFragmentDirections.actionGlobalPhoneLoginFragment())
    }

    private fun onOneClickLogin() {
        findNavController().navigate(AccountLoginFragmentDirections.actionGlobalOneClickLoginFragment())
    }

    private fun onDashboard() {
        startActivity(Intent(requireActivity(), DashboardActivity::class.java))
        requireActivity().finish()
    }


}