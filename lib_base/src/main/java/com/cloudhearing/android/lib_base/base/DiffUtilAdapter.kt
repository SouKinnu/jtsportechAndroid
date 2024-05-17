package com.cloudhearing.android.lib_base.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloudhearing.android.lib_base.utils.context.AppProvider


abstract class DiffUtilAdapter<T, S : DiffUtil.ItemCallback<T>, ItemHolder : RecyclerView.ViewHolder>(
    diffCallback: S
) :
    ListAdapter<T, ItemHolder>(diffCallback) {

    protected var inflater: LayoutInflater =
        AppProvider.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return onCreateItemViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        onBindItemViewHolder(holder, position)
    }

    /**
     * 解决地址相同不刷新问题
     *
     * @param list
     */
    override fun submitList(list: MutableList<T>?) {
        super.submitList(if (list != null) ArrayList(list) else null)
    }

    /**
     * 创建 view
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): ItemHolder

    /**
     * 给view中的控件设置数据
     *
     * @param itemHolder
     * @param position
     */
    protected abstract fun onBindItemViewHolder(itemHolder: ItemHolder, position: Int)

}