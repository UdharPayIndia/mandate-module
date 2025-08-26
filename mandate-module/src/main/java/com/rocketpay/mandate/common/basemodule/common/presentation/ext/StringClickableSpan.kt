package com.rocketpay.mandate.common.basemodule.common.presentation.ext

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class StringClickableSpan(private val onClick:() -> Unit): ClickableSpan() {
    override fun onClick(v: View) {
        onClick()
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = true
        ds.color = ResourceManager.getInstance().getColor(R.color.rp_blue_2)
    }
}