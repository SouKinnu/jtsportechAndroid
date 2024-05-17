package com.jtsportech.visport.android.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.utils.toDp
import com.google.android.material.button.MaterialButton
import com.jtsportech.visport.android.R


/**
 * Call-to-action button with disabled and enabled states.
 * @constructor
 */
class CtaButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {


    init {
//        cornerRadius = 6.toDp.toInt()
//        cornerRadius = 30.toDp.toInt()
        parseAttrs(context, attrs)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CtaButton)
        typedArray.getBoolean(R.styleable.CtaButton_actvieState, false).let {
            if (it) enable() else disable()
        }

        typedArray.recycle()
    }

    fun disable() {
        isEnabled = false
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.mercury))
//        setBackgroundColor(ContextCompat.getColor(context,R.color.mercury))
        elevation = resources.getDimension(R.dimen.button_elevation_disabled)
        setTextColor(resources.getColor(R.color.gray))
    }

    fun enable() {
        isEnabled = true
        // MaterialButton 不支持设置渐变的背景
        backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.ecstasy))
//        background = ContextCompat.getDrawable(context, R.drawable.shape_login_button_bg)
        elevation = resources.getDimension(R.dimen.button_elevation_active)
        setTextColor(resources.getColor(R.color.white))
    }
}
