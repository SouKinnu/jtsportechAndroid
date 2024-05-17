package com.cloudhearing.android.lib_base.base

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
import androidx.activity.ComponentDialog
import androidx.activity.addCallback
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.R
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2021/11/18 17:35
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseBindingDialog<VB : ViewBinding>(
    context: Context,
    themeResId: Int = R.style.BaseDialog,
    inflate: (LayoutInflater) -> VB,
    private val canceledOnTouchOutside: Boolean = false,
    private val widthPercentage: Float = 0.8f,
    private val gravity: Int = Gravity.CENTER
) : ComponentDialog(context, themeResId) {

    var binding: VB = inflate(layoutInflater)

    init {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adjustLayoutSize()
        setupLifecycleOwner()
        setupBackPressedDispatcher()
    }

    private fun init() {
        // Make us non-modal, so that others can receive touch events.
        window!!.setFlags(FLAG_NOT_TOUCH_MODAL, FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        window!!.setFlags(FLAG_WATCH_OUTSIDE_TOUCH, FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(binding.root)
    }

    private fun adjustLayoutSize() {
        val vm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = vm.defaultDisplay
        val point = Point()
        display.getSize(point)
        val layoutParams = window!!.attributes
        layoutParams.width = (point.x * widthPercentage).toInt()    //宽度设置为屏幕宽度的0.8

        layoutParams.gravity = gravity

        window!!.attributes = layoutParams


        setCanceledOnTouchOutside(canceledOnTouchOutside)

//        window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setupLifecycleOwner() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                dismiss()
            }
        })
    }

    private fun setupBackPressedDispatcher(){
        onBackPressedDispatcher.addCallback {
            Timber.d("走了 onBackPressedDispatcher ${Looper.getMainLooper() == Looper.myLooper()}")
            dismiss()
        }
    }
}