package com.jtsportech.visport.android.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.cloudhearing.android.lib_base.utils.hide
import com.cloudhearing.android.lib_base.utils.show
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_base.utils.toSp
import com.jtsportech.visport.android.R
import com.ruijin.android.rainbow.components.TextViewMarquee

/**
 * Author: BenChen
 * Date: 2024/01/02 13:54
 * Email:chenxiaobin@cloudhearing.cn
 */
class SubmenuButton(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    private val mIvIcon = AppCompatImageView(context)
    private val mTvTitle = AppCompatTextView(context)
    private val mIbTitleSide = AppCompatImageButton(context)
    private val mTvSubTitle = AppCompatTextView(context)
    private val mIvMore = AppCompatImageView(context)
    private val mTvStateInfo = TextViewMarquee(context)
    private val mUnderscoreView: View = View(context)

    private var mIcon: Drawable? = null
    private var mMoreIcon: Drawable? = null
    private var mTitle: String = ""
    private var mTitleSize: Float = 13.toSp
    private var mTitleColor: Int = 0

    private var mSubTitle: String = ""
    private var mSubTitleSize: Float = 8.toSp
    private var mSubTitleColor: Int = ContextCompat.getColor(context, R.color.mine_shaft)

    private var mStateInfo: String = ""
    private var mStateInfoSize: Float = 15.toSp
    private var mStateInfoColor: Int = 0

    private var mIsShowMore: Boolean = true
    private var mIsShowTitleSideView: Boolean = false

    init {
        parseAttrs(context, attrs)

//        setClickableAndFocusable(true)

        background = ContextCompat.getDrawable(context, R.drawable.background_button_submenu)

        mIvIcon.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    adjustViewBounds = true
                    addRule(CENTER_VERTICAL)
                    addRule(ALIGN_PARENT_START)
                    marginStart = 16.toDp.toInt()
                    setImageDrawable(mIcon)
                }
        }.also {
            addView(it)
        }

        mTvTitle.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    setTextColor(mTitleColor)
                    setTextSize(COMPLEX_UNIT_PX, mTitleSize)
                    addRule(CENTER_VERTICAL)
                    addRule(END_OF, mIvIcon.id)
                    marginStart = 14.toDp.toInt()
                    text = mTitle
                    maxWidth = 200.toDp.toInt()
                }
        }.also {
            addView(it)
        }
        mIbTitleSide.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    addRule(CENTER_VERTICAL)
                    addRule(END_OF, mTvTitle.id)
                    adjustViewBounds = true
                    marginStart = 4.toDp.toInt()
//                    setImageResource(R.drawable.ic_connect_question)
//                    background = getSelectableItemBackgroundBorderlessDrawable(context)
                }
        }.also {
            addView(it)
            if (mIsShowTitleSideView)
                it.visibility = View.VISIBLE
            else
                it.visibility = View.GONE
        }
        mTvSubTitle.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    setTextColor(mSubTitleColor)
                    setTextSize(COMPLEX_UNIT_PX, mSubTitleSize)
                    addRule(BELOW, mTvTitle.id)
                    marginStart = 16.toDp.toInt()
                    text = mSubTitle
                }
        }.also {
            addView(it)
        }
        mIvMore.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    adjustViewBounds = true
                    addRule(CENTER_VERTICAL)
                    addRule(ALIGN_PARENT_END)
                    marginEnd = 10.toDp.toInt()
                    setImageDrawable(mMoreIcon)
                }
        }.also {
            addView(it)
            if (mIsShowMore)
                it.visibility = View.VISIBLE
            else
                it.visibility = View.INVISIBLE
        }

        mTvStateInfo.apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    id = View.generateViewId()
                    addRule(CENTER_VERTICAL)
                    addRule(END_OF, mTvTitle.id)
                    addRule(START_OF, mIvMore.id)
                    marginEnd = 16.toDp.toInt()
                    marginStart = 20.toDp.toInt()
                    gravity = Gravity.END
                    setTextColor(mStateInfoColor)
                    setTextSize(COMPLEX_UNIT_PX, mStateInfoSize)
                    text = mStateInfo
                }
        }.also {
            addView(it)
        }

        mUnderscoreView.apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.toDp.toInt()).apply {
                addRule(ALIGN_PARENT_BOTTOM)
                marginStart = 30.toDp.toInt()
                marginEnd = 10.toDp.toInt()
            }
        }.also {
            addView(it)
        }
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.SubmenuButton)
        mTitle = typedArray.getString(R.styleable.SubmenuButton_title).orEmpty()
        mTitleSize = typedArray.getDimension(R.styleable.SubmenuButton_titleSize, mTitleSize)
        mTitleColor = typedArray.getColor(
            R.styleable.SubmenuButton_titleColor,
            ContextCompat.getColor(context, R.color.mine_shaft)
        )
        mSubTitle = typedArray.getString(R.styleable.SubmenuButton_subTitle).orEmpty()
        mSubTitleSize =
            typedArray.getDimension(R.styleable.SubmenuButton_subTitleSize, mSubTitleSize)
        mSubTitleColor = typedArray.getColor(
            R.styleable.SubmenuButton_subTitleColor,
            ContextCompat.getColor(context, R.color.mine_shaft)
        )
        mStateInfo = typedArray.getString(R.styleable.SubmenuButton_stateInfo).orEmpty()
        mStateInfoSize =
            typedArray.getDimension(R.styleable.SubmenuButton_titleSize, mStateInfoSize)
        mStateInfoColor = typedArray.getColor(
            R.styleable.SubmenuButton_stateInfoColor,
            ContextCompat.getColor(context, R.color.black)
        )
        mIsShowMore = typedArray.getBoolean(R.styleable.SubmenuButton_isShowMore, true)
        setClickableAndFocusable(
            typedArray.getBoolean(
                R.styleable.SubmenuButton_isClickableAndFocusable,
                true
            )
        )
        mIsShowTitleSideView =
            typedArray.getBoolean(R.styleable.SubmenuButton_isShowTitleSideView, false)
        mMoreIcon =
            typedArray.getDrawable(R.styleable.SubmenuButton_moreIcon) ?: ContextCompat.getDrawable(
                context,
                R.drawable.icon_center_arrow_gray
            )

        val isShowUnderscore =
            typedArray.getBoolean(R.styleable.SubmenuButton_isShowUnderscore, false)

        if (isShowUnderscore) {
            setUnderscoreColor(
                typedArray.getColor(
                    R.styleable.SubmenuButton_bar_underscore_color,
                    ContextCompat.getColor(context, R.color.porcelain)
                )
            )
        }

        mIcon =
            typedArray.getDrawable(R.styleable.SubmenuButton_titleIcon)


        typedArray.recycle()
    }

    private fun setClickableAndFocusable(clickable: Boolean) {
        isClickable = clickable
        isFocusable = clickable
    }

    fun setTitle(@StringRes id: Int) {
        mTvTitle.setText(id)
    }

    fun setTitle(title: String) {
        mTvTitle.text = title
    }

    fun setStateInfo(@StringRes id: Int) {
        mTvStateInfo.setText(id)
    }

    fun setStateInfo(info: String) {
        mTvStateInfo.text = info
    }

    fun setStateInfoColor(colorResId: Int) {
        mStateInfoColor = ContextCompat.getColor(context, colorResId)
        mTvStateInfo.setTextColor(mStateInfoColor)
    }

    fun setShowTitleSideView(isShow: Boolean) {
        mIbTitleSide.isVisible = isShow
    }

    fun getTitleSideView(): View {
        return mIbTitleSide
    }

    fun setUnderscoreColor(color: Int) {
        mUnderscoreView.setBackgroundColor(color)
    }

    fun setMoreIcon(@DrawableRes ids: Int) {
        mMoreIcon = ContextCompat.getDrawable(context, ids)
        mIvMore.setImageDrawable(mMoreIcon)
    }

    fun setShowMoreIcon(isShow: Boolean) {
        if (isShow) {
            mIvMore.show()
        } else {
            mIvMore.hide()
        }
    }
}