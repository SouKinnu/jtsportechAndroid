package com.jtsportech.visport.android.mine.changePhoneNumber

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
import com.jtsportech.visport.android.databinding.FragmentChangePhoneNumberBinding
import com.jtsportech.visport.android.utils.maskPhoneNumber
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class ChangePhoneNumberFragment :
    BaseBindingVmFragment<FragmentChangePhoneNumberBinding, ChangePhoneNumberViewModel>(
        FragmentChangePhoneNumberBinding::inflate
    ) {

    @StringDef(
        STEP_ONE,
        STEP_TWO,
        STEP_THREE
    )
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class CHANGEPHONENUMBERSTEPS

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

        @CHANGEPHONENUMBERSTEPS
        var mCurrentStep: String = STEP_ONE
    }

    override fun initView() {
        setPhoneNumber()

        binding.apply {
            apChangePhoneNumber.setOnClickLeftIconListener {
                goBack()
            }

            // step one
            etCodeOne.onTextChange {
                viewModel.verificationOriginCodeValue = it
            }

            tvCodeState.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.getVerificationOriginCode()
                }
                .launchIn(mainScope)

            btNextOne.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.verifyVerificationOriginCode()
                }
                .launchIn(mainScope)

            // step two
            etOldPhoneNumber.onTextChange {
                viewModel.oldPhoneNumberValue = it
            }

            etNewPhoneNumber.onTextChange {
                viewModel.newPhoneNumberValue = it
            }

            etCodeTwo.onTextChange {
                viewModel.verificationNewCodeValue = it
            }

            tvCodeStateTwo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.getVerificationNewCode()
                }
                .launchIn(mainScope)

            btNextTwo.clickFlow()
                .throttleFirst(ANTI_SHAKE_THRESHOLD)
                .onEach {
                    viewModel.changePhoneNumber()
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

    private fun setPhoneNumber() {
        val phoneNumber = PreferencesWrapper.get().getPhoneNumber().maskPhoneNumber()

        binding.tvPhoneTips.text = getString(R.string.change_password_sms_tips, phoneNumber)
    }

    override fun initData() {

    }

    override fun initEvent() {
        viewModel.run {
            satisfyStepOneFlow.observeState(this@ChangePhoneNumberFragment) {
                if (it) binding.btNextOne.enable() else binding.btNextOne.disable()
            }

            satisfyStepTwoFlow.observeState(this@ChangePhoneNumberFragment) {
                if (it) binding.btNextTwo.enable() else binding.btNextTwo.disable()
            }

            toastFlowEvents.observeEvent(this@ChangePhoneNumberFragment) {
                systremToast(it)
            }

            nextStepFlowEvents.observeEvent(this@ChangePhoneNumberFragment) {
                onNextStep()
            }

            isSmsCodeOriginCountdownTimerEvents.observeEvent(this@ChangePhoneNumberFragment) {
                if (it) {
                    binding.tvCodeState.isEnabled = false
                } else {
                    binding.tvCodeState.isEnabled = true
                    binding.tvCodeState.text = getString(R.string.phone_get_a_verification_code)
                }

            }

            isSmsCodeNewCountdownTimerEvents.observeEvent(this@ChangePhoneNumberFragment) {
                if (it) {
                    binding.tvCodeStateTwo.isEnabled = false
                } else {
                    binding.tvCodeStateTwo.isEnabled = true
                    binding.tvCodeStateTwo.text = getString(R.string.phone_get_a_verification_code)
                }

            }

            smsCodeOriginCountdownNumberEvents.observeEvent(this@ChangePhoneNumberFragment) {
                binding.tvCodeState.text = "${it}s"
            }

            smsCodeNewCountdownNumberEvents.observeEvent(this@ChangePhoneNumberFragment) {
                binding.tvCodeStateTwo.text = "${it}s"
            }

            signOutNoticeEvents.observeEvent(this@ChangePhoneNumberFragment) {
                onSignOut()
            }

            loadState.observeEvent(this@ChangePhoneNumberFragment) {
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

    private fun onSignOut() {
        LiveEventBus.get<String>(SIGN_OUT).post("")
    }

    private fun onNextStep() {
        Timber.d("当前的步骤是 ${mCurrentStep}")
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
            binding.apChangePhoneNumber.setLeftIconDisplay(false)
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        if (mCurrentStep == STEP_THREE){
            onSignOut()
        }
    }

    private fun goBack() {
        findNavController().popBackStack()
    }


}