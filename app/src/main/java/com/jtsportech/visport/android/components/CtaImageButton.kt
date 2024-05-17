package com.jtsportech.visport.android.components

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.utils.getSelectableItemBackgroundBorderlessDrawable
import com.jtsportech.visport.android.R


class CtaImageButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {

    var isEnable: Boolean = false
        private set

    private var enableIcon: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.icon_agree
    )


    private var disableIcon: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.icon_no_agree
    )


    init {
        parseAttrs(context, attrs)
        disable()
        background = getSelectableItemBackgroundBorderlessDrawable(context)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CtaImageButton)
        typedArray.getDrawable(R.styleable.CtaImageButton_enableIcon)?.let {
            enableIcon = it
        }
        typedArray.getDrawable(R.styleable.CtaImageButton_disableIcon)?.let {
            disableIcon = it
        }

        typedArray.recycle()
    }

    fun disable() {
        isEnable = false
        setImageDrawable(disableIcon)
    }

    fun enable() {
        isEnable = true
        setImageDrawable(enableIcon)
    }
}