package com.rocketpay.mandate.common.basemodule.common.presentation.validator

import java.util.regex.Matcher
import java.util.regex.Pattern

internal class RegexValidator {

    operator fun invoke(regex: String, content: String): Boolean {
        val pattern = Pattern.compile(regex)
        val matcher: Matcher = pattern.matcher(content)
        return matcher.matches()
    }
}
