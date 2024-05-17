package com.jtsportech.visport.android.mine.cancelAccount

import androidx.annotation.StringDef
import androidx.navigation.fragment.findNavController
import com.cloudhearing.android.lib_base.base.BaseBindingVmFragment
import com.cloudhearing.android.lib_base.dataSource.LoadState
import com.cloudhearing.android.lib_base.utils.ANTI_SHAKE_THRESHOLD
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.clickFlow
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.observeEvent
import com.cloudhearing.android.lib_base.utils.observeState
import com.cloudhearing.android.lib_base.utils.onTextChange
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.systremToast
import com.cloudhearing.android.lib_base.utils.throttleFirst
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.components.webview.BWebView
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.jtsportech.visport.android.databinding.FragmentCancelAccountBinding
import com.jtsportech.visport.android.mine.changePassword.ChangePasswordFragment
import com.jtsportech.visport.android.utils.maskPhoneNumber
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class CancelAccountFragment :
    BaseBindingVmFragment<FragmentCancelAccountBinding, CancelAccountViewModel>(
        FragmentCancelAccountBinding::inflate
    ) {

    @StringDef(
        STEP_ONE,
        STEP_TWO,
        STEP_THREE
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CANCELACCOUNTSTEPS

    companion object {
        /**
         * 步骤一
         */
        const val STEP_ONE = "step_one"

        /**
         * 步骤二
         */
        const val STEP_TWO = "step_two"

        /**
         * 步骤三
         */
        const val STEP_THREE = "step_three"

        @CancelAccountFragment.CANCELACCOUNTSTEPS
        var mCurrentStep: String = STEP_ONE
    }

    override fun initView() {
        setPhoneNumber()

        binding.apply {
            apCancelAccount.setOnClickLeftIconListener {
                goBack()
            }

            // step one
            etCodeOne.onTextChange {
                viewModel.verificationCodeValue = it
            }

            tvCodeState.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.getVerificationCode()
                }
                .launchIn(mainScope)

            btNextOne.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.verifyVerificationCode()
                }
                .launchIn(mainScope)

            // step two
            setupWebView()

            btNextTwo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.cancellAccount()
                }
                .launchIn(mainScope)

            // step three
            btNextThree.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.signOut()
                }
                .launchIn(mainScope)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            satisfyStepOneFlow.observeState(this@CancelAccountFragment) {
                if (it) binding.btNextOne.enable() else binding.btNextOne.disable()
            }

            satisfyStepTwoFlow.observeState(this@CancelAccountFragment) {
                if (it) binding.btNextTwo.enable() else binding.btNextTwo.disable()
            }

            toastFlowEvents.observeEvent(this@CancelAccountFragment) {
                systremToast(it)
            }

            nextStepFlowEvents.observeEvent(this@CancelAccountFragment) {
                onNextStep()
            }

            isSmsCodeCountdownTimerEvents.observeEvent(this@CancelAccountFragment) {
                if (it) {
                    binding.tvCodeState.isEnabled = false
                } else {
                    binding.tvCodeState.isEnabled = true
                    binding.tvCodeState.text = getString(R.string.phone_get_a_verification_code)
                }

            }

            isLookReadCancelAccountCountdownTimerEvents.observeEvent(this@CancelAccountFragment) {
                if (it) {
                    binding.btNextTwo.disable()
                } else {
                    binding.btNextTwo.enable()
                    binding.btNextTwo.text = getString(R.string.cancelAccount_agree_to_deregister)
                }
            }

            smsCodeCountdownNumberEvents.observeEvent(this@CancelAccountFragment) {
                binding.tvCodeState.text = "${it}s"
            }

            lookReadCancelAccountCountdownNumberEvents.observeEvent(this@CancelAccountFragment) {
                binding.btNextTwo.text =
                    getString(R.string.cancelAccount_please_read_the_cancellation_agreement, it)
            }

            signOutNoticeEvents.observeEvent(this@CancelAccountFragment) {
                onSignOut()
            }

            loadState.observeEvent(this@CancelAccountFragment) {
                handleLoadState(it)
            }
        }
    }

    private fun onNextStep() {
        Timber.d("当前的步骤是 ${mCurrentStep}")
        if (mCurrentStep == STEP_ONE) {
            mCurrentStep = STEP_TWO

            binding.groupStepOne.hide()
            binding.groupStepTwo.show()
            binding.groupStepThree.hide()

            // 要等待 60s 才可以
            viewModel.lookReadCancelAccountStartCountdown()

        } else if (mCurrentStep == STEP_TWO) {
            mCurrentStep = STEP_THREE

            binding.groupStepOne.hide()
            binding.groupStepTwo.hide()
            binding.groupStepThree.show()
            binding.apCancelAccount.setLeftIconDisplay(false)


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

    private fun setPhoneNumber() {
        val phoneNumber = PreferencesWrapper.get().getPhoneNumber().maskPhoneNumber()

        binding.tvPhoneTips.text = getString(R.string.change_password_sms_tips, phoneNumber)
    }

    private fun setupWebView() {//BWebView.ANDROID_ASSET_INDEX +
        binding.webviewCancelAccount.loadWeb(WebViewType.PRIVACY_POLICY.url[0])
    }


    private fun goBack() {
        findNavController().popBackStack()
    }

    private fun onSignOut() {
        LiveEventBus.get<String>(SIGN_OUT).post("")
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mCurrentStep == STEP_THREE) {
            onSignOut()
        }
    }
}