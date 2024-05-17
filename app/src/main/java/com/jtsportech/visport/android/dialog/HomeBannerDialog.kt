package com.jtsportech.visport.android.dialog

import android.content.Context
import android.text.format.Time
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.cloudhearing.android.lib_common.network.dataSource.homebanner.EventsItem
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.DialogBannerBinding
import com.jtsportech.visport.android.dialog.base.BindingDialog


class HomeBannerDialog(context: Context) :
    BindingDialog<DialogBannerBinding>(
        context,
        inflate = DialogBannerBinding::inflate,
        width = 1f
    ) {
    private val radioButtonList: ArrayList<RadioButton> by lazy {
        ArrayList()
    }
    private val radioGroupLayoutParams: RadioGroup.LayoutParams by lazy {
        RadioGroup.LayoutParams(
            RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 8, 8, 8)
        }
    }

    private var mOnClickListener: ((EventsItem) -> Unit)? = null

    fun setOnClickListener(listener: (EventsItem) -> Unit) {
        mOnClickListener = listener
    }

    private val bannerAdapter: BannerAdapter by lazy {
        BannerAdapter().apply {
            setOnClickListener {
                mOnClickListener?.invoke(it)
            }
        }
    }

    init {
        binding.close.setOnClickListener {
            dismiss()
        }
    }

    fun loadData(eventsItemList: List<EventsItem>) {
        bannerAdapter.submitList(eventsItemList)
        for (e in eventsItemList) {
            val radioButton = radioButton()
            binding.RadioGroup.addView(radioButton, radioGroupLayoutParams)
            radioButtonList.add(radioButton)
        }
        if (eventsItemList.size > 1) radioButtonList[0].isChecked = true
        else binding.RadioGroup.visibility = View.GONE
        binding.ViewPager.adapter = bannerAdapter
        binding.ViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                for (i in 0 until radioButtonList.size) {
                    when (position) {
                        i -> radioButtonList[i].isChecked = true
                    }
                }
            }
        })
    }

    fun isToday(t: Long): Boolean {
        val time = Time("GTM+8")
        time.set(t)
        val thenYear = time.year
        val thenMonth = time.month
        val thenMonthDay = time.monthDay
        time.set(System.currentTimeMillis())
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay)
    }

    private fun radioButton(): RadioButton {
        return RadioButton(context).apply {
            setButtonDrawable(0)
            isFocusable = false
            background =
                ContextCompat.getDrawable(context, R.drawable.selector_dialog_banner)
            View.generateViewId()
        }
    }
}