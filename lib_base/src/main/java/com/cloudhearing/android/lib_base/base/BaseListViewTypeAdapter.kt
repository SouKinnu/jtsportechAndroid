package com.cloudhearing.android.lib_base.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Author: BenChen
 * Date: 2023/12/25 13:45
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseListViewTypeAdapter<T, VB : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, BaseListViewTypeAdapter.BaseViewHolder<VB>>(diffCallback) {
    protected abstract fun getItemViewType(position: Int, item: T): Int

    protected abstract fun onCreateBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): VB

    protected abstract fun onBind(binding: VB, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = onCreateBinding(inflater, parent, viewType)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = getItem(position)
        onBind(holder.binding, item, position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return getItemViewType(position, item)
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) :
        RecyclerView.ViewHolder(binding.root)

}