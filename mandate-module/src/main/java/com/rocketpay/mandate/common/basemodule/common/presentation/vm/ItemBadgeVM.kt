package com.rocketpay.mandate.common.basemodule.common.presentation.vm

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt

internal class ItemBadgeVM {
    val text = ObservableField<String>()
    val textColor = ObservableInt()
    val background = ObservableField<Drawable>()
}
