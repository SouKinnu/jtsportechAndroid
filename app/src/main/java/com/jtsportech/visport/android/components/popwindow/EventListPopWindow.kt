package com.jtsportech.visport.android.components.popwindow

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.cloudhearing.android.lib_base.utils.toDp
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dashboard.home.event.EventPopAdapter

/**
 * Author: BenChen
 * Date: 2024/03/22 18:45
 * Email:chenxiaobin@cloudhearing.cn
 */
class EventListPopWindow(val ctx: Context) : PopupWindow(ctx) {

    private val mEventPopAdapter: EventPopAdapter by lazy {
        EventPopAdapter().apply {
            setOnClickListener { competition, pos ->
                dismiss()
                mOnItemClickListener?.invoke(competition,pos)
            }
        }
    }

    private var mPopupView: View? = null

    private var mRvEventPop: RecyclerView? = null

    private var mOnItemClickListener: ((Competition,Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Competition,Int) -> Unit) {
        mOnItemClickListener = listener
    }

    init {
        init()
    }

    private fun init() {
        mPopupView = LayoutInflater.from(ctx).inflate(R.layout.popwindow_event_list, null)
        animationStyle = com.cloudhearing.android.lib_base.R.style.popwin_scale_top_anim
        contentView = mPopupView
        width = 100.toDp.toInt()
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(BitmapDrawable())
        isFocusable = true
        isTouchable = true
        isOutsideTouchable = true

        initView()
    }

    private fun initView() {
        mRvEventPop = mPopupView?.findViewById(R.id.rv_event_pop)
        mRvEventPop!!.adapter = mEventPopAdapter
    }

    fun setEventPopList(list: List<Competition>) {
        val newList = list.toMutableList()

        if (newList.isNotEmpty()) {
            newList.add(
                0,
                Competition(
                    name = ctx.getString(R.string.event_all_events)
                )
            )
        }

        mEventPopAdapter.submitList(newList)
    }

    fun show(view: View?) {
        showAsDropDown(view, (ScreenUtils.getAppScreenWidth() - width) / 2, 5.toDp.toInt())
    }
}