package com.jtsportech.visport.android.components

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.jtsportech.visport.android.R


class AppBarView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val mTitle: AppCompatTextView = AppCompatTextView(context)
    private val mLeftImageView: AppCompatImageView = AppCompatImageView(context)
    private val mRightImageView: AppCompatImageView = AppCompatImageView(context)
    private val mRightTextView: AppCompatTextView = AppCompatTextView(context)
    private val mUnderscoreView: View = View(context)

    private val mDefaultTitleTextSize: Float = 17f

    private val mDefaultRightTextSize: Float = 15f

    private var mLeftIconListener: (() -> Unit?)? = null

    private var mRightIconListener: ((view: View) -> Unit)? = null

    private var mRightTextListener: (() -> Unit)? = null

    fun setOnClickLeftIconListener(listener: () -> Unit) {
        mLeftIconListener = listener
    }

    fun setOnClickRightIconListener(listener: (view: View) -> Unit) {
        mRightIconListener = listener
    }

    fun setOnClickRightTextListener(listener: () -> Unit) {
        mRightTextListener = listener
    }

    init {
        setBackgroundColor(resources.getColor(R.color.white))

        parseAttrs(attrs)

        mLeftImageView.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_VERTICAL)
                    marginStart = 16.toDp.toInt()
                    setPadding(10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt())
//                    background = getSelectableItemBackgroundBorderlessDrawable(context)
                }
        }.also {
            addView(it)
            it.setOnClickListener {
                mLeftIconListener?.invoke()
            }
        }

        mTitle.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_IN_PARENT)
//                    textSize = 5.toSp
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
//                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    textAlignment = TEXT_ALIGNMENT_CENTER
                }
        }.also {
            addView(it)
        }

        mRightImageView.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_VERTICAL)
                    addRule(ALIGN_PARENT_END)
                    marginEnd = 16.toDp.toInt()
                    setPadding(10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt())
//                    background = getSelectableItemBackgroundBorderlessDrawable(context)
                }
        }.also {
            addView(it)
            it.setOnClickListener { v ->
                mRightIconListener?.invoke(v)
            }
        }

        mRightTextView.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    addRule(CENTER_VERTICAL)
                    addRule(ALIGN_PARENT_END)
                    marginEnd = 16.toDp.toInt()
//                    textSize = 4.toSp
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    setPadding(10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt(), 10.toDp.toInt())
                }
        }.also {
            addView(it)
            it.setOnClickListener {
                mRightTextListener?.invoke()
            }
        }

        mUnderscoreView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.toDp.toInt()).apply {
                addRule(ALIGN_PARENT_BOTTOM)
            }
        }.also {
            addView(it)
        }

    }

    private fun parseAttrs(attrs: AttributeSet?) = attrs?.let { attr ->
        context.obtainStyledAttributes(attr, R.styleable.AppBarView).apply {
            setTitle(getString(R.styleable.AppBarView_title) ?: "")
            setTitleColor(getColor(R.styleable.AppBarView_titleColor, Color.BLACK))
            setTitleSize(getDimension(R.styleable.AppBarView_titleSize, mDefaultTitleTextSize))
            getDrawable(R.styleable.AppBarView_bar_left_icon)?.let { setLeftIcon(it) }
            getDrawable(R.styleable.AppBarView_bar_right_icon)?.let { setRightIcon(it) }
            setRightText(getString(R.styleable.AppBarView_bar_right_text) ?: "")
            setRightTextColor(getColor(R.styleable.AppBarView_bar_right_textColor, Color.BLACK))
            setRightTextSize(
                getDimension(
                    R.styleable.AppBarView_bar_right_textSize,
                    mDefaultRightTextSize
                )
            )
            setUnderscoreColor(
                getColor(
                    R.styleable.AppBarView_bar_underscore_color,
                    Color.parseColor("#D0C9D6")
                )
            )
            recycle()
        }
    }

    fun setTitle(title: String) {
        mTitle.text = title
    }

    fun setTitle(title: CharSequence) {
        mTitle.text = title
    }

    fun setTitleColor(color: Int) {
        mTitle.setTextColor(color)
    }

    fun setTitleSize(size: Float) {
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setLeftIcon(drawable: Drawable) {
        mLeftImageView.setImageDrawable(drawable)
    }

    fun setLeftIconDisplay(isDisplay: Boolean) {
        mLeftImageView.isVisible = isDisplay
    }

    fun setRightIcon(drawable: Drawable) {
        mRightImageView.setImageDrawable(drawable)
    }

    fun setRightText(text: String) {
        mRightTextView.text = text
        isShowRightText(text.isNotEmpty())
    }

    fun isShowRightText(show: Boolean) {
        if (show) mRightTextView.show()
        else mRightTextView.hide()
    }

    fun setRightTextColor(color: Int) {
        mRightTextView.setTextColor(color)
    }

    fun setRightTextSize(size: Float) {
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setUnderscoreColor(color: Int) {
        mUnderscoreView.setBackgroundColor(color)
    }
}