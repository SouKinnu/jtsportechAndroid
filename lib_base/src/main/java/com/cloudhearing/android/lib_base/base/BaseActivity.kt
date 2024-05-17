package com.cloudhearing.android.lib_base.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cloudhearing.android.lib_base.base.delegate.impl.BasicAbilitiesDelegateImpl
import com.cloudhearing.android.lib_base.base.delegate.impl.CoroutineDelegateImpl
import com.cloudhearing.android.lib_base.base.delegate.listener.BasicAbilitiesDelegate
import com.cloudhearing.android.lib_base.base.delegate.listener.CoroutineDelegate
import com.cloudhearing.android.lib_base.component.dialog.LoadingOverlayDialog
import com.cloudhearing.android.lib_base.utils.HasLoadingOverlay


abstract class BaseActivity : AppCompatActivity(), SwitchConfig,
    BasicAbilitiesDelegate by BasicAbilitiesDelegateImpl(),
    CoroutineDelegate by CoroutineDelegateImpl(), HasLoadingOverlay {

    private val mLoadingProgressDialog by lazy {
//        ProgressDialog(this)
        LoadingOverlayDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        registerTransparentStatusBar(this, transparentStatusBar)
        super.onCreate(savedInstanceState)

        registerBasicAbilitiesDelegate(this, lifecycle, savedInstanceState)
        registerPaddingSystemWindowInsets(
            this,
            paddingTopSystemWindowInsets,
            paddingBottomSystemWindowInsets
        )
//        registerMarginBottomSystemWindowInsets(this, marginBottomSystemWindowInsets)
        registerTransitionAnimation(
            this,
            enterAnimation,
            exitAnimation,
            popEnterAnimation,
            popExitAnimation
        )
        registerCoroutine(lifecycle)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(registerBaseContext(newBase))
    }


    /**
     * Increase Lifecycle monitoring
     *
     */
    protected fun IBaseViewModel.addObserver() {
        lifecycle.addObserver(this)
    }

    override fun showLoadingScreen(messageId: Int) {
        mLoadingProgressDialog.apply {
            setTitle(messageId)
            if (!isShowing) show()
        }
    }

    override fun showLoadingScreen(message: String) {
        mLoadingProgressDialog.apply {
            setTitle(message)
            if (!isShowing) show()
        }
    }

    override fun showLoadingScreenWithNoMessage() {
        mLoadingProgressDialog.apply {
            setTitle("")
            if (!isShowing) show()
        }
    }

    override fun hideLoadingScreen() {
        mLoadingProgressDialog.dismiss()
    }

}