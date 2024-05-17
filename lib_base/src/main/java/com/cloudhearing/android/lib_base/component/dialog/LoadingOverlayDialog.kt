package com.cloudhearing.android.lib_base.component.dialog

import android.content.Context
import android.os.Bundle
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.cloudhearing.android.lib_base.databinding.DialogLoadingOverlayBinding

/**
 * Author: BenChen
 * Date: 2024/02/20 17:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class LoadingOverlayDialog(context: Context) : BaseBindingDialog<DialogLoadingOverlayBinding>(
    context,
    inflate = DialogLoadingOverlayBinding::inflate,
    widthPercentage = 0.4f
) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    fun setTitles(message: String) {
        binding.loadingOverlay.setMessage(message)
    }

    fun setTitles(messageId: Int) {
        binding.loadingOverlay.setMessage(context.getString(messageId))
    }
}