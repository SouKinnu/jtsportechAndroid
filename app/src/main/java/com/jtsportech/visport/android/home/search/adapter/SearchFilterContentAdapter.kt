package com.jtsportech.visport.android.home.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.base.IDelegateAdapter
import com.jtsportech.visport.android.R
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterEntity
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterLayoutType
import com.jtsportech.visport.android.dataSource.home.search.SearchFilterTitleType

/**
 * Author: BenChen
 * Date: 2024/03/28 10:30
 * Email:chenxiaobin@cloudhearing.cn
 */
class SearchFilterContentAdapter : IDelegateAdapter<SearchFilterEntity> {

    private var mOnClickListener: ((SearchFilterEntity) -> Unit)? = null
    private var mOnMoreClickListener: ((SearchFilterEntity, View) -> Unit)? = null

    fun setOnClickListener(listener: (SearchFilterEntity) -> Unit) {
        mOnClickListener = listener
    }

    fun setOnMoreClickListener(listener: (SearchFilterEntity, View) -> Unit) {
        mOnMoreClickListener = listener
    }

    override fun isForViewType(t: SearchFilterEntity): Boolean {
        return t.type == SearchFilterLayoutType.CONTENT
    }

    override fun hasViewType(): Int {
        return SearchFilterLayoutType.CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_filter_content, parent, false)
        return SearchFilterContentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        t: SearchFilterEntity
    ) {
        val viewHolder = holder as SearchFilterContentViewHolder
        val context = viewHolder.itemView.context

        holder.mTvContent.text = t.subTitle
        holder.mTvContent.isSelected = t.isSelected ?: false
        holder.mIvBg.isSelected = t.isSelected ?: false
        holder.mIvDown.isVisible = t.isMore ?: false

        holder.itemView.setOnClickListener {
            if (t.titleType == SearchFilterTitleType.MATCH_TYPE && t.hasMoreView == true) {
                mOnMoreClickListener?.invoke(t, holder.itemView)
            } else {
                mOnClickListener?.invoke(t)
            }
        }
    }

    class SearchFilterContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvContent: AppCompatTextView = itemView.findViewById(R.id.tv_content)
        val mIvBg: AppCompatImageView = itemView.findViewById(R.id.iv_bg)
        val mIvDown: AppCompatImageView = itemView.findViewById(R.id.iv_down)
    }
}