package com.jtsportech.visport.android.home.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.base.IDelegateAdapter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterEntity
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterLayoutType

/**
 * Author: BenChen
 * Date: 2024/03/28 10:30
 * Email:chenxiaobin@cloudhearing.cn
 */
class SearchFilterTitleAdapter : IDelegateAdapter<SearchFilterEntity> {
    override fun isForViewType(t: SearchFilterEntity): Boolean {
        return t.type == SearchFilterLayoutType.TITLE
    }

    override fun hasViewType(): Int {
        return SearchFilterLayoutType.TITLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_filter_title, parent, false)
        return SearchFilterTitleViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        t: SearchFilterEntity
    ) {
        val viewHolder = holder as SearchFilterTitleViewHolder
        val context = viewHolder.itemView.context

        holder.mTvTitle.text = t.title
    }

    class SearchFilterTitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvTitle: AppCompatTextView = itemView.findViewById(R.id.tv_title)
    }
}