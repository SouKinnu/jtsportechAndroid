package com.jtsportech.visport.android.racedetail.video

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private var space: Int, private var itemNum: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
        if (itemNum == 2) {
            if (parent.getChildAdapterPosition(view) % itemNum == 0) {
                outRect.left = 0
            } else if (parent.getChildLayoutPosition(view) % itemNum == 1) {
                outRect.right = 0
            }
        }
    }

}