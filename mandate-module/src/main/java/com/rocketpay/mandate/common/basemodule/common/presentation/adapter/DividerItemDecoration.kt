package com.rocketpay.mandate.common.basemodule.common.presentation.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class DividerItemDecoration : RecyclerView.ItemDecoration {

    private var mDivider: Drawable? = null
    private var mShowFirstDivider = false
    private var mShowLastDivider = false
    private var dividerStartPosition: Int = 0
    private var extraPaddingLeft: Int = 0
    private var extraPaddingRight: Int = 0
    private val extraChildPaddingTop = SparseIntArray()
    private val extraChildPaddingBottom = SparseIntArray()

    constructor(divider: Drawable) {
        mDivider = divider
    }

    constructor(context: Context, attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.listDivider))
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    constructor(context: Context, attrs: AttributeSet, showFirstDivider: Boolean, showLastDivider: Boolean) : this(context, attrs) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    constructor(divider: Drawable, showFirstDivider: Boolean, showLastDivider: Boolean, extraPaddingLeft: Float, extraPaddingRight: Float, dividerStartPosition: Int) : this(divider) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
        this.extraPaddingLeft = extraPaddingLeft.toInt()
        this.extraPaddingRight = extraPaddingRight.toInt()
        this.dividerStartPosition = dividerStartPosition
    }

    /**
     * Set the position, the decorator should start drawing dividers. This method is useful, if for some reason few dividers are not required.
     *
     * @param dividerStartPosition position from which divider needs to be drawn
     */
    fun setDividerStartPosition(dividerStartPosition: Int) {
        this.dividerStartPosition = dividerStartPosition
    }

    /**
     * Add extra padding on the top of the given child. Provide the child number starting from 0 from top of the list.
     * Provide negative numbers to start from end. For example provide -1 for the last child.
     *
     * @param childIndex Index of the child
     * @param padding    padding top
     */
    fun setExtraChildPaddingTop(childIndex: Int, padding: Int) {
        extraChildPaddingTop.put(childIndex, padding)
    }

    /**
     * Add extra padding on the bottom of the given child. Provide the child number starting from 0 from top of the list.
     * Provide negative numbers to start from end. For example provide -1 for the last child.
     *
     * @param childIndex Index of the child
     * @param padding    padding bottom
     */
    fun setExtraChildPaddingBottom(childIndex: Int, padding: Int) {
        extraChildPaddingBottom.put(childIndex, padding)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mDivider == null) {
            return
        }
        if (parent.getChildLayoutPosition(view) < 1) {
            return
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = mDivider!!.intrinsicHeight
        } else {
            outRect.left = mDivider!!.intrinsicWidth
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mDivider == null) {
            super.onDrawOver(c, parent, state)
            return
        }

        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        val size: Int
        val orientation = getOrientation(parent)
        val childCount = parent.childCount

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = mDivider!!.intrinsicHeight
            left = parent.paddingLeft + this.extraPaddingLeft
            right = parent.width - parent.paddingRight - this.extraPaddingRight
        } else {
            size = mDivider!!.intrinsicWidth
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
        }

        for (i in (if (mShowFirstDivider) 0 else Math.max(1, dividerStartPosition)) until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.top - params.topMargin
                bottom = top + size
            } else {
                left = child.left - params.leftMargin
                right = left + size
            }
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }

        if (mShowLastDivider && childCount > 0) {
            val child = parent.getChildAt(childCount - 1)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.bottom + params.bottomMargin
                bottom = top + size
            } else {
                left = child.right + params.rightMargin
                right = left + size
            }
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }
    }

    private fun getOrientation(parent: RecyclerView): Int {
        if (parent.layoutManager is LinearLayoutManager) {
            val layoutManager = parent.layoutManager as LinearLayoutManager?
            return layoutManager!!.orientation
        } else {
            throw IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.")
        }
    }
}
