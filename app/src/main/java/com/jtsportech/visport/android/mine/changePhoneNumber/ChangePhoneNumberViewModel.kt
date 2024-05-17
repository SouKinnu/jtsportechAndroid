package com.jtsportech.visport.android.mine.changePhoneNumber

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

class ChangePhoneNumberViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mSmsCodeOriginCountdownTimer: CountdownTimer by lazy {
        CountdownTimer()
    }

    private val mSmsCodeNewCountdownTimer: CountdownTimer by lazy {
        CountdownTimer()
    }

    private val _satisfyStepOneFlow = MutableStateFlow(false)

    val satisfyStepOneFlow = _satisfyStepOneFlow.asStateFlow()

    private val _satisfyStepTwoFlow = MutableStateFlow(false)

    val satisfyStepTwoFlow = _satisfyStepTwoFlow.asStateFlow()

    private val _nextStepFlowEvents = SharedFlowEvents<Unit>()

    val nextStepFlowEvents = _nextStepFlowEvents.asSharedFlow()

    private val _isSmsCodeOriginCountdownTimerEvents = SharedFlowEvents<Boolean>()

    val isSmsCodeOriginCountdownTimerEvents = _isSmsCodeOriginCountdownTimerEvents.asSharedFlow()

    private val _smsCodeOriginCountdownNumberEvents = SharedFlowEvents<Long>()

    val smsCodeOriginCountdownNumberEvents = _smsCodeOriginCountdownNumberEvents.asSharedFlow()

    private val _isSmsCodeNewCountdownTimerEvents = SharedFlowEvents<Boolean>()

    val isSmsCodeNewCountdownTimerEvents = _isSmsCodeNewCountdownTimerEvents.asSharedFlow()

    private val _smsCodeNewCountdownNumberEvents = SharedFlowEvents<Long>()

    val smsCodeNewCountdownNumberEvents = _smsCodeNewCountdownNumberEvents.asSharedFlow()

    private val _signOutNoticeEvents = SharedFlowEvents<Unit>()

    val signOutNoticeEvents = _signOutNoticeEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    var verificationOriginCodeValue: String = ""
        set(value) {
            field = value

            satisfyStepOne = value.isNotEmpty()
        }

    var oldPhoneNumberValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                value.isNotEmpty() && newPhoneNumberValue.isNotEmpty() && verificationNewCodeValue.isNotEmpty()
        }

    var newPhoneNumberValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                oldPhoneNumberValue.isNotEmpty() && value.isNotEmpty() && verificationNewCodeValue.isNotEmpty()
        }

    var verificationNewCodeValue: String = ""
        set(value) {
            field = value

            satisfyStepTwo =
                oldPhoneNumberValue.isNotEmpty() && newPhoneNumberValue.isNotEmpty() && value.isNotEmpty()
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

    // 旧手机号的校验
    private var mOriginSmsToken: String = ""
    private var mOriginCheckToken: String = ""

    // 新手机号的校验
    private var mNewSmsToken: String = ""
    private var mNewCheckToken: String = ""

    fun getVerificationOriginCode() {
        launchRequest(
            isLoading = true, {
                mUserRepository.changePhoneNumberToStepOne()
            }, {
                Timber.d(it)
                if (it != null) {
                    mOriginSmsToken = it

                    startOriginCodeCountdown()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun verifyVerificationOriginCode() {
        if (mOriginSmsToken.isEmpty()) {
            Timber.e("当前 originSmsToken 为空")
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.changePhoneNumberToStepTwo(
                    mOriginSmsToken,
                    verificationOriginCodeValue
                )
            }, {
                Timber.d(it)
                if (it != null) {
                    mOriginCheckToken = it

                    _nextStepFlowEvents.setEvent(Unit)

                    cancelOriginCodeCountdown()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun getVerificationNewCode() {
        if (mOriginCheckToken.isEmpty()) {
            Timber.e("当前 originCheckToken 为空")
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.changePhoneNumberToStepThree(
                    mOriginCheckToken, newPhoneNumberValue
                )
            }, {
                if (it != null) {
                    mNewSmsToken = it

                    startNewCodeCountdown()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    fun changePhoneNumber() {
        if (mNewSmsToken.isEmpty()) {
            Timber.e("当前 newSmsToken 为空")
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.changePhoneNumberToStepFour(
                    mNewSmsToken, verificationNewCodeValue
                )
            }, {
                _nextStepFlowEvents.setEvent(Unit)

                cancelNewCodeCountdown()
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

    private fun startOriginCodeCountdown() {
        mSmsCodeOriginCountdownTimer.startCountdown(60L, onTick = {
            launchUi {
                _isSmsCodeOriginCountdownTimerEvents.setEvent(true)
                _smsCodeOriginCountdownNumberEvents.setEvent(it)
            }
        }, onFinish = {
            launchUi {
                _isSmsCodeOriginCountdownTimerEvents.setEvent(false)
            }
        })
    }

    private fun startNewCodeCountdown() {
        mSmsCodeNewCountdownTimer.startCountdown(60L, onTick = {
            launchUi {
                _isSmsCodeNewCountdownTimerEvents.setEvent(true)
                _smsCodeNewCountdownNumberEvents.setEvent(it)
            }
        }, onFinish = {
            launchUi {
                _isSmsCodeNewCountdownTimerEvents.setEvent(false)
            }
        })
    }

    private fun cancelOriginCodeCountdown() {
        mSmsCodeOriginCountdownTimer.cancelCountdown()
    }

    private fun cancelNewCodeCountdown() {
        mSmsCodeNewCountdownTimer.cancelCountdown()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelOriginCodeCountdown()
        cancelNewCodeCountdown()
    }
}