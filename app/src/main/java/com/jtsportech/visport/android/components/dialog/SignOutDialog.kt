package com.jtsportech.visport.android.components.dialog

import android.content.Context
import android.os.Bundle
import com.cloudhearing.android.lib_base.base.BaseBindingDialog
import com.jtsportech.visport.android.databinding.DialogSignOutBinding

/**
 * Author: BenChen
 * Date: 2024/03/01 10:04
 * Email:chenxiaobin@cloudhearing.cn
 */
class SignOutDialog(context: Context) : BaseBindingDialog<DialogSignOutBinding>(
    context,
    inflate = DialogSignOutBinding::inflate
) {

    private var mPositiveButtonlickListener: (() -> Unit)? = null
    private var mNegativeButtonlickListener: (() -> Unit)? = null

    fun setPositiveButtonlickListener(listener: () -> Unit) {
        mPositiveButtonlickListener = listener
    }

    fun setNegativeButtonlickListener(listener: () -> Unit) {
        mNegativeButtonlickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            tvPositive.setOnClickListener {
                dismiss()

                mPositiveButtonlickListener?.invoke()
            }

            tvNegative.setOnClickListener {
                dismiss()

                mNegativeButtonlickListener?.invoke()
            }
        }
    }

    fun setTitle(text: String) {
        binding.tvTitle.text = text
    }

    fun setSubtitle(text: String) {
        binding.tvSubtitle.text = text
    }

    fun setPositiveButtonLable(text: String) {
        binding.tvPositive.text = text
    }

    fun setNegativeButtonLable(text: String) {
        binding.tvNegative.text = text
    }
}