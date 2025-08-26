package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToLong

internal object AmountUtils {

    const val CURRENCY = "INR"
    const val CURRENCY_SYMBOL = "₹"
    const val NON_MONETISED_UPI_MAXIMUM_AMOUNT = 15000

    fun formatWithoutRupeeSymbol(amount: Double): String {
        return formatCurrencyAmount(amount)
    }

    fun format(amount: Double): String {
        return "$CURRENCY_SYMBOL${formatCurrencyAmount(amount)}"
    }

    fun format(amount: Double, showCurrency: Boolean): String {
        if(showCurrency){
            return "$CURRENCY_SYMBOL${formatCurrencyAmount(amount)}"
        }else{
            return formatCurrencyAmount(amount)
        }
    }

    fun format(amount: Double, locale: Locale): String {
        return "$CURRENCY_SYMBOL${formatCurrencyAmount(amount, locale)}"
    }

    fun formatCurrencyAmount(amount: Double): String {
        var amount = abs(amount)

        // round to 2 decimal places
        if (amount.isNaN()) {
            return 0.0.toString()
        }
        amount = (amount * 100.0).roundToLong() / 100.0


        val symbols = DecimalFormatSymbols(Locale.ENGLISH)

        val hundreds = DecimalFormat("###.##", symbols)
        val paddedHundreds = DecimalFormat("000.##", symbols)
        val thousandsNMore = DecimalFormat("##,##", symbols)
        val low = amount % 1000
        val high = floor(amount / 1000)
        return if (high == 0.0) {
            hundreds.format(low)
        } else {
            thousandsNMore.format(high) + "," + paddedHundreds.format(low)
        }
    }

    fun formatCurrencyAmount(amount: Double, locale: Locale): String {
        var amount = abs(amount)

        // round to 2 decimal places
        if (amount.isNaN()) {
            return 0.0.toString()
        }
        amount = (amount * 100.0).roundToLong() / 100.0


        val symbols = DecimalFormatSymbols(locale)

        val hundreds = DecimalFormat("###.##", symbols)
        val paddedHundreds = DecimalFormat("000.##", symbols)
        val thousandsNMore = DecimalFormat("##,##", symbols)
        val low = amount % 1000
        val high = floor(amount / 1000)
        return if (high == 0.0) {
            hundreds.format(low)
        } else {
            thousandsNMore.format(high) + "," + paddedHundreds.format(low)
        }
    }

    fun roundOffValue(amount: Double): Double {
        var amount = abs(amount)

        // round to 2 decimal places
        if (amount.isNaN()) {
            return 0.0
        }
        return (amount * 100.0).roundToLong() / 100.0
    }

    fun stringToDouble(amount: String): Double {
        return try {
            amount.toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun stringToInt(installment: String?): Int {
        return try {
            installment?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun stringToLong(installment: String?): Long {
        return try {
            installment?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
