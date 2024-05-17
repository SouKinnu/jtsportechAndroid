package com.cloudhearing.android.lib_common.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: BenChen
 * Date: 2024/03/21 10:17
 * Email:chenxiaobin@cloudhearing.cn
 */
class GridSpaceItemDecoration(
    private val spanCount: Int,
    private val rowSpacing: Int,
    private val columnSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 获取view在adapter中的位置。
        val column = position % spanCount // view所在的列

        // 列间距
        outRect.left = if (column == 0) 0 else columnSpacing

        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=rowSpacing
//        if (position >= spanCount) {
//            outRect.top = rowSpacing // item top
//        }

        outRect.top = rowSpacing // item top
    }
}