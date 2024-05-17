package com.jtsportech.visport.android.dialog.base

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
import android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
import androidx.viewbinding.ViewBinding
import com.cloudhearing.android.lib_base.R

/**
 * Author: BenChen
 * Date: 2021/11/18 17:35
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BindingDialog<VB : ViewBinding>(
    context: Context,
    themeResId: Int = R.style.BaseDialog,
    inflate: (LayoutInflater) -> VB,
    var width: Float,
    private val canceledOnTouchOutside: Boolean = false,
    private val widthPercentage: Float = 0.8f,
    private val gravity: Int = Gravity.CENTER
) : Dialog(context, themeResId) {
    var binding: VB = inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val vm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = vm.defaultDisplay
        val point = Point()
        display.getSize(point)
        val layoutParams = window!!.attributes
        layoutParams.width = (point.x * width).toInt()    //宽度设置为屏幕宽度的0.8
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = gravity
        window!!.attributes = layoutParams
        setCanceledOnTouchOutside(canceledOnTouchOutside)
    }

    private fun init() {
        // Make us non-modal, so that others can receive touch events.
        window!!.setFlags(FLAG_NOT_TOUCH_MODAL, FLAG_NOT_TOUCH_MODAL)
        // ...but notify us that it happened.
        window!!.setFlags(FLAG_WATCH_OUTSIDE_TOUCH, FLAG_WATCH_OUTSIDE_TOUCH)
        setContentView(binding.root)
    }
}