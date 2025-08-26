package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import com.rocketpay.mandate.common.basemodule.common.presentation.validator.RegexValidator

internal class DataValidator {

    private val regexValidator = RegexValidator()

    companion object {

        const val countryCodeRegex = "^(\\+?\\d{1,3}|\\d{1,4})\$"

        // const val mobileNumberRegex = "^[+]{0,1}[0-9]{9,17}\$"
        const val mobileNumberRegex = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}$"
        const val otpRegex = "^[0-9]{6}\$"
        const val imeiRegex = "^[0-9]{15}\$"

        const val nameRegex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$"

        const val ifscRegex = "^[a-zA-Z]{4}[a-zA-Z0-9]{7}\$"
        const val accountNumberRegex = "^[a-zA-Z0-9]{9,18}\$"
        const val upiIdRegex = "^[\\w.-]*[@][\\w]*\$"
        const val panRegex = "^[A-Z]{5}[0-9]{4}[A-Z]{1}\$"

        const val amountMaxLength = 10
        const val noteMaxLength = 256
        const val nameMaxLength = 256
        const val nameMinLength = 3
        const val modelNameMinLength = 2

        const val minimumAmount = 15

    }

    fun isValidCountryCode(countryCode: String): Boolean {
        return if (countryCode.isEmpty()) {
            false
        } else {
            regexValidator(countryCodeRegex, countryCode)
        }
    }

    fun isValidMobileNumber(mobileNumber: String): Boolean {
        return if (mobileNumber.isEmpty()) {
            false
        } else {
            regexValidator(mobileNumberRegex, mobileNumber)
        }
    }

    fun isValidNumber(number: String): Boolean {
        return !number.isEmpty()
    }


    fun isValidImei(imei: String): Boolean {
        return if (imei.isEmpty()) {
            false
        } else {
            regexValidator(imeiRegex, imei)
        }
    }

    fun isValidOtp(otp: String): Boolean {
        return if (otp.isEmpty()) {
            false
        } else {
            regexValidator(otpRegex, otp)
        }
    }

    fun isValidName(name: String): Boolean {
        return name.trim().isNotEmpty() && nameMinLength <= name.trim().length && name.trim().length <= nameMaxLength
    }

    fun isValidModelName(name: String): Boolean {
        return name.trim().isNotEmpty() && modelNameMinLength <= name.trim().length && name.trim().length <= nameMaxLength
    }

    fun isValidString(string: String, regex: String): Boolean {
        return if (string.isEmpty()) {
            false
        } else {
            regexValidator(regex, string)
        }
    }

    fun isValidIfsc(ifsc: String): Boolean {
        return if (ifsc.isEmpty()) {
            false
        } else {
            regexValidator(ifscRegex, ifsc)
        }
    }

    fun isValidBankAccountNumber(accountNumber: String): Boolean {
        return if (accountNumber.isEmpty()) {
            false
        } else {
            regexValidator(accountNumberRegex, accountNumber)
        }
    }

    fun isValidUpiId(upiId: String): Boolean {
        return if (upiId.isEmpty()) {
            false
        } else {
            regexValidator(upiIdRegex, upiId)
        }
    }

    fun isValidAmount(amount: String): Boolean {
        return amount.isNotEmpty() && AmountUtils.stringToDouble(amount) > 0 && amount.length < amountMaxLength
    }

    fun isValidNotes(notes: String): Boolean {
        return notes.isNotEmpty() && notes.length < noteMaxLength
    }

    fun isValidPan(pan: String): Boolean {
        return if (pan.isEmpty()) {
            false
        } else {
            regexValidator(panRegex, pan)
        }
    }

    fun isValidInput(pan: String?, regex: String?): Boolean {
        return if (pan.isNullOrEmpty()) {
            false
        } else {
            if(!regex.isNullOrEmpty()) {
                regexValidator(regex, pan)
            }else{
                true
            }
        }
    }
}
