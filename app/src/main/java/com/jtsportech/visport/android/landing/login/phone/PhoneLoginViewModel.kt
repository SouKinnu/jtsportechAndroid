package com.jtsportech.visport.android.landing.login.phone

import androidx.lifecycle.ViewModel
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.CountdownTimer
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseResp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class PhoneLoginViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mSmsCodeCountdownTimer: CountdownTimer by lazy {
        CountdownTimer()
    }

    private val mWeChatRepository: WeChatRepository by lazy {
        WeChatRepository()
    }

    private val _satisfyStepFlow = MutableStateFlow(false)

    val satisfyStepFlow = _satisfyStepFlow.asStateFlow()

    private val _isSmsCodeCountdownTimerEvents = SharedFlowEvents<Boolean>()

    val isSmsCodeCountdownTimerEvents = _isSmsCodeCountdownTimerEvents.asSharedFlow()

    private val _smsCodeCountdownNumberEvents = SharedFlowEvents<Long>()

    val smsCodeCountdownNumberEvents = _smsCodeCountdownNumberEvents.asSharedFlow()

    private val _loginResultFlowEvents = SharedFlowEvents<Boolean>()

    val loginResultFlowEvents = _loginResultFlowEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    var accountNameValue: String = ""
        set(value) {
            field = value

            _satisfyStepFlow.value = value.isNotEmpty() && verificationCodeValue.isNotEmpty()
        }

    var verificationCodeValue: String = ""
        set(value) {
            field = value

            _satisfyStepFlow.value = accountNameValue.isNotEmpty() && value.isNotEmpty()
        }

    var invitationCodeValue: String = ""
        set(value) {
            field = value
        }

    private var mSmsToken: String = ""
    private var mCheckToken: String = ""

    fun getVerificationCode() {
        if (accountNameValue.isEmpty()) {
            return
        }

        launchRequest(
            isLoading = true, {
                mUserRepository.smsLoginSendCode(
                    accountNameValue
                )
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

    fun smsLogin() {
        launchRequest(
            isLoading = true, {
                mUserRepository.smsLogin(
                    inviteCode = invitationCodeValue,
                    smsToken = mSmsToken,
                    validCode = verificationCodeValue
                )
            }, {
                Timber.d(it)
                if (it != null) {
                    PreferencesWrapper.get().setAccessToken(it)
                    getUserInfo()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun getUserInfo() {
        launchRequest(
            isLoading = true, {
                mUserRepository.getUserInfo()
            }, {
                if (it != null) {
                    PreferencesBusinessHelper.saveUserInfo(it)
                }
                _loginResultFlowEvents.setEvent(true)
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

    override fun onStart() {
        super.onStart()
        WechatHelper.INSTANCE.registerWechatHelperCallback(mWechatHelperCallback)
        QQHelper.INSTANCE.registerQQHelperCallback(mQQHelperCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        WechatHelper.INSTANCE.unregisterWechatHelperCallback()
        QQHelper.INSTANCE.unregisterWechatHelperCallback()
        cancelCountdown()
    }

    private val mWechatHelperCallback = object : WechatHelper.WechatHelperCallback {
        override fun handleResponse(errCode: Int, code: String) {
            if (errCode == BaseResp.ErrCode.ERR_OK) {
                Timber.d("微信登录成功回调")
                getWeChatAccessToken(code)
            }
        }
    }

    private val mQQHelperCallback = object : QQHelper.QQHelperCallback {
        override fun handleResponse(openid: String) {
            qqOpenLogin(openid)
        }

        override fun cancelLogin() {
        }
    }

    private fun qqOpenLogin(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openLogin(openId, "QQ")
            }, {
                if (it != null) {
                    PreferencesWrapper.get().setAccessToken(it)
                    getUserInfo()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun getWeChatAccessToken(code: String) {
        launchRequest(
            isLoading = false, {
                mWeChatRepository.getAccessToken(
                    appid = WechatHelper.APP_ID,
                    secret = WechatHelper.APP_SECRET,
                    code = code,
                    grant_type = "authorization_code"
                )
            }, {
                if (it != null) {
                    wechatOpenLogin(it.openid.orEmpty())
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )

    }

    private fun wechatOpenLogin(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openLogin(openId, "WECHAT")
            }, {
                if (it != null) {
                    PreferencesWrapper.get().setAccessToken(it)
                    getUserInfo()
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }
}