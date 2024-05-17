package com.jtsportech.visport.android.racedetail.comprehensivedata

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.EventData
import com.jtsportech.visport.android.databinding.ItemComprehensiveDataBinding


class ComprehensiveDataAdapter :
    BaseListAdapter<EventData, ItemComprehensiveDataBinding>(EventDataDiffCallback()) {
    interface OnItemClickListener {
        fun callBack(eventName: String)
    }

    private lateinit var onItemClickListener: OnItemClickListener
    fun setCallBack(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemComprehensiveDataBinding {
        return ItemComprehensiveDataBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemComprehensiveDataBinding, item: EventData, position: Int) {
        binding.eventName.text = item.eventName
        binding.team1Num.text = item.team1Count.toString()
        binding.team2Num.text = item.team2Count.toString()
        binding.team1Pro.max = item.team1Count + item.team2Count
        binding.team2Pro.max = item.team1Count + item.team2Count
        binding.team1Pro.progress = item.team1Count
        binding.team2Pro.progress = item.team2Count

        binding.root.setOnClickListener {
            if (::onItemClickListener.isInitialized) {
                onItemClickListener.callBack(item.eventName)
            }
        }
    }

    class EventDataDiffCallback : DiffUtil.ItemCallback<EventData>() {
        override fun areItemsTheSame(oldItem: EventData, newItem: EventData): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: EventData, newItem: EventData): Boolean {
            return false
        }
    }

}