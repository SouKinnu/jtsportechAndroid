package com.cloudhearing.android.lib_base.utils

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * Creates a new ClickableSpan.
 * @property addUnderline Boolean Whether to underline the link or not.
 * @property callback Function<Unit>
 * @constructor
 */
class SpannableClickHandler(val addUnderline: Boolean = true, private val callback: () -> Unit) :
    ClickableSpan() {
    override fun onClick(widget: View) {
        callback()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = addUnderline
    }
}
