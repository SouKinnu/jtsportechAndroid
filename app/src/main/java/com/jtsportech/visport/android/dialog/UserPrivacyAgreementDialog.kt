package com.jtsportech.visport.android.dialog

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.DialogPrivacyShowBinding
import com.jtsportech.visport.android.dialog.base.BindingDialog


class UserPrivacyAgreementDialog(context: Context) :
    BindingDialog<DialogPrivacyShowBinding>(
        context,
        inflate = DialogPrivacyShowBinding::inflate,
        width = 0.85f
    ) {
    private var mOnClickListener: ((Int) -> Unit)? = null
    fun setOnClickListener(listener: (Int) -> Unit) {
        mOnClickListener = listener
    }

    companion object {
        //用户协议
        private const val ARGEEMENT_TEXT_CLICK: Int = 1

        //隐私协议
        private const val SECRET_TEXT_CLICK: Int = 2

        //同意按钮
        private const val ARGEE_BTN_CLICK: Int = 3

        //不同意按钮
        private const val NOT_ARGEE_BTN_CLICK: Int = 4

        //用户协议需要加颜色的开始文字位置
        private var START_AG: Int = 0

        //结束
        private var END_AG: Int = 0

        //隐私开始文字位置
        private var START_SECRET = 0

        //结束
        private var END_SECRET = 0
    }

    init {
        val layoutParams = window!!.attributes
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = Gravity.CENTER
        window!!.attributes = layoutParams

        val argContent = ContextCompat.getString(context, R.string.privacy_agreement)
        val serviceStr = ContextCompat.getString(context, R.string.account_user_agreement)
        val privateStr = ContextCompat.getString(context, R.string.account_privacy_agreement)
        START_AG = argContent.indexOf(serviceStr)
        END_AG = START_AG + serviceStr.length
        START_SECRET = argContent.indexOf(privateStr)
        END_SECRET = START_SECRET + privateStr.length
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context, R.color.cerulean))
        val colorSpan2 = ForegroundColorSpan(ContextCompat.getColor(context, R.color.cerulean))
        val underlineSpan = UnderlineSpan()
        val underlineSpan2 = UnderlineSpan()
        val userArgeeClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mOnClickListener?.invoke(ARGEEMENT_TEXT_CLICK)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(context, R.color.cerulean)
                ds.isUnderlineText = false//去除连接下划线
                ds.clearShadowLayer()
            }
        }
        val secretClick = object : ClickableSpan() {
            override fun onClick(widget: View) {
                mOnClickListener?.invoke(SECRET_TEXT_CLICK)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ContextCompat.getColor(context, R.color.cerulean)
                ds.isUnderlineText = false//去除连接下划线
                ds.clearShadowLayer()
            }
        }
        binding.content.text = SpannableString(argContent).apply {
            setSpan(colorSpan, START_AG, END_AG, Spannable.SPAN_INTERMEDIATE)
            setSpan(underlineSpan, START_AG + 1, END_AG - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(userArgeeClick, START_AG, END_AG, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(colorSpan2, START_SECRET, END_SECRET, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            setSpan(
                underlineSpan2,
                START_SECRET + 1,
                END_SECRET - 1,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            setSpan(
                secretClick,
                START_SECRET,
                END_SECRET,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        binding.agreeBtn.setOnClickListener {
            mOnClickListener?.invoke(ARGEE_BTN_CLICK)
        }
        binding.noAgreeBtn.setOnClickListener {
            mOnClickListener?.invoke(NOT_ARGEE_BTN_CLICK)
        }
        binding.content.movementMethod = LinkMovementMethod.getInstance()
        //设置点击背景色透明  解决点击时有阴影效果
        binding.content.highlightColor =
            ContextCompat.getColor(context, android.R.color.transparent)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    fun initView() {

    }
}