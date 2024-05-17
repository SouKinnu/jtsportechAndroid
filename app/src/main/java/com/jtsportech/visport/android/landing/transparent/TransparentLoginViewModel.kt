package com.jtsportech.visport.android.landing.transparent

import android.app.Activity
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseResp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/04/01 11:57
 * Email:chenxiaobin@cloudhearing.cn
 */
class TransparentLoginViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mWeChatRepository: WeChatRepository by lazy {
        WeChatRepository()
    }

    private val _loginResultFlowEvents = SharedFlowEvents<Boolean>()

    val loginResultFlowEvents = _loginResultFlowEvents.asSharedFlow()

    private val _toastFlowEvents = SharedFlowEvents<String>()

    val toastFlowEvents = _toastFlowEvents.asSharedFlow()

    private var mActivity: Activity? = null

    fun setupActivity(activity: Activity) {
        mActivity = activity
    }

    override fun onStart() {
        super.onStart()
//        WechatHelper.INSTANCE.registerWechatHelperCallback(mWechatHelperCallback)
//        UMHelper.INSTANCE.registerUMHelperCallback(mUMHelperCallback)
        QQHelper.INSTANCE.registerQQHelperCallback(mQQHelperCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
//        UMHelper.INSTANCE.unregisterUMHelperCallback(mUMHelperCallback)
//        WechatHelper.INSTANCE.unregisterWechatHelperCallback()
        QQHelper.INSTANCE.unregisterWechatHelperCallback()

        mActivity = null
    }

    private val mUMHelperCallback = object : UMHelper.UMHelperCallback {
        override fun onOneClickButton(token: String) {
        }

        override fun onAccountLoginButton() {
        }

        override fun onPhoneNumberLoginButton() {
        }

        override fun onWeChatLoginButton() {
            WechatHelper.INSTANCE.requestWeChatLogin()
        }

        override fun onQQLoginButton() {
            QQHelper.INSTANCE.requestQQLogin(mActivity!!)
        }
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
            qqOpenLogin(openid)
        }

        override fun cancelLogin() {
            Timber.d("取消登录")
            mActivity?.finish()
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
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onToastMessage("${it.message}")
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
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onToastMessage("${it.message}")
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
                onLoginResults(false)
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onLoginResults(false)
                onToastMessage("${it.message}")
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
                onLoginResults(true)
            }, { errorCode, errorMsg ->
                onLoginResults(false)
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onLoginResults(false)
                onToastMessage("${it.message}")
            }
        )
    }

    private suspend fun onLoginResults(result: Boolean) {
        _loginResultFlowEvents.setEvent(result)
    }

    private suspend fun onToastMessage(message: String) {
        _toastFlowEvents.setEvent(message)
    }
}