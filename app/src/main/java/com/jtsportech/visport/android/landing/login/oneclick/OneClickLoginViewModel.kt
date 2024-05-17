package com.jtsportech.visport.android.landing.login.oneclick

import android.app.Activity
import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.cloudhearing.android.lib_base.base.BaseViewModel
import com.cloudhearing.android.lib_base.utils.JUMP_ACCOUNT_LOGIN
import com.cloudhearing.android.lib_base.utils.JUMP_PHONE_LOGIN
import com.cloudhearing.android.lib_base.utils.JUMP_QQ_LOGIN
import com.cloudhearing.android.lib_base.utils.LOGIN_RESULTS
import com.cloudhearing.android.lib_base.utils.SIGN_OUT
import com.cloudhearing.android.lib_base.utils.SharedFlowEvents
import com.cloudhearing.android.lib_base.utils.TOAST_MESSAGE
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.setEvent
import com.cloudhearing.android.lib_common.network.repository.UserRepository
import com.cloudhearing.android.lib_common.network.repository.WeChatRepository
import com.cloudhearing.android.lib_common.utils.launchRequest
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jtsportech.visport.android.landing.transparent.TransparentLoginActivity
import com.jtsportech.visport.android.utils.PreferencesBusinessHelper
import com.jtsportech.visport.android.utils.helper.QQHelper
import com.jtsportech.visport.android.utils.helper.UMHelper
import com.jtsportech.visport.android.utils.helper.WechatHelper
import com.tencent.mm.opensdk.modelbase.BaseResp
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

class OneClickLoginViewModel : BaseViewModel() {

    private val mUserRepository: UserRepository by lazy {
        UserRepository()
    }

    private val mWeChatRepository: WeChatRepository by lazy {
        WeChatRepository()
    }

    private var mActivity: Activity? = null
    fun setupActivity(activity: Activity) {
        mActivity = activity
    }

    override fun onCreate() {
        super.onCreate()
        WechatHelper.INSTANCE.registerWechatHelperCallback(mWechatHelperCallback)
        QQHelper.INSTANCE.registerQQHelperCallback(mQQHelperCallback)
        UMHelper.INSTANCE.registerUMHelperCallback(mUMHelperCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        UMHelper.INSTANCE.unregisterUMHelperCallback(mUMHelperCallback)
        WechatHelper.INSTANCE.unregisterWechatHelperCallback()
        QQHelper.INSTANCE.unregisterWechatHelperCallback()
        mActivity = null
    }


    private val mUMHelperCallback = object : UMHelper.UMHelperCallback {
        override fun onOneClickButton(token: String) {
            mobileLogin(token)
        }

        override fun onAccountLoginButton() {
            jumpAccountLogin()
        }

        override fun onPhoneNumberLoginButton() {
            jumpPhoneLogin()
        }

        override fun onWeChatLoginButton() {
            WechatHelper.INSTANCE.requestWeChatLogin()
        }

        override fun onQQLoginButton() {
//            QQHelper.INSTANCE.requestQQLogin(mActivity!!)
            mActivity?.startActivity(Intent(mActivity, TransparentLoginActivity::class.java))
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
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onToastMessage("${it.message}")
            }
        )
    }

    private fun mobileLogin(token: String) {
        val invitationCode = UMHelper.INSTANCE.invitationCode

        launchRequest(
            isLoading = false, {
                mUserRepository.mobileLogin(token, invitationCode)
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
                onToastMessage("${errorCode}:${errorMsg}")
            }, {
                onToastMessage("${it.message}")
            }
        )
    }

    private fun onToastMessage(msg: String) {
        LiveEventBus.get<String>(TOAST_MESSAGE).post(msg)
    }

    private fun onLoginResults(result: Boolean) {
        LiveEventBus.get<Boolean>(LOGIN_RESULTS).post(result)
    }

    private fun jumpAccountLogin() {
        LiveEventBus.get<String>(JUMP_ACCOUNT_LOGIN).post("")
    }

    private fun jumpPhoneLogin() {
        LiveEventBus.get<String>(JUMP_PHONE_LOGIN).post("")
    }

    private fun jumpQQLogin() {
        LiveEventBus.get<String>(JUMP_QQ_LOGIN).post("")
    }
}