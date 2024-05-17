package com.cloudhearing.android.lib_common.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: BenChen
 * Date: 2021/12/8 10:43
 * Email:chenxiaobin@cloudhearing.cn
 */
class SpacesItemDecoration(private val space: Int,private val firstTop: Boolean = true) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space

        // Add top margin only for the first item to avoid double space between items
        if (firstTop && parent.getChildPosition(view) == 0) outRect.top = space
    }
}