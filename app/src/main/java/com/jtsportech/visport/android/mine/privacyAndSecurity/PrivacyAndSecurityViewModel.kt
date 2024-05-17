package com.jtsportech.visport.android.mine.privacyAndSecurity

import android.app.Activity
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseResp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

class PrivacyAndSecurityViewModel : BaseViewModel() {


    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mWeChatRepository: WeChatRepository by lazy {
        WeChatRepository()
    }

    private val _wechatBindStateFlow = MutableStateFlow(false)

    val wechatBindStateFlow = _wechatBindStateFlow.asStateFlow()

    private val _qqBindStateFlow = MutableStateFlow(false)

    val qqBindStateFlow = _qqBindStateFlow.asStateFlow()


    private val _unbindSuccessFlowEvents = SharedFlowEvents<Unit>()

    val unbindSuccessFlowEvents = _unbindSuccessFlowEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private val wechat = "WECHAT"
    private val qq = "QQ"

    private var mWeChatopenId = ""
    private var mQQopenId = ""

    val isWeChatBind: Boolean
        get() = _wechatBindStateFlow.value

    val isQQBind: Boolean
        get() = _qqBindStateFlow.value

    override fun onStart() {
        super.onStart()
        WechatHelper.INSTANCE.registerWechatHelperCallback(mWechatHelperCallback)
        QQHelper.INSTANCE.registerQQHelperCallback(mQQHelperCallback)
        getOpenBindList()
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
            Timber.d("QQ登录成功回调")
            qqOpenBind(openid)
        }

        override fun cancelLogin() {
        }
    }

    fun handleWeChatLogin() {
        val isBind = _wechatBindStateFlow.value

        if (isBind) {
            wechatOpenUnbind(mWeChatopenId)
        } else {
            WechatHelper.INSTANCE.requestWeChatLogin()
        }
    }

    fun handleQQLogin(activity: Activity) {
        val isBind = _qqBindStateFlow.value

        if (isBind) {
            qqOpenUnbind(mQQopenId)
        } else {
            QQHelper.INSTANCE.requestQQLogin(activity)
        }
    }


    fun getOpenBindList() {
        launchRequest(
            isLoading = false, {
                mUserRepository.openBindList()
            }, {
                if (!it.isNullOrEmpty()) {
                    val weChatOpenLogin = it.find {
                        it.openType == "WECHAT"
                    }

                    val qqOpenLogin = it.find {
                        it.openType == "QQ"
                    }

                    if (weChatOpenLogin != null) {
                        _wechatBindStateFlow.value = true
                        mWeChatopenId = weChatOpenLogin.openId.orEmpty()
                    } else {
                        _wechatBindStateFlow.value = false
                        mWeChatopenId = ""
                    }

                    if (qqOpenLogin != null) {
                        _qqBindStateFlow.value = true
                        mQQopenId = qqOpenLogin.openId.orEmpty()
                    } else {
                        _qqBindStateFlow.value = false
                        mQQopenId = ""
                    }


                } else {
                    _wechatBindStateFlow.value = false
                    _qqBindStateFlow.value = false

                    mWeChatopenId = ""
                    mQQopenId = ""
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun wechatOpenBind(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openBind(openId, "WECHAT")
            }, {
                getOpenBindList()
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun qqOpenBind(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openBind(openId, "QQ")
            }, {
                getOpenBindList()
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
                    wechatOpenBind(it.openid.orEmpty())
                }
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )

    }

    private fun wechatOpenUnbind(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openUnbind(openId, "WECHAT")
            }, {
                _unbindSuccessFlowEvents.setEvent(Unit)
                getOpenBindList()
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }

    private fun qqOpenUnbind(openId: String) {
        launchRequest(
            isLoading = false, {
                mUserRepository.openUnbind(openId, "QQ")
            }, {
                _unbindSuccessFlowEvents.setEvent(Unit)
                getOpenBindList()
            }, { errorCode, errorMsg ->
                _toastFlowEvents.setEvent("${errorCode}:${errorMsg}")
            }, {
                _toastFlowEvents.setEvent("${it.message}")
            }
        )
    }


}