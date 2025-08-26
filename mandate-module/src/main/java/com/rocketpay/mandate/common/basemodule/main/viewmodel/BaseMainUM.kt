package com.rocketpay.mandate.common.basemodule.main.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel

internal abstract class BaseMainUM: ViewModel() {
    open val toolbarVisibility = ObservableInt(View.VISIBLE)
    open val toolbarIcon = ObservableField<Drawable>()
    open val titleTextColor = ObservableInt()
    open val toolbarTitleString = ObservableField<String>()
    open val toolbarBackground = ObservableField<Drawable>()
    open val toolbarSubtitleString = ObservableField<String>()
}
