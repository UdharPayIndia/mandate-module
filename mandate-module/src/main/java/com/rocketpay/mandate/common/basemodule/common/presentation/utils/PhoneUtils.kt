package com.rocketpay.mandate.common.basemodule.common.presentation.utils

internal object PhoneUtils {

    val INDIA_COUNTRY_CODE = "+91"
    val INDIA_COUNTRY_CODE_WITHOUT_PLUS = "0"

    fun purifiedIndianMobileNumber(mobileNumber: String): String {
        val filteredMobileNumber = mobileNumber.replace("-","").replace(" ","")
        if (filteredMobileNumber.length > 10) {
            val mobileNumberWithCountryCode = filteredMobileNumber
                .removePrefix(INDIA_COUNTRY_CODE)
                .removePrefix(INDIA_COUNTRY_CODE_WITHOUT_PLUS)
            return "$INDIA_COUNTRY_CODE$mobileNumberWithCountryCode"
        }
        return "$INDIA_COUNTRY_CODE$filteredMobileNumber"
    }

    fun purifyMobileNumberWithoutCountryCode(mobileNumber: String): String {
        val mobileNumberWithoutDash = mobileNumber.replace("-","").replace(" ","")
        return "$INDIA_COUNTRY_CODE${mobileNumberWithoutDash.substring(mobileNumberWithoutDash.count() - 10, mobileNumberWithoutDash.count())}"
    }

    fun removedCountryCodeFromMobileNumber(number: String): String {
        for (country in Countries().getCountryList()) {
            val countryCode = country.code
            if (number.startsWith(countryCode)) {
                return number.substring(countryCode.length)
            }
        }
        return number
    }
}
