package com.jtsportech.visport.android.components

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.BOTTOM
import android.view.Gravity.CENTER
import android.view.View
import android.widget.RelativeLayout
import android.widget.RelativeLayout.BELOW
import android.widget.RelativeLayout.CENTER_HORIZONTAL
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_base.utils.toSp
import com.jtsportech.visport.android.R

/**
 * Author: BenChen
 * Date: 2024/01/03 10:48
 * Email:chenxiaobin@cloudhearing.cn
 */
class NavigationBarView(context: Context, attrs: AttributeSet?) :
    LinearLayoutCompat(context, attrs) {

    private val navigationItems: MutableList<Navigation> = mutableListOf()
    private val clickableViewIds: MutableList<ViewID> = mutableListOf()

    private val backgroundSize: Int = 64.toDp.toInt()
    private val textMargin: Int = 2.toSp.toInt()
    private val underlineHeight: Int = 1.toDp.toInt()

    var currentIndex: Int = 0
        set(value) {
            field = value
            if (clickableViewIds.isNotEmpty()) {
                val viewId =
                    if (value < clickableViewIds.size) clickableViewIds[value].rootViewId else clickableViewIds[0].rootViewId
                onUpdateState(viewId)
            }
        }

    var backgroundActive: Boolean = false
        set(value) {
            field = value
            onUpdateState(value)
        }

    var unSelectedTextColor: Int = ContextCompat.getColor(context, android.R.color.darker_gray)
        set(value) {
            field = value
            updateTextColors()
        }

    var selectedTextColor: Int = ContextCompat.getColor(context, android.R.color.black)
        set(value) {
            field = value
            updateTextColors()
        }

    var fontSize: Float = 14f
        set(value) {
            field = value
            updateTextSize()
        }

    init {
        orientation = HORIZONTAL
        clipChildren = false
        clipToPadding = false
        elevation = 10.toDp
        parseAttrs(context, attrs)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.NavigationBarView)

        unSelectedTextColor = typedArray.getColor(
            R.styleable.NavigationBarView_unSelectedTextColor,
            ContextCompat.getColor(context, android.R.color.darker_gray)
        )
        selectedTextColor = typedArray.getColor(
            R.styleable.NavigationBarView_selectedTextColor,
            ContextCompat.getColor(context, android.R.color.black)
        )
        fontSize = typedArray.getFloat(
            R.styleable.NavigationBarView_fontSize,
            14f
        )

        typedArray.recycle()

        applyWindowInsets()
    }

    private fun applyWindowInsets() {
//        doOnApplyWindowInsets { view,  _, padding, _ ->
//            view.setPadding(
//                0, 0, 0, padding.bottom
//            )
//        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun addNavigations(list: List<Navigation>) {
        if (navigationItems.isEmpty()) {
            navigationItems.addAll(list)
            renderViews()
        }
    }

    private fun renderViews() {
        navigationItems.forEachIndexed { index, item ->
            val relativeLayout = RelativeLayout(context).apply {
                id = View.generateViewId()
                layoutParams = LayoutParams(
                    0,
                    if (item.background == 0) LayoutParams.MATCH_PARENT else backgroundSize
                ).apply {
                    weight = 1F
                    if (item.background != 0) gravity = BOTTOM
                }
            }.also {
                addView(it)
                it.setOnClickListener { view ->
                    currentIndex = index
                    onItemClickListener?.invoke(index)
                }
            }

            if (item.background != 0) {
                val backgroundImageView = createBackgroundImageView(item.background)
                relativeLayout.addView(backgroundImageView)
            }

            val imageView = createIconImageView(item.icon)
            relativeLayout.addView(imageView)

            if (item.isTextShow && item.text != 0) {
                val textView = createTextView(item.text, imageView.id)
                relativeLayout.addView(textView)
                if (item.background == 0) {
                    clickableViewIds.add(ViewID(relativeLayout.id, imageView.id, textView.id))
                }
            } else if (item.background == 0) {
                clickableViewIds.add(ViewID(relativeLayout.id, imageView.id))
            }
        }

        onUpdateState(clickableViewIds[currentIndex].rootViewId)
    }

    private fun createBackgroundImageView(backgroundRes: Int): AppCompatImageView {
        return AppCompatImageView(context).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(backgroundSize, backgroundSize).apply {
                addRule(CENTER_HORIZONTAL)
            }
            adjustViewBounds = true
            background = ContextCompat.getDrawable(context, backgroundRes)
        }
    }

    private fun createIconImageView(iconRes: Int): AppCompatImageView {
        return AppCompatImageView(context).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(CENTER_HORIZONTAL)
                setMargins(0, 6.toDp.toInt(), 0, 0)
            }
            adjustViewBounds = true
            setImageResource(iconRes)
        }
    }

    private fun createTextView(textRes: Int, imageViewId: Int): AppCompatTextView {
        return AppCompatTextView(context).apply {
            id = View.generateViewId()
            layoutParams = RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(BELOW, imageViewId)
                addRule(CENTER_HORIZONTAL)
                setMargins(0, textMargin, 0, textMargin)
            }
            setTextColor(unSelectedTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
            text = resources.getString(textRes)
            gravity = CENTER
        }
    }

    private fun onUpdateState(id: Int) {
        val action = clickableViewIds.any { it.rootViewId == id }
        if (!action) return

        clickableViewIds.forEach {
            val isSelected = it.rootViewId == id
            findViewById<AppCompatImageView>(it.iconId)?.isSelected = isSelected
            findViewById<AppCompatTextView>(it.textId)?.setTextColor(
                if (isSelected) selectedTextColor else unSelectedTextColor
            )
        }
    }

    private fun updateTextColors() {
        clickableViewIds.forEach { viewId ->
            val isSelected = viewId.rootViewId == clickableViewIds[currentIndex].rootViewId
            val textView = findViewById<AppCompatTextView>(viewId.textId)
            textView?.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isSelected) selectedTextColor else unSelectedTextColor
                )
            )
        }
    }

    private fun updateTextSize() {
        clickableViewIds.forEach { viewId ->
            val textView = findViewById<AppCompatTextView>(viewId.textId)
            textView?.textSize = fontSize
        }
    }

    private fun onUpdateState(active: Boolean) {
        getBackgroundImageView()?.isSelected = active
    }

    fun getBackgroundImageView(): AppCompatImageView? =
        findViewById(clickableViewIds[currentIndex].rootViewId)

    data class Navigation(
        @DrawableRes val icon: Int = 0,
        @StringRes var text: Int = 0,
        val isTextShow: Boolean = false,
        @DrawableRes val background: Int = 0,
    )

    data class ViewID(
        val rootViewId: Int,
        val iconId: Int = 0,
        val textId: Int = 0
    )
}