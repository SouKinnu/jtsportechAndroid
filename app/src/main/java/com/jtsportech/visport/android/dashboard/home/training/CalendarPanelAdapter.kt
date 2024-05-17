package com.jtsportech.visport.android.dashboard.home.training

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_base.base.BaseListPlusAdapter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.HomeTabEntity
import com.jtsportech.visport.android.dataSource.home.event.CalendarPanelEntity
import com.jtsportech.visport.android.databinding.ItemCalendarBinding
import timber.log.Timber

/**
 * Author: BenChen
 * Date: 2024/01/12 15:08
 * Email:chenxiaobin@cloudhearing.cn
 */
class CalendarPanelAdapter :
    BaseListPlusAdapter<List<CalendarPanelEntity>, ItemCalendarBinding>(CalendarPanelDiffCallback()) {

    private var mOnClickListener: ((Int, Int) -> Unit)? = null

    fun setOnClickListener(listener: (Int, Int) -> Unit) {
        mOnClickListener = listener
    }

    class CalendarPanelDiffCallback : DiffUtil.ItemCallback<List<CalendarPanelEntity>>() {
        override fun areItemsTheSame(
            oldItem: List<CalendarPanelEntity>,
            newItem: List<CalendarPanelEntity>
        ): Boolean {
            return oldItem.size == newItem.size
        }

        override fun areContentsTheSame(
            oldItem: List<CalendarPanelEntity>,
            newItem: List<CalendarPanelEntity>
        ): Boolean {
            oldItem.forEachIndexed { index, entity ->
                if (entity.selected != newItem[index].selected || entity.bottomTime != newItem[index].bottomTime) {
                    return false
                }
            }

            return true
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemCalendarBinding {
        return ItemCalendarBinding.inflate(inflater, parent, false)
    }

    override fun bind(
        holder: BaseViewHolder<ItemCalendarBinding>,
        binding: ItemCalendarBinding,
        item: List<CalendarPanelEntity>,
        position: Int
    ) {
        val context = binding.root.context

        Timber.d("有走3 ${holder.layoutPosition}")

        // 周日
        binding.tvDateSun1.text = item[0].topTime
        binding.tvDateSun2.text = item[0].bottomTime
        binding.ivDateSun.setImageDrawable(
            if (item[0].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlSun.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 0)
        }

        // 周一
        binding.tvDateMon1.text = item[1].topTime
        binding.tvDateMon2.text = item[1].bottomTime
        binding.ivDateMon.setImageDrawable(
            if (item[1].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlMon.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 1)
        }

        // 周二
        binding.tvDateTue1.text = item[2].topTime
        binding.tvDateTue2.text = item[2].bottomTime
        binding.ivDateTue.setImageDrawable(
            if (item[2].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlTue.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 2)
        }

        // 周三
        binding.tvDateWed1.text = item[3].topTime
        binding.tvDateWed2.text = item[3].bottomTime
        binding.ivDateWed.setImageDrawable(
            if (item[3].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlWed.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 3)
        }

        // 周四
        binding.tvDateThu1.text = item[4].topTime
        binding.tvDateThu2.text = item[4].bottomTime
        binding.ivDateThu.setImageDrawable(
            if (item[4].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlThu.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 4)
        }

        // 周五
        binding.tvDateFri1.text = item[5].topTime
        binding.tvDateFri2.text = item[5].bottomTime
        binding.ivDateFri.setImageDrawable(
            if (item[5].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlFri.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 5)
        }

        // 周六
        binding.tvDateSat1.text = item[6].topTime
        binding.tvDateSat2.text = item[6].bottomTime
        binding.ivDateSat.setImageDrawable(
            if (item[6].selected) ContextCompat.getDrawable(context, R.drawable.bg_date_orange)
            else ContextCompat.getDrawable(context, R.drawable.bg_date_blue)
        )
        binding.rlSat.setOnClickListener {
            mOnClickListener?.invoke(holder.layoutPosition, 6)
        }


    }
}