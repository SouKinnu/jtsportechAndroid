package com.jtsportech.visport.android.racedetail.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jtsportech.visport.android.R

class PopupBallPerformance(context: Context?) : PopupWindow(context) {
    private var mContext: Context
    private var view: View
    private var bg: Drawable
    private lateinit var ballClose: ImageView
    private lateinit var ballGood: TextView
    private lateinit var ballNormal: TextView
    private lateinit var ballPoor: TextView
    private lateinit var onPerformanceClickListener: OnPerformanceClickListener

    fun setOnPerformanceClickListener(listener: OnPerformanceClickListener): PopupBallPerformance {
        onPerformanceClickListener = listener
        return this
    }

    init {
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        view = mInflater.inflate(R.layout.dialog_ball_performance, null)

        initView()
        bg = ContextCompat.getDrawable(context, R.drawable.shape_ball_performance_bg)!!
        contentView = view
        setBackgroundDrawable(bg)
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    return event.action == MotionEvent.ACTION_OUTSIDE
                }
                return false
            }
        })
        ballClose.setOnClickListener { dismiss() }
        ballGood.setOnClickListener { onPerformanceClickListener.good() }
        ballNormal.setOnClickListener { onPerformanceClickListener.normal() }
        ballPoor.setOnClickListener { onPerformanceClickListener.poor() }
    }

    private fun initView() {
        isFocusable = false
        isTouchable = true
        isOutsideTouchable = false

        ballClose = view.findViewById(R.id.ball_close)
        ballGood = view.findViewById(R.id.ball_good)
        ballNormal = view.findViewById(R.id.ball_normal)
        ballPoor = view.findViewById(R.id.ball_poor)
    }

    interface OnPerformanceClickListener {
        fun good()
        fun normal()
        fun poor()
    }
}