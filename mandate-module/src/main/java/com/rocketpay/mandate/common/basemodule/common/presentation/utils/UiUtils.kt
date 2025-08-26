package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.app.Activity
import androidx.fragment.app.Fragment

internal object UiUtils {

    fun isActivityInCorrectState(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    fun isFragmentStateCorrect(fragment: Fragment?): Boolean {
        return fragment != null && fragment.context != null && fragment.isAdded && fragment.view != null && !fragment.isStateSaved && isActivityInCorrectState(fragment.activity)
    }

    fun maskText(text: String?): String {
        return if (!text.isNullOrEmpty()) {
            when {
                text.length > 5 -> {
                    text.replaceRange(0, text.length-4, "XXXXXX")
                }
                else -> {
                    text
                }
            }
        } else {
            ""
        }
    }

}
