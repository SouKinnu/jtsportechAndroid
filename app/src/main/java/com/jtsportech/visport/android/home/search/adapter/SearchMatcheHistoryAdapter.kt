package com.jtsportech.visport.android.home.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.BaseListAdapter
import com.jtsportech.visport.android.dataSource.home.search.SearchEntity
import com.jtsportech.visport.android.databinding.ItemSearchHistoryBinding

/**
 * Author: BenChen
 * Date: 2024/03/27 15:15
 * Email:chenxiaobin@cloudhearing.cn
 */
class SearchMatcheHistoryAdapter :
    BaseListAdapter<SearchEntity, ItemSearchHistoryBinding>(SearchMatcheHistoryDiffCallback()) {

    private var mOnSearchClickListener: ((String) -> Unit)? = null
    private var mOnDeleteClickListener: ((Int) -> Unit)? = null


    fun setOnSearchClickListener(listener: (String) -> Unit) {
        mOnSearchClickListener = listener
    }

    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        mOnDeleteClickListener = listener
    }

    class SearchMatcheHistoryDiffCallback : DiffUtil.ItemCallback<SearchEntity>() {
        override fun areItemsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean {
            return oldItem.content == newItem.content
        }

    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ItemSearchHistoryBinding {
        return ItemSearchHistoryBinding.inflate(inflater, parent, false)
    }

    override fun bind(binding: ItemSearchHistoryBinding, item: SearchEntity, position: Int) {
        val context = binding.root.context

        binding.apply {
            tvSearchName.text = item.content

            tvSearchName.setOnClickListener {
                mOnSearchClickListener?.invoke(item.content)
            }

            ibDelete.setOnClickListener {
                mOnDeleteClickListener?.invoke(position)
            }
        }
    }
}