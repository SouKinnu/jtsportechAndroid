package com.cloudhearing.android.lib_base.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Author: BenChen
 * Date: 2023/12/25 11:56
 * Email:chenxiaobin@cloudhearing.cn
 */
abstract class BaseBindingAdapter<T, VB : ViewBinding>(
    private var items: List<T>
) : RecyclerView.Adapter<BaseBindingAdapter.ViewHolder<VB>>() {

    abstract fun bind(binding: VB, item: T, position: Int)

    abstract fun inflateBinding(parent: ViewGroup): VB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        val binding = inflateBinding(parent)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        val item = items[position]
        bind(holder.binding, item, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getItems(): List<T> {
        return items
    }

    class ViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}