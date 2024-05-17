package com.cloudhearing.android.lib_base.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException


open class DelegateAdapter<T, S : DiffUtil.ItemCallback<T>>(diffCallback: S) :
    DiffUtilAdapter<T, S, RecyclerView.ViewHolder>(diffCallback) {

    private val delegateAdapters = arrayListOf<IDelegateAdapter<T>>()

    fun addDelegate(delegateAdapter: IDelegateAdapter<T>) {
        delegateAdapters.add(delegateAdapter)
    }

    fun getDelegate(): List<IDelegateAdapter<T>> {
        return delegateAdapters
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // 找到对应的委托Adapter
        val delegateAdapter = delegateAdapters[viewType]
        // 把onCreateViewHolder交给委托Adapter去处理
        return delegateAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindItemViewHolder(itemHolder: RecyclerView.ViewHolder, position: Int) {
        // 找到当前ViewHolder的ViewType，也就是委托Adapter在集合中的index
        val viewType = itemHolder.itemViewType
        // 找到对应的委托Adapter
        val delegateAdapter = delegateAdapters[viewType]
        // 把onBindViewHolder交给委托Adapter去处理
        delegateAdapter.onBindViewHolder(itemHolder, position, currentList[position])
    }

    override fun getItemViewType(position: Int): Int {
        // 找到当前位置的数据
        val delegateData = currentList[position]

        // 遍历所有的代理，问下他们谁能处理
        delegateAdapters.forEach { delegateAdapter ->
            if (delegateAdapter.isForViewType(delegateData)) {
                // 谁能处理返回他的index
                return delegateAdapters.indexOf(delegateAdapter)
            }
        }
        throw RuntimeException("没有找到可以处理的委托Adapter")
    }
}