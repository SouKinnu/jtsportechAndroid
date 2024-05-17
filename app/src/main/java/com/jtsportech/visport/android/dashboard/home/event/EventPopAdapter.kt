package com.jtsportech.visport.android.dashboard.home.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.cloudhearing.android.lib_common.network.dataSource.home.competition.Competition
import com.jtsportech.visport.android.databinding.ItemPopEventBinding

/**
 * Author: BenChen
 * Date: 2024/03/22 19:11
 * Email:chenxiaobin@cloudhearing.cn
 */
class EventPopAdapter :
    BaseListAdapter<Competition, ItemPopEventBinding>(EventPopDiffCallback()) {

    private var mOnClickListener: ((Competition,Int) -> Unit)? = null

    fun setOnClickListener(listener: (Competition,Int) -> Unit) {
        mOnClickListener = listener
    }

    class EventPopDiffCallback : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return false
        }

    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup): ItemPopEventBinding {
        return ItemPopEventBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemPopEventBinding, item: Competition, position: Int) {
        val context = binding.root.context

        binding.apply {
            tvEventName.text = item.name

            root.setOnClickListener {
                mOnClickListener?.invoke(item,position)
            }
        }
    }
}