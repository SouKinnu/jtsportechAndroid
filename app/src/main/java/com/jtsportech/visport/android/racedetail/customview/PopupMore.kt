package com.jtsportech.visport.android.racedetail.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jtsportech.visport.android.R

class PopupMore(context: Context?) : PopupWindow(context) {
    private var mContext: Context
    private var viewMore: View
    private var bgBlack: Drawable
    private var collectVideo: LinearLayout
    private var collectEvents: LinearLayout
    private var shareVideo: LinearLayout
    private var shareEvents: LinearLayout
    private var ivCollect: ImageView
    private var ivCollects: ImageView
    private var tvCollect: TextView
    private var tvCollects: TextView

    init {
        val mInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mContext = context
        viewMore = mInflater.inflate(R.layout.pop_more, null)
        collectVideo = viewMore.findViewById(R.id.collect_video)
        collectEvents = viewMore.findViewById(R.id.collect_events)
        shareVideo = viewMore.findViewById(R.id.share_video)
        shareEvents = viewMore.findViewById(R.id.share_events)
        ivCollect = viewMore.findViewById(R.id.iv_collect)
        ivCollects = viewMore.findViewById(R.id.iv_collects)
        tvCollect = viewMore.findViewById(R.id.tv_collect)
        tvCollects = viewMore.findViewById(R.id.tv_collects)

        initView()
        bgBlack = ContextCompat.getDrawable(context, R.drawable.bg_black)!!
        val displayMetrics = context.resources.displayMetrics
        contentView = viewMore
        width = displayMetrics.widthPixels / 3
//        height = displayMetrics.heightPixels * 5 / 17
        setBackgroundDrawable(bgBlack)
        setTouchInterceptor(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    return event.action == MotionEvent.ACTION_OUTSIDE
                }
                return false
            }
        })
        viewMore.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return true
                }
                return false
            }
        })

    }

    private fun initView() {
        isFocusable = true
        isTouchable = true
    }

    fun setOnCollectionVideoListener(l: View.OnClickListener) {
        collectVideo.setOnClickListener(l)
    }

    fun setOnCollectionEventsListener(l: View.OnClickListener) {
        collectEvents.setOnClickListener(l)
    }

    fun setOnShareVideoListener(l: View.OnClickListener) {
        shareVideo.setOnClickListener(l)
    }

    fun setOnShareEventsListener(l: View.OnClickListener) {
        shareEvents.setOnClickListener(l)
    }



    fun setCollectSelected(isSelect: Boolean) {
        ivCollect.isSelected = isSelect
        tvCollect.text =
            if (isSelect)
                mContext.resources.getString(R.string.collection_video_un)
            else
                mContext.resources.getString(R.string.collection_video)
    }

    fun setCollectsSelected(isSelect: Boolean) {
        ivCollects.isSelected = isSelect
        tvCollects.text =
            if (isSelect)
                mContext.resources.getString(R.string.collection_compilation_un)
            else
                mContext.resources.getString(R.string.collection_compilation)
    }
}