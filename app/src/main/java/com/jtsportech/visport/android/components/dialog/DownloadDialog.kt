package com.jtsportech.visport.android.components.dialog

import android.content.Context
import android.view.View
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.jtsportech.visport.android.databinding.DialogDownloadBinding

class DownloadDialog(context: Context) : BaseBindingDialog<DialogDownloadBinding>(
    context,
    inflate = DialogDownloadBinding::inflate,
    widthPercentage = 0.5f
) {

    fun setDownloadTitle(title: String): DownloadDialog {
        binding.downloadTitle.text = title
        return this
    }

    fun setContent(content: String): DownloadDialog {
        binding.downloadContent.text = content
        return this
    }

    fun setOnCancelClickListener(listener: View.OnClickListener): DownloadDialog {
        binding.downloadCancel.setOnClickListener(listener)
        return this
    }
}