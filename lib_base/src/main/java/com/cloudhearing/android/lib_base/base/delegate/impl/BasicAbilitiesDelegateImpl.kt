package com.cloudhearing.android.lib_base.base.delegate.impl

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.cloudhearing.android.lib_base.base.delegate.listener.BasicAbilitiesDelegate
import com.cloudhearing.android.lib_base.utils.applySystemWindowInsetsPadding
import com.cloudhearing.android.lib_base.utils.language.XLanguageUtils
import com.cloudhearing.android.lib_base.utils.removeSystemWindowInsetsPadding
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2023/12/19 17:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class BasicAbilitiesDelegateImpl : BasicAbilitiesDelegate, DefaultLifecycleObserver {

    private var activity: AppCompatActivity? = null

    override fun registerBasicAbilitiesDelegate(
        activity: AppCompatActivity,
        lifecycle: Lifecycle,
        savedInstanceState: Bundle?
    ) {
        this.activity = activity

        lifecycle.addObserver(this)

        isNeedRestartApp(activity, savedInstanceState)

    }

    override fun registerTransparentStatusBar(
        activity: AppCompatActivity,
        transparentStatusBar: Boolean
    ) {
        if (transparentStatusBar) {
            activity.enableEdgeToEdge()
        } else {
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK
                ),
                navigationBarStyle = SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK
                )
            )
        }
    }

    override fun registerImmersiveStatusBar(
        activity: AppCompatActivity,
        transparentStatusBar: Boolean
    ) {
        if (transparentStatusBar) {
            activity.enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK
                )
            )

            val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
            rootView.removeSystemWindowInsetsPadding(removeTop = true)
        }
    }

    override fun registerPaddingSystemWindowInsets(
        activity: AppCompatActivity,
        marginTopSystemWindowInsets: Boolean,
        marginBottomSystemWindowInsets: Boolean
    ) {
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
        rootView.applySystemWindowInsetsPadding(
            applyTop = marginTopSystemWindowInsets,
            applyBottom = marginBottomSystemWindowInsets
        )
    }

    override fun registerMarginBottomSystemWindowInsets(
        activity: AppCompatActivity,
        marginBottomSystemWindowInsets: Boolean
    ) {
//        if (marginBottomSystemWindowInsets) {
//            val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
//            rootView.paddingBottomSystemWindowInsets()
//        }
    }

    override fun registerTransitionAnimation(
        activity: AppCompatActivity,
        enterAnimation: Int,
        exitAnimation: Int,
        popEnterAnimation: Int,
        popExitAnimation: Int
    ) {
        activity.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_OPEN,
                    enterAnimation,
                    exitAnimation
                )
            } else {
                overrideActivityTransition (OVERRIDE_TRANSITION_OPEN,enterAnimation, exitAnimation)
            }

//            handleOnBackPressedDispatcher {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                    overrideActivityTransition(
//                        OVERRIDE_TRANSITION_CLOSE,
//                        popEnterAnimation,
//                        popExitAnimation
//                    )
//                } else {
//                    overridePendingTransition(popEnterAnimation, popExitAnimation)
//                }
//            }
        }
    }

    override fun registerBaseContext(context: Context): Context {
        return XLanguageUtils.attachBaseContext(context)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        activity = null
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }


    /**
     * Configuration changes require restarting the app
     *
     * @param savedInstanceState
     */
    private fun isNeedRestartApp(activity: AppCompatActivity, savedInstanceState: Bundle?) =
        activity.apply {
            Timber.e("savedInstanceState $savedInstanceState")
            if (savedInstanceState == null) {
                return@apply
            }

            val intent =
                packageManager.getLaunchIntentForPackage(application.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
}