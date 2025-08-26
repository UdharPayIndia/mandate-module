package com.rocketpay.mandate.main.init.resourcemanager

import com.rocketpay.mandate.common.resourcemanager.LanguageTranslator
import java.util.*

internal class LanguageTranslatorImpl: LanguageTranslator {
    override fun getString(resId: String, defaultValue: String): String {
        return defaultValue
    }

    override fun getString(resId: String, defaultValue: String, locale: Locale): String {
        return defaultValue
    }
}
