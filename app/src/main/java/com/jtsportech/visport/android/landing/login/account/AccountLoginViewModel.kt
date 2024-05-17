package com.jtsportech.visport.android.landing.login.account

import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.CompetitionList
import com.cloudhearing.android.lib_common.network.dataSource.mine.UserInfo
import com.cloudhearing.android.lib_common.network.repository.CompetitionRepository
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseResp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class AccountLoginViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mCompetitionRepository: CompetitionRepository by lazy {
        CompetitionRepository()
    }

    private val mWeChatRepository: WeChatRepository by lazy {
        WeChatRepository()
    }

    private val _validityStateFlow = MutableStateFlow(false)

    val validityStateFlow = _validityStateFlow.asStateFlow()

    private val _loginResultFlowEvents = SharedFlowEvents<Boolean>()

    val loginResultFlowEvents = _loginResultFlowEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private var mUsername: String = ""
    private var mPassword: String = ""
    private var mInvitationCode: String = ""


    init {

    }

    override fun onStart() {
        super.onStart()
        WechatHelper.INSTANCE.registerWechatHelperCallback(mWechatHelperCallback)
        QQHelper.INSTANCE.registerQQHelperCallback(mQQHelperCallback)
//        accountLogin()
    }

    override fun onDestroy() {
        super.onDestroy()
        WechatHelper.INSTANCE.unregisterWechatHelperCallback()
        QQHelper.INSTANCE.unregisterWechatHelperCallback()
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

    fun updateUserame(username: String) {
        mUsername = username

        checkValidity()
    }

    fun updatePassword(password: String) {
        mPassword = password

        checkValidity()
    }

    fun updateInvitationCode(code: String) {
        mInvitationCode = code
    }

    private fun checkValidity() {
        val isUsernameValidity = mUsername.isNotEmpty()
        val isPasswordValidity = mPassword.isNotEmpty()

        _validityStateFlow.value = isUsernameValidity && isPasswordValidity
    }

    fun login() {
        accountLogin()
    }

    private fun accountLogin() {
        launchRequest(
            isLoading = false, {
                mUserRepository.accountLogin(username = mUsername, password = mPassword)
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

}