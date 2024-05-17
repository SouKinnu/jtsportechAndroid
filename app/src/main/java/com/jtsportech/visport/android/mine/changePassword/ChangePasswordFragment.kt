package com.jtsportech.visport.android.mine.changePassword

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
import com.jtsportech.visport.android.databinding.FragmentChangePasswordBinding
import com.jtsportech.visport.android.utils.maskPhoneNumber
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ChangePasswordFragment :
    BaseBindingVmFragment<FragmentChangePasswordBinding, ChangePasswordViewModel>(
        FragmentChangePasswordBinding::inflate
    ) {

    @StringDef(
        STEP_ONE,
        STEP_TWO,
        STEP_THREE
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CHANGEPASSWORDSTEPS

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

        @CHANGEPASSWORDSTEPS
        var mCurrentStep: String = STEP_ONE
    }

    override fun initView() {


        setPhoneNumber()

        binding.apply {
            apChangePassword.setOnClickLeftIconListener {
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
            etOldPassword.onTextChange {
                viewModel.oldPasswordValue = it
            }

            etNewPassword.onTextChange {
                viewModel.newPasswordValue = it
            }

            etNewConfirmPassword.onTextChange {
                viewModel.newConfirmPasswordValue = it
            }

            btNextTwo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.changePassword()
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
            satisfyStepOneFlow.observeState(this@ChangePasswordFragment) {
                if (it) binding.btNextOne.enable() else binding.btNextOne.disable()
            }

            satisfyStepTwoFlow.observeState(this@ChangePasswordFragment) {
                if (it) binding.btNextTwo.enable() else binding.btNextTwo.disable()
            }

            toastFlowEvents.observeEvent(this@ChangePasswordFragment) {
                systremToast(it)
            }

            nextStepFlowEvents.observeEvent(this@ChangePasswordFragment) {
                onNextStep()
            }

            isSmsCodeCountdownTimerEvents.observeEvent(this@ChangePasswordFragment) {
                if (it) {
                    binding.tvCodeState.isEnabled = false
                } else {
                    binding.tvCodeState.isEnabled = true
                    binding.tvCodeState.text = getString(R.string.phone_get_a_verification_code)
                }

            }

            smsCodeCountdownNumberEvents.observeEvent(this@ChangePasswordFragment) {
                binding.tvCodeState.text = "${it}s"
            }

            signOutNoticeEvents.observeEvent(this@ChangePasswordFragment) {
                onSignOut()
            }

            loadState.observeEvent(this@ChangePasswordFragment) {
                handleLoadState(it)
            }
        }
    }

    private fun onSignOut() {
        LiveEventBus.get<String>(SIGN_OUT).post("")
    }

    private fun setPhoneNumber() {
        val phoneNumber = PreferencesWrapper.get().getPhoneNumber().maskPhoneNumber()

        binding.tvPhoneTips.text = getString(R.string.change_password_sms_tips, phoneNumber)
    }

    private fun onNextStep() {
        Timber.d("当前的步骤是 $mCurrentStep")
        if (mCurrentStep == STEP_ONE) {
            mCurrentStep = STEP_TWO

            binding.groupStepOne.hide()
            binding.groupStepTwo.show()
            binding.groupStepThree.hide()

        } else if (mCurrentStep == STEP_TWO) {
            mCurrentStep = STEP_THREE

            binding.groupStepOne.hide()
            binding.groupStepTwo.hide()
            binding.groupStepThree.show()
            binding.apChangePassword.setLeftIconDisplay(false)
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

    private fun goBack() {
        findNavController().popBackStack()
    }


    override fun onDestroy() {
        super.onDestroy()

        if (mCurrentStep == STEP_THREE) {
            onSignOut()
        }
    }


}