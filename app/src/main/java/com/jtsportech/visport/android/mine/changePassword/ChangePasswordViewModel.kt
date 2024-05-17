package com.jtsportech.visport.android.mine.changePassword

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.utils.CountdownTimer
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class ChangePasswordViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mSmsCodeCountdownTimer: CountdownTimer by lazy {
        CountdownTimer()
    }


    private val _satisfyStepOneFlow = MutableStateFlow(false)

    val satisfyStepOneFlow = _satisfyStepOneFlow.asStateFlow()

    private val _satisfyStepTwoFlow = MutableStateFlow(false)

    val satisfyStepTwoFlow = _satisfyStepTwoFlow.asStateFlow()

    private val _nextStepFlowEvents = SharedFlowEvents<Unit>()

    val nextStepFlowEvents = _nextStepFlowEvents.asSharedFlow()

    private val _isSmsCodeCountdownTimerEvents = SharedFlowEvents<Boolean>()

    val isSmsCodeCountdownTimerEvents = _isSmsCodeCountdownTimerEvents.asSharedFlow()

    private val _smsCodeCountdownNumberEvents = SharedFlowEvents<Long>()

    val smsCodeCountdownNumberEvents = _smsCodeCountdownNumberEvents.asSharedFlow()

    private val _signOutNoticeEvents = SharedFlowEvents<Unit>()

    val signOutNoticeEvents = _signOutNoticeEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    var verificationCodeValue: String = ""
        set(value) {
            field = value

            satisfyStepOne = value.isNotEmpty()
        }
    var oldPasswordValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                value.isNotEmpty() && newPasswordValue.isNotEmpty() && newConfirmPasswordValue.isNotEmpty()
        }
    var newPasswordValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                oldPasswordValue.isNotEmpty() && value.isNotEmpty() && newConfirmPasswordValue.isNotEmpty()
        }
    var newConfirmPasswordValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                oldPasswordValue.isNotEmpty() && newPasswordValue.isNotEmpty() && value.isNotEmpty()
        }

    private var satisfyStepOne: Boolean = false
        set(value) {
            field = value

            _satisfyStepOneFlow.value = value
        }

    private var satisfyStepTwo: Boolean = false
        set(value) {
            field = value

            _satisfyStepTwoFlow.value = value
        }

    private var mSmsToken: String = ""
    private var mCheckToken: String = ""

    fun getVerificationCode() {
        launchRequest(
            isLoading = true, {
                mUserRepository.changePasswordToSendcode()
            }, {
                Timber.d(it)
                if (it != null) {
                    mSmsToken = it

                    startCountdown()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun startCountdown() {
        mSmsCodeCountdownTimer.startCountdown(60L, onTick = {
            launchUi {
                _isSmsCodeCountdownTimerEvents.setEvent(true)
                _smsCodeCountdownNumberEvents.setEvent(it)
            }
        }, onFinish = {
            launchUi {
                _isSmsCodeCountdownTimerEvents.setEvent(false)
            }
        })
    }

    private fun cancelCountdown() {
        mSmsCodeCountdownTimer.cancelCountdown()
    }

    fun verifyVerificationCode() {
        if (mSmsToken.isEmpty()) {
            Timber.e("当前 smsToken 为空")
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.changePasswordStepOne(mSmsToken, verificationCodeValue)
            }, {
                Timber.d(it)
                if (it != null) {
                    mCheckToken = it

                    _nextStepFlowEvents.setEvent(Unit)

                    cancelCountdown()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun changePassword() {
        if (mCheckToken.isEmpty()) {
            Timber.e("当前 checkToken 为空")
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.changePasswordStepTwo(
                    mCheckToken,
                    oldPasswordValue,
                    newPasswordValue,
                    newConfirmPasswordValue
                )
            }, {
                _nextStepFlowEvents.setEvent(Unit)
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun signOut() = launchUi {
        withContext(coroutineDispatchers.io){
            PreferencesBusinessHelper.onSignOut()
        }

        _signOutNoticeEvents.setEvent(Unit)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelCountdown()
    }


}