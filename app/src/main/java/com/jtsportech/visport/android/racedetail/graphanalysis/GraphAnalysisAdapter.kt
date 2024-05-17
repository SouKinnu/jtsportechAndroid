package com.jtsportech.visport.android.racedetail.graphanalysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.ChartDataX
import com.cloudhearing.android.lib_common.network.dataSource.racedetail.DataX
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.databinding.ItemComprehensiveDataBinding

class GraphAnalysisAdapter() :
    BaseListAdapter<DataX, ItemComprehensiveDataBinding>(EventDataDiffCallback()) {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemComprehensiveDataBinding {
        return ItemComprehensiveDataBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemComprehensiveDataBinding, item: DataX, position: Int) {
        binding.eventName.text = item.dataItem
        binding.team1Num.text = item.team1Value.toString() + "%"
        binding.team2Num.text = item.team2Value.toString() + "%"
        binding.team1Pro.max = 100
        binding.team2Pro.max = 100
        binding.team1Pro.progress = item.team1Value.toInt()
        binding.team2Pro.progress = item.team2Value.toInt()
    }

    class EventDataDiffCallback : DiffUtil.ItemCallback<DataX>() {
        override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean {
            return false
        }
    }
}