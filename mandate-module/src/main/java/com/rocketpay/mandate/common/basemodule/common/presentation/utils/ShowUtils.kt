package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.Context
import android.widget.Toast

internal object ShowUtils {

    fun shortToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun longToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

}