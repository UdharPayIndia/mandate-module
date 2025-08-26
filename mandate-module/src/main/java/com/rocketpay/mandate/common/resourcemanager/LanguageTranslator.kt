package com.rocketpay.mandate.common.resourcemanager

import java.util.*

internal interface LanguageTranslator {
    fun getString(resId: String, defaultValue: String): String
    fun getString(resId: String, defaultValue: String, locale: Locale): String
}
