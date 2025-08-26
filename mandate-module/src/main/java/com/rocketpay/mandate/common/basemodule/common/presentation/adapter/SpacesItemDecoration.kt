package com.rocketpay.mandate.common.basemodule.common.presentation.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class SpacesItemDecoration : RecyclerView.ItemDecoration {

    private var spaceHorizontal: Int = 0
    private var spaceTop: Int = 0
    private var spaceBottom: Int = 0

    constructor(space: Int): super(){
        this.spaceHorizontal = space
        this.spaceTop = space
        this.spaceBottom = space
    }

    constructor(spaceHorizontal: Int, spaceVertical: Int): super(){
        this.spaceHorizontal = spaceHorizontal
        this.spaceTop = spaceVertical
        this.spaceBottom = spaceVertical
    }

    constructor(spaceHorizontal: Int, spaceTop: Int, spaceBottom: Int): super(){
        this.spaceHorizontal = spaceHorizontal
        this.spaceTop = spaceTop
        this.spaceBottom = spaceBottom
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spaceHorizontal
        outRect.right = spaceHorizontal
        outRect.bottom = spaceBottom
        outRect.top = spaceTop

        //        // Add top margin only for the first item to avoid double space between items
        //        if (parent.getChildLayoutPosition(view) == 0) {
        //            outRect.top = space
        //        } else {
        //            outRect.top = 0
        //        }
    }

}