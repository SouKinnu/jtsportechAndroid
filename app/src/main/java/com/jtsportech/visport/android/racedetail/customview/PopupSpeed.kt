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
import com.cloudhearing.android.lib_common.utils.DensityUtils
import com.jtsportech.visport.android.R

class PopupSpeed(context: Context?) :
    PopupWindow(context) {
    private var mContext: Context
    private var view: View
    private var bg: Drawable
    private lateinit var onSpeedClickListener: OnSpeedClickListener
    private var currentSpeed: String = "1.0"
    private var speedsGroup: RadioGroup
    private val buttons = ArrayList<RadioButton>()
    private var speeds: Array<String>

    fun setCurrentSpeed(speed: String): PopupSpeed {
        currentSpeed = speed
        changeCheck()
        return this
    }

    fun setOnSpeedClickListener(listener: OnSpeedClickListener): PopupSpeed {
        onSpeedClickListener = listener
        return this
    }

    init {
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        view = mInflater.inflate(R.layout.pop_visual_angle, null)
        speedsGroup = view.findViewById(R.id.visual_angle_group)
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
        speeds = context.resources.getStringArray(R.array.speeds)
        speeds.forEachIndexed { index: Int, it: String ->
            buttons.add(radioButton(it, index))
            speedsGroup.addView(buttons[index])
        }
        changeCheck()
    }

    private fun radioButton(name: String, index: Int): RadioButton {
        return RadioButton(mContext).apply {
            background = null
            setButtonDrawable(0)
            gravity = Gravity.CENTER
            textSize = DensityUtils.px2sp(mContext.resources.getDimension(R.dimen.sp_14))
            setTextColor(context.getColor(R.color.white))
            text = name + "X"
            setPadding(5, 5, 5, 5)
            View.generateViewId()
            setOnClickListener {
                if (name != currentSpeed) {
                    currentSpeed = name
                    changeCheck()
                    onSpeedClickListener.onSelectSpeed(currentSpeed)
                }
            }
        }
    }

    private fun changeCheck() {
        buttons.forEachIndexed { index, button ->
            if (speeds[index] == currentSpeed) {
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

    interface OnSpeedClickListener {
        fun onSelectSpeed(speedStr: String)
    }
}