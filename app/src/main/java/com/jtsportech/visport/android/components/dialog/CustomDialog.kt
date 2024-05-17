package com.jtsportech.visport.android.components.dialog

import android.content.Context
import android.view.View
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.jtsportech.visport.android.databinding.DialogCustomBinding
import com.jtsportech.visport.android.databinding.DialogDownloadBinding

class CustomDialog(context: Context) : BaseBindingDialog<DialogCustomBinding>(
    context,
    inflate = DialogCustomBinding::inflate,
    widthPercentage = 0.5f
) {

    fun setTitle(title: String): CustomDialog {
        binding.title.text = title
        return this
    }

    fun setContent(content: String): CustomDialog {
        binding.content.text = content
        return this
    }

    fun setOnCancelClickListener(listener: View.OnClickListener): CustomDialog {
        binding.cancel.setOnClickListener(listener)
        return this
    }
    fun setOnSureClickListener(listener: View.OnClickListener): CustomDialog {
        binding.commit.setOnClickListener(listener)
        return this
    }
}