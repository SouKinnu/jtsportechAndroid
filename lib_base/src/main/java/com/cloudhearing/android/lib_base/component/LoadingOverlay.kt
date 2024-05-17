package com.cloudhearing.android.lib_base.component

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.R
import com.cloudhearing.android.lib_base.utils.toDp


/**
 * Author: BenChen
 * Date: 2021/12/20 10:54
 * Email:chenxiaobin@cloudhearing.cn
 */
class LoadingOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RelativeLayout(context, attrs, R.style.LoadingOverlay) {

    private val mBgView: View = View(context, attrs)
    private val mProgressBar: ProgressBar = ProgressBar(context, attrs)
    private val mMessageTextView: AppCompatTextView =
        AppCompatTextView(context, attrs, R.style.LoadingOverlay_Text)

    init {
        isClickable = true
        isFocusable = true

        mBgView.apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(160.toDp.toInt(), 160.toDp.toInt()).apply {
                addRule(CENTER_IN_PARENT)
            }
            background = ContextCompat.getDrawable(context, R.drawable.shape_toast_bg)
        }.also {
            addView(it)
        }

        mProgressBar.apply {
            id = View.generateViewId()
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_IN_PARENT)
                }
            indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.ecstasy))
        }.also {
            addView(it)
        }

        mMessageTextView.apply {
            id = View.generateViewId()
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(ALIGN_START, mBgView.id)
                    addRule(ALIGN_END, mBgView.id)
                    addRule(ALIGN_PARENT_BOTTOM,mBgView.id)
                    setMargins(10.toDp.toInt(), 0, 10.toDp.toInt(), 30.toDp.toInt())
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    gravity = Gravity.CENTER
                }
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
        }.also {
            addView(it)
        }

        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        elevation = 80.toDp


    }

    fun setFocus(isFoucs: Boolean) {
        isFocusable = isFoucs
        isFocusableInTouchMode = isFoucs
        if (isFoucs) {
            requestFocus()
            requestFocusFromTouch()
        }
    }

    fun setMessage(message: String){
        mMessageTextView.text = message
    }

    fun show(@StringRes messageId: Int? = null) {
        messageId?.let {
            mMessageTextView.text = resources.getString(it)
        }
        visibility = View.VISIBLE
    }

    fun show(message: String) {
        mMessageTextView.text = message
        visibility = View.VISIBLE
        invalidate()
    }

    fun hide() {
        mMessageTextView.text = ""
        visibility = View.GONE
    }
}