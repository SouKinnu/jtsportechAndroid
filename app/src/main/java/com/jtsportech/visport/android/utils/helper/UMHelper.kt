package com.jtsportech.visport.android.utils.helper

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ToastUtils
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.cloudhearing.android.lib_base.utils.onTextChange
import com.cloudhearing.android.lib_common.utils.GsonUtil
import com.cloudhearing.android.lib_common.utils.mmkv.PreferencesWrapper
import com.jtsportech.visport.android.BuildConfig
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.webview.WebViewType
import com.mobile.auth.gatewayauth.ResultCode
import com.umeng.commonsdk.UMConfigure
import com.umeng.umverify.UMResultCode
import com.umeng.umverify.UMVerifyHelper
import com.umeng.umverify.listener.UMPreLoginResultListener
import com.umeng.umverify.listener.UMTokenResultListener
import com.umeng.umverify.model.UMTokenRet
import com.umeng.umverify.view.UMAbstractPnsViewDelegate
import com.umeng.umverify.view.UMAuthRegisterXmlConfig
import com.umeng.umverify.view.UMAuthUIConfig
import org.json.JSONObject
import timber.log.Timber
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Author: BenChen
 * Date: 2024/03/27 11:13
 * Email:chenxiaobin@cloudhearing.cn
 *
 * [友盟SDK接入文档](https://developer.umeng.com/docs/143070/detail/144780)
 */
class UMHelper private constructor() {

    private val context: Context by lazy {
        AppProvider.get()
    }

    private val threadLocal = ThreadLocal<Boolean>()

    companion object {

        const val APP_KEY = "6602b7ca8d21b86a1847eb58"

        const val APP_SECRET =
            "8V0j6l6aEpkm+qK/n1HAH4DhHfVfWfVRRj0TSZIDvb+9Mhn6WGjQ77A5/pfetw9P/dgYzwTqo9XXDZKAitynGQUkG9TAZhx6kuy2KmxWoWCeYUoKlHq2iMh07VoGiZHR1KZBdAIsBLRLhxEeWoX18H4W3eTuyPqe8CsfdF8gO34QsDNGRA3RrSbvAlB2KN8dRLr45FZQwdCa6DukTsYV44yowwqS88mN4WmTIFXspnDGVCepYNXGIXRvDJWl7BFUEBAqgiGXCWG9RugAi4EYITXrGZQrt3Z1b5IQjB2Ez8u21ztA5HlxbZP9nksR1ryw"

        const val CHANNEL = "jtsport"

        const val ACCELERATE_LOGINPAGE_TIMEOUT = 5000

        const val LOGIN_TOKEN_TIMEOUT = 5000

        @JvmStatic
        val INSTANCE: UMHelper by lazy {
            UMHelper()
        }
    }

    private var authHelper: UMVerifyHelper? = null
    private var envAvailable: Boolean = false
    private var isJumpLoginAuthPage: Boolean = false
    private var isAgree = false

    init {
        preInit()
    }

    /**
     *  为满足工信部合规要求，请确保按照合规指南进行预初始化
     *  https://developer.umeng.com/docs/119267/detail/182050
     */
    private fun preInit() {
        UMConfigure.preInit(context, APP_KEY, CHANNEL)
    }

    fun init() {
        // 确保只初始化一次
        if (threadLocal.get() != null) {
            throw IllegalStateException("UMHelper already init")
        }


        UMConfigure.init(context, APP_KEY, CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "")

        // 初始化SDK实例
        authHelper = UMVerifyHelper.getInstance(context, object : UMTokenResultListener {
            override fun onTokenSuccess(p0: String?) {
                Timber.d("checkEnvAvailable: $p0")

                envAvailable = true

                val pTokenRet = UMTokenRet.fromJson(p0)

                if (UMResultCode.CODE_ERROR_ENV_CHECK_SUCCESS == pTokenRet.code) {
                    accelerateLoginPage()
                }
            }

            override fun onTokenFailed(p0: String?) {
                Timber.d("checkEnvAvailable: $p0")

                envAvailable = false
            }
        })
        authHelper!!.setLoggerEnable(BuildConfig.DEBUG)
        authHelper!!.setAuthSDKInfo(APP_SECRET)

        authHelper!!.checkEnvAvailable(UMVerifyHelper.SERVICE_TYPE_LOGIN)

        threadLocal.set(true)
    }

    fun supportOneClick(): Boolean {
        return envAvailable
    }

    fun isJumpLoginAuthPage(): Boolean {
        return isJumpLoginAuthPage
    }

    /**
     * 在不是一进app就需要登录的场景 建议调用此接口 加速拉起一键登录页面
     * 等到用户点击登录的时候 授权页可以秒拉
     * 预取号的成功与否不影响一键登录功能，所以不需要等待预取号的返回。
     *
     */
    fun accelerateLoginPage() {
        // 预取号码信息，建议在 APP 登录页初始化时调用，提高后续授权页的打开速度。
        // 不要在 App 启动初始化时调用，预取号接口有有效期，避免接口调用资源浪费
        authHelper!!.accelerateLoginPage(
            ACCELERATE_LOGINPAGE_TIMEOUT,
            object : UMPreLoginResultListener {
                override fun onTokenSuccess(p0: String?) {
                    Timber.d("预取号成功: $p0")

                    authHelper?.releasePreLoginResultListener()
                }

                override fun onTokenFailed(p0: String?, p1: String?) {
                    Timber.e("预取号失败: $p0 , $p1")

                    authHelper?.releasePreLoginResultListener()
                }
            })
    }

    /**
     * 拉起授权页
     *
     */
    fun getLoginToken() {
        isJumpLoginAuthPage = true

        configLoginTokenPort()
        authHelper!!.setAuthListener(object : UMTokenResultListener {
            override fun onTokenSuccess(p0: String) {
                doTokenSuccess(p0)
            }

            override fun onTokenFailed(p0: String?) {
                Timber.e("获取认证token失败 $p0")
            }
        })
        authHelper!!.getLoginToken(context, LOGIN_TOKEN_TIMEOUT)
        Timber.d("正在唤起授权页")
    }

    /**
     * 退出授权页面
     *
     */
    fun quitLoginPage() {
        invitationCode = ""
        isJumpLoginAuthPage = false

        authHelper!!.quitLoginPage()
    }

    private fun configLoginTokenPort() {
        authHelper!!.removeAuthRegisterXmlConfig()
        authHelper!!.removeAuthRegisterViewConfig()

        // 授权页物理返回键禁用
        authHelper!!.closeAuthPageReturnBack(true)

        authHelper!!.addAuthRegisterXmlConfig(
            UMAuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.custom_login_port, object : UMAbstractPnsViewDelegate() {
                    override fun onViewCreated(view: View) {
                        val etInvitationCode = view.findViewById<EditText>(R.id.et_invitation_code)
                        val tvAccountLogin =
                            view.findViewById<AppCompatTextView>(R.id.tv_account_login)
                        val tvPhoneLogin = view.findViewById<AppCompatTextView>(R.id.tv_phone_login)
                        val ivWechat = view.findViewById<AppCompatImageView>(R.id.iv_wechat)
                        val ivQQ = view.findViewById<AppCompatImageView>(R.id.iv_qq)

                        etInvitationCode.onTextChange {
                            invitationCode = it
                        }

                        tvAccountLogin.setOnClickListener {
                            quitLoginPage()
                            receiveAccountLoginButton()
                        }

                        tvPhoneLogin.setOnClickListener {
                            quitLoginPage()
                            receivePhoneNumberLoginButton()
                        }

                        ivWechat.setOnClickListener {
                            if (!isAgree) {
                                ToastUtils.showShort(R.string.phone_please_agree_to_the_privacy_policy_first)
                                return@setOnClickListener
                            }
                            receiveWeChatLoginButton()
                        }

                        ivQQ.setOnClickListener {
                            if (!isAgree) {
                                ToastUtils.showShort(R.string.phone_please_agree_to_the_privacy_policy_first)
                                return@setOnClickListener
                            }
                            receiveQQLoginButton()
                        }
                    }
                }).build()
        )

        authHelper!!.setUIClickListener { code, context, jsonString ->
            var jsonObject: JSONObject? = null

            try {
                if (jsonString.isNotEmpty()) {
                    jsonObject = JSONObject(jsonString)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                jsonObject = JSONObject()
            }

            when (code) {
                // 点击登录按钮事件
                ResultCode.CODE_ERROR_USER_LOGIN_BTN -> {
                    Timber.d("点击登录按钮事件")
                    if (jsonObject?.optBoolean("isChecked") == false) {
                        ToastUtils.showShort(R.string.phone_please_agree_to_the_privacy_policy_first)
                    }
                }
                // 点击check box事件
                ResultCode.CODE_ERROR_USER_CHECKBOX -> {
                    Timber.d("点击check box事件")
                    isAgree = jsonObject?.optBoolean("isChecked") == true
                }
            }
        }

        setupAuthUIConfig()
    }


    private fun setupAuthUIConfig() {
        val userAgreementText = context.getString(R.string.account_user_agreement)
        val privacyAgreementText = context.getString(R.string.account_privacy_agreement)
        var userAgreementUrl = ""
        var privacyAgreementUrl = ""
        isAgree = false

        val useLocal = PreferencesWrapper.get().getUseLocal()

        if (useLocal.isEmpty() || useLocal.contains("zh")) {
            userAgreementUrl = WebViewType.USER_AGREEMENT.url[0]
            privacyAgreementUrl = WebViewType.PRIVACY_POLICY.url[0]
        } else {
            userAgreementUrl = WebViewType.USER_AGREEMENT.url[1]
            privacyAgreementUrl = WebViewType.PRIVACY_POLICY.url[1]
        }


        authHelper!!.setAuthUIConfig(
            UMAuthUIConfig.Builder()
                .setAppPrivacyOne(
                    userAgreementText,
                    userAgreementUrl
                )
                .setAppPrivacyTwo(
                    privacyAgreementText,
                    privacyAgreementUrl
                )
                .setAppPrivacyColor(
                    ContextCompat.getColor(context, R.color.gray),
                    ContextCompat.getColor(context, R.color.ecstasy)
                )
                .setStatusBarColor(Color.WHITE)
//                .setAuthPageActIn("in_activity", "out_activity")
//                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setPrivacyState(false)
                .setLightColor(true)
                .setStatusBarHidden(true)
                .setStatusBarColor(Color.TRANSPARENT)
                .setWebNavColor(Color.WHITE)
                .setNavReturnImgDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_backarrow_black
                    )
                )
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setLogBtnBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.selector_login_bg
                    )
                )
                .setLogBtnText(
                    ContextCompat.getString(
                        context,
                        R.string.oneclick_oneclick_login_with_your_local_number
                    )
                )
                .setLogBtnToastHidden(true)
                .setUncheckedImgDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_no_agree
                    )
                )
                .setCheckedImgDrawable(ContextCompat.getDrawable(context, R.drawable.icon_agree))
                .create()
        )
    }

    private fun doTokenSuccess(ret: String) {
        try {
            val tokenRet = GsonUtil.gsonToBean(ret, UMTokenRet::class.java)

            if (tokenRet != null && "600001" != tokenRet.code) {
                val token = tokenRet.token
                Timber.d("获取认证token成功 $token")
                authHelper?.hideLoginLoading()
                receiveOneClickButton(token)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var invitationCode: String = ""
        private set


    interface UMHelperCallback {
        fun onOneClickButton(token: String)
        fun onAccountLoginButton()
        fun onPhoneNumberLoginButton()
        fun onWeChatLoginButton()
        fun onQQLoginButton()
    }

    private val mUMHelperCallbackList: MutableList<UMHelperCallback> =
        Collections.synchronizedList(CopyOnWriteArrayList())

    fun registerUMHelperCallback(listener: UMHelperCallback) {
        if (!mUMHelperCallbackList.contains(listener)) {
            mUMHelperCallbackList.add(listener)
        }
    }

    fun unregisterUMHelperCallback(listener: UMHelperCallback) {
        mUMHelperCallbackList.remove(listener)
    }

    private fun receiveOneClickButton(token: String) {
        mUMHelperCallbackList.forEach {
            it.onOneClickButton(token)
        }
    }

    private fun receiveAccountLoginButton() {
        mUMHelperCallbackList.forEach {
            it.onAccountLoginButton()
        }
    }

    private fun receivePhoneNumberLoginButton() {
        mUMHelperCallbackList.forEach {
            it.onPhoneNumberLoginButton()
        }
    }

    private fun receiveWeChatLoginButton() {
        mUMHelperCallbackList.forEach {
            it.onWeChatLoginButton()
        }
    }

    private fun receiveQQLoginButton() {
        mUMHelperCallbackList.forEach {
            it.onQQLoginButton()
        }
    }
}