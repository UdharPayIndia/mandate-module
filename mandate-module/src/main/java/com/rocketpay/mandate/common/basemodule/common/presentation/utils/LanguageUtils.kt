package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.res.Resources

internal object LanguageUtils {

    fun getDeviceLocale(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Resources.getSystem().configuration.locales[0].language
        }else{
            Resources.getSystem().configuration.locale.language
        }
    }

}