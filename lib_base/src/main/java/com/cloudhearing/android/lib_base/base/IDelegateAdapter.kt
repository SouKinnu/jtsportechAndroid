package com.cloudhearing.android.lib_base.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


interface IDelegateAdapter<T> {

    /**
     * 查找委托时调用的方法，返回自己能处理的类型即可
     *
     * @param t
     * @return
     */
    fun isForViewType(t: T): Boolean

    /**
     * 得到视图类型
     *
     * @return
     */
    fun hasViewType(): Int

    /**
     * 用于委托Adapter的onCreateViewHolder方法
     *
     * @param parent
     * @param viewType
     * @return
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder


    /**
     * 用于委托Adapter的onBindViewHolder方法
     *
     * @param holder
     * @param position
     * @param t
     */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, t: T)
}