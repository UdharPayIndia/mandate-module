package com.rocketpay.mandate.common.basemodule.common.presentation.ext

import android.app.Dialog
import android.content.res.Resources
import android.view.ViewGroup

internal fun Dialog.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val percentWidth = dm.widthPixels * percent
    window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}

internal fun Dialog.setFullScreen() {
    window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
}
