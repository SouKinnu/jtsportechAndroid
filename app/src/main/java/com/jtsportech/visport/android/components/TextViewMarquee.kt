/*
 * 19-8-23 下午5:45 coded form Zhonghua.
 */

package com.ruijin.android.rainbow.components

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewMarquee @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        //设置为跑马灯显示
        ellipsize = TextUtils.TruncateAt.MARQUEE
        //获取焦点
        isFocusable = true
        //可以通过toucth来获得focus
        isFocusableInTouchMode = true
        //设置重复的次数
        marqueeRepeatLimit = -1
        //单行显示文字
        isSingleLine = true
        isSelected = true
    }

    override fun isFocused(): Boolean {
        return true
    }

    fun isMarqueenText(): Boolean {
        return ellipsize === TextUtils.TruncateAt.MARQUEE && isFocused
    }

    fun startMaquee(start: Boolean) {
        ellipsize = if (start) TextUtils.TruncateAt.MARQUEE else TextUtils.TruncateAt.END
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startMaquee(true)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        startMaquee(false)
    }
}