package com.jtsportech.visport.android.racedetail.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.Video
import com.jtsportech.visport.android.R

class PopupVisualAngle(context: Context?, visualAngles: List<Video>) :
    PopupWindow(context) {
    private var mContext: Context
    private var view: View
    private var bg: Drawable
    private lateinit var onVisualAngleClickListener: OnVisualAngleClickListener
    private var currentPosition: Int = 0
    private var visualAngleGroup: RadioGroup
    private val buttons = ArrayList<RadioButton>()

    fun setCurrentVisualAnglePosition(position: Int): PopupVisualAngle {
        currentPosition = position
        changeCheck()
        return this
    }

    fun setOnVisualAngleClickListener(listener: OnVisualAngleClickListener): PopupVisualAngle {
        onVisualAngleClickListener = listener
        return this
    }

    init {
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        view = mInflater.inflate(R.layout.pop_visual_angle, null)
        visualAngleGroup = view.findViewById(R.id.visual_angle_group)
        initView()
        bg = ContextCompat.getDrawable(context, R.drawable.shape_ball_performance_bg)!!
        contentView = view
        val displayMetrics = context.resources.displayMetrics
        height = displayMetrics.widthPixels
        setBackgroundDrawable(bg)
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    return event.action == MotionEvent.ACTION_OUTSIDE
                }
                return false
            }
        })
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return true
                }
                return false
            }
        })

        visualAngles.forEachIndexed { index: Int, it: Video ->
            buttons.add(radioButton(it.perspective, index))
            visualAngleGroup.addView(buttons[index])
        }
        changeCheck()
    }

    private fun radioButton(name: String, index: Int): RadioButton {
        return RadioButton(mContext).apply {
            background = null
            setButtonDrawable(0)
            gravity = Gravity.CENTER
            textSize = 14f
            setTextColor(context.getColor(R.color.white))
            text = name
            setPadding(5, 5, 5, 5)
            View.generateViewId()
            setOnClickListener {
                if (index != currentPosition) {
                    currentPosition = index
                    changeCheck()
                    onVisualAngleClickListener.onVisualAngleCut(index)
                }
            }
        }
    }

    private fun changeCheck() {
        buttons.forEachIndexed { index, button ->
            if (index == currentPosition) {
                button.setTextColor(mContext.getColor(R.color.ecstasy))
            } else {
                button.setTextColor(mContext.getColor(R.color.white))
            }

        }
    }

    private fun initView() {
        isFocusable = true
        isTouchable = true
        isClippingEnabled = false
    }

    interface OnVisualAngleClickListener {
        fun onVisualAngleCut(position: Int)
    }
}