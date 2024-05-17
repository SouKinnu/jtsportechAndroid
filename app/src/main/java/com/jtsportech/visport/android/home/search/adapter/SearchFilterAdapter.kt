package com.jtsportech.visport.android.home.search.adapter

import androidx.recyclerview.widget.DiffUtil
import com.cloudhearing.android.lib_base.base.DelegateAdapter
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterEntity

/**
 * Author: BenChen
 * Date: 2024/03/28 10:27
 * Email:chenxiaobin@cloudhearing.cn
 */
class SearchFilterAdapter :
    DelegateAdapter<SearchFilterEntity, SearchFilterAdapter.SearchFilterDiffCallback>(
        SearchFilterDiffCallback()
    ) {


    class SearchFilterDiffCallback : DiffUtil.ItemCallback<SearchFilterEntity>() {
        override fun areItemsTheSame(
            oldItem: SearchFilterEntity,
            newItem: SearchFilterEntity
        ): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(
            oldItem: SearchFilterEntity,
            newItem: SearchFilterEntity
        ): Boolean {
            return oldItem.isSelected == newItem.isSelected && oldItem.subTitle == newItem.subTitle
        }

    }
}