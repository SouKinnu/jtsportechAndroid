package com.jtsportech.visport.android.components.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.jtsportech.visport.android.databinding.DialogTimeShutdownTipsBinding
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/03/21 14:31
 * Email:chenxiaobin@cloudhearing.cn
 */
class TimedShutdownTipsDialog(context: Context) :
    BaseBindingDialog<DialogTimeShutdownTipsBinding>(
        context,
        inflate = DialogTimeShutdownTipsBinding::inflate,
        widthPercentage = 0.3f
    ) {

    companion object {
        const val EXECUTE_DISMISS_WHAT = 1
        const val EXECUTE_DISMISS_DELAYED = 1 * 1000L
    }

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                EXECUTE_DISMISS_WHAT -> {
                    Timber.i("执行 dismiss 操作")
                    dismiss()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setDimAmount(0f)
    }

    fun setTimedShutdownImage(@DrawableRes resId: Int) {
        binding.ivTimedShutdown.setImageResource(resId)
    }

    fun setMessage(@StringRes resId: Int) {
        binding.tvMessage.setText(resId)
    }

    fun setIconShow(isVisibility: Boolean) {
        binding.ivTimedShutdown.isVisible = isVisibility
    }

    override fun show() {
        super.show()
        Timber.i("执行 show 操作")
        sendMessage()
    }

    override fun dismiss() {
        super.dismiss()
        removeMessage()
    }

    fun show(delayMillis: Long) {
        super.show()
        Timber.i("执行 show 操作")
        sendMessage(delayMillis)
    }

    private fun sendMessage(delayMillis: Long = EXECUTE_DISMISS_DELAYED) {
        if (handler.hasMessages(EXECUTE_DISMISS_WHAT)) {
            handler.removeMessages(EXECUTE_DISMISS_WHAT)
        }
        Timber.i("执行 sendEmptyMessageDelayed 操作 delayMillis $delayMillis")
        handler.sendEmptyMessageDelayed(EXECUTE_DISMISS_WHAT, delayMillis)
    }

    private fun removeMessage() {
        if (handler.hasMessages(EXECUTE_DISMISS_WHAT)) {
            handler.removeMessages(EXECUTE_DISMISS_WHAT)
        }
    }
}