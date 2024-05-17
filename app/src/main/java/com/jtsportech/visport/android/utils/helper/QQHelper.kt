package com.jtsportech.visport.android.utils.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.cloudhearing.android.lib_base.utils.context.AppProvider
import com.tencent.connect.common.Constants
import com.tencent.tauth.DefaultUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import org.json.JSONObject
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/03/26 18:15
 * Email:chenxiaobin@cloudhearing.cn
 *
 * [QQSDK接入文档](https://wiki.connect.qq.com/qq%e7%99%bb%e5%bd%95)
 */

class QQHelper private constructor() {

    private val context: Context by lazy {
        AppProvider.get()
    }

    companion object {
        const val APP_ID = "102100102"

        const val APP_KEY = "2ziHdleOtAHAsQsC"

        const val AUTHORITIES = "com.tencent.login.fileprovider"

        @JvmStatic
        val INSTANCE: QQHelper by lazy {
            QQHelper()
        }
    }

    private var mTencent: Tencent? = null
    var uiListener: BaseUiListener? = null
        private set

    init {
        init()
    }

    private fun init() {
        Tencent.setIsPermissionGranted(true)
        mTencent = Tencent.createInstance(APP_ID, context, AUTHORITIES)
        uiListener = BaseUiListener(mTencent!!)
    }

    fun requestQQLogin(activity: Activity) {
        if (!mTencent!!.isSessionValid) {
            Timber.d("判断会话有效")

            //这里Tencent的实例mTencent的login函数的三个参数
            //1.为当前的context，
            //2.权限,可选项，一般选择all即可，即全部的权限，不过目前好像也只有一个开放的权限了
            //3.为UIlistener的实例对象
            when (mTencent!!.login(activity, "all", uiListener!!)) {
                -1 -> {
                    Timber.e("异常")
                }

                0 -> {
                    Timber.d("正常登录")
                }

                1 -> {
                    Timber.d("开始登录")
                }

                2 -> {
                    Timber.e("使用H5登陆或显示下载页面")
                }

                else -> {
                    Timber.e("出错")
                }
            }

        } else {
            Timber.d("判断会话无效")
        }
    }


    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //腾讯QQ回调，这里的iu仍然是相关的UIlistener
        Timber.d("腾讯QQ回调，这里的iu仍然是相关的 UIlistener")
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener)
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, uiListener)
            }
        }
    }

    interface QQHelperCallback {
        fun handleResponse(openid: String)

        fun cancelLogin()
    }

    private var mQQHelperCallback: QQHelperCallback? = null

    fun registerQQHelperCallback(listener: QQHelperCallback) {
        mQQHelperCallback = listener
    }

    fun unregisterWechatHelperCallback() {
        mQQHelperCallback = null
    }

    inner class BaseUiListener(private var mTencent: Tencent) : DefaultUiListener() {

        override fun onComplete(p0: Any?) {
            super.onComplete(p0)
            if (p0 == null) {
                Timber.e("返回为空,登录失败")
                return
            }

            val jsonResponse = p0 as JSONObject
            if (jsonResponse.length() == 0) {
                Timber.e("返回为空,登录失败")
                return
            }

            doComplete(jsonResponse)

            Timber.d("登录成功")
        }

        /**
         * 首先需要用上一步获取的json数据对mTencent进行赋值，这部分放在doComplete方法中执行
         *
         */
        private fun doComplete(values: JSONObject?) {
            val openid = values?.get("openid")

            Timber.d("openid $openid")

            mQQHelperCallback?.handleResponse(openid.toString())
        }

        override fun onError(p0: UiError?) {
            super.onError(p0)
        }

        override fun onCancel() {
            super.onCancel()
            Timber.d("取消登录")
            mQQHelperCallback?.cancelLogin()
        }
    }
}
