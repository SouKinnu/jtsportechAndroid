package com.jtsportech.visport.android.mine.privacyAndSecurity

import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.safetyShow
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.dialog.SignOutDialog
import com.jtsportech.visport.android.components.dialog.TimedShutdownTipsDialog
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.jtsportech.visport.android.databinding.FragmentPrivacyAndSecurityBinding
import com.jtsportech.visport.android.utils.PRIVACY_AGREEMENT
import com.jtsportech.visport.android.utils.USER_AGREEMENT
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PrivacyAndSecurityFragment :
    BaseBindingVmFragment<FragmentPrivacyAndSecurityBinding, PrivacyAndSecurityViewModel>(
        FragmentPrivacyAndSecurityBinding::inflate
    ) {

    private val mTimedShutdownTipsDialog: TimedShutdownTipsDialog by lazy {
        TimedShutdownTipsDialog(requireContext()).apply {
            setTimedShutdownImage(R.drawable.ic_download_ok)
            setMessage(R.string.privacy_and_security_the_unbinding_is_successful)
        }
    }

    private val mWeChatUnbindDialog: SignOutDialog by lazy {
        SignOutDialog(requireContext()).apply {
            setTitle(getString(R.string.privacy_and_security_prompt))
            setSubtitle(getString(R.string.privacy_and_security_unbound_tips))
            setNegativeButtonLable(getString(R.string.alert_cancel))
            setPositiveButtonLable(getString(R.string.alert_sure))
            setPositiveButtonlickListener {
                viewModel.handleWeChatLogin()
            }
        }
    }

    private val mQQUnbindDialog: SignOutDialog by lazy {
        SignOutDialog(requireContext()).apply {
            setTitle(getString(R.string.privacy_and_security_prompt))
            setSubtitle(getString(R.string.privacy_and_security_unbound_tips))
            setNegativeButtonLable(getString(R.string.alert_cancel))
            setPositiveButtonLable(getString(R.string.alert_sure))
            setPositiveButtonlickListener {
                viewModel.handleQQLogin(requireActivity())
            }
        }
    }

    override fun initView() {
        binding.apply {
            apPrivacyAndSecurity.setOnClickLeftIconListener {
                goBack()
            }

            sbUserPrivacyAgreement.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onAgreement(PRIVACY_AGREEMENT)
                }
                .launchIn(mainScope)

            sbUserUseAgreement.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onAgreement(USER_AGREEMENT)
                }
                .launchIn(mainScope)

            sbChangeYourPassword.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onChangePassword()
                }
                .launchIn(mainScope)

            sbWechatAccount.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    if (viewModel.isWeChatBind) {
                        mWeChatUnbindDialog.safetyShow()
                    } else {
                        viewModel.handleWeChatLogin()
                    }
                }
                .launchIn(mainScope)

            sbQqAccount.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    if (viewModel.isQQBind) {
                        mQQUnbindDialog.safetyShow()
                    } else {
                        viewModel.handleQQLogin(requireActivity())
                    }
                }
                .launchIn(mainScope)

            mbDeleteAccount.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    onCancelAccount()
                }
                .launchIn(mainScope)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            wechatBindStateFlow.observeState(this@PrivacyAndSecurityFragment) {
                val message =
                    if (it) getString(R.string.privacy_and_security_bind) else getString(R.string.privacy_and_security_unbound)
                binding.sbWechatAccount.setStateInfo(message)
            }

            qqBindStateFlow.observeState(this@PrivacyAndSecurityFragment){
                val message =
                    if (it) getString(R.string.privacy_and_security_bind) else getString(R.string.privacy_and_security_unbound)
                binding.sbQqAccount.setStateInfo(message)
            }

            unbindSuccessFlowEvents.observeEvent(this@PrivacyAndSecurityFragment) {
                mTimedShutdownTipsDialog.safetyShow()
            }

            toastFlowEvents.observeEvent(this@PrivacyAndSecurityFragment) {
                systremToast(it)
            }

            loadState.observeEvent(this@PrivacyAndSecurityFragment) {
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

    private fun onAgreement(agreement: String) {
        val type =
            if (agreement == USER_AGREEMENT) WebViewType.USER_AGREEMENT else WebViewType.PRIVACY_POLICY

        findNavController().navigate(
            PrivacyAndSecurityFragmentDirections.actionPrivacyAndSecurityFragmentToWebActivity2(type)
        )
    }

    private fun onChangePassword() {
        findNavController().navigate(
            PrivacyAndSecurityFragmentDirections.actionPrivacyAndSecurityFragmentToChangePasswordFragment()
        )
    }

    private fun onCancelAccount() {
        findNavController().navigate(PrivacyAndSecurityFragmentDirections.actionPrivacyAndSecurityFragmentToCancelAccountFragment())
    }

    private fun goBack() {
        requireActivity().onBackPressed()
    }


}