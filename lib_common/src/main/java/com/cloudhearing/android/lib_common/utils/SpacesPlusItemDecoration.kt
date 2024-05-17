package com.cloudhearing.android.lib_common.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Author: BenChen
 * Date: 2021/12/8 10:43
 * Email:chenxiaobin@cloudhearing.cn
 */
class SpacesPlusItemDecoration(
    private val topSpace: Int = 0,
    private val leftSpace: Int = 0,
    private val rightSpace: Int = 0,
    private val bottomSpace: Int = 0,
    private val firstTop: Boolean = true
) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = leftSpace
        outRect.right = rightSpace
        outRect.bottom = bottomSpace

        // Add top margin only for the first item to avoid double space between items
        if (firstTop && parent.getChildPosition(view) == 0) outRect.top = topSpace
    }
}