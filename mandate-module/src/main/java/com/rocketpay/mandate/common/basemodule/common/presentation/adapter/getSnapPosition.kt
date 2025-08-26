package com.rocketpay.mandate.common.basemodule.common.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

internal fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

internal fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    onSnapPositionChangeListener: SnapOnScrollListener.OnSnapPositionChangeListener,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, false, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}