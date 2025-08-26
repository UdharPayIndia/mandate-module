package com.rocketpay.mandate.common.basemodule.common.presentation.ext

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.annotation.ColorInt
import java.security.MessageDigest
import java.util.Locale

internal val String.md5: String
    get() {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

internal val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

internal fun String.getSpannable(subString: String, @ColorInt color: Int, makeBold: Boolean): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val tempOriginalString = SpannableString(this)
    tempOriginalString.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    if (makeBold) {
        tempOriginalString.setSpan(
            StyleSpan(Typeface.BOLD),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return tempOriginalString
}

internal fun String.getSpannable(): SpannableString {
    return SpannableString(this)
}

internal fun SpannableString.setTextColor(subString: String, @ColorInt color: Int): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        ForegroundColorSpan(color),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}

internal fun SpannableString.makeBold(subString: String): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        StyleSpan(Typeface.BOLD),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}

internal fun SpannableString.strike(subString: String): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        StrikethroughSpan(),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}


internal fun SpannableString.underline(subString: String): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        UnderlineSpan(),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}

internal fun SpannableString.clickable(subString: String, onClick: ()-> Unit): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        StringClickableSpan(onClick),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}

internal fun SpannableString.setTextSize(subString: String, proportion: Float) : SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(RelativeSizeSpan(proportion),startIndex,endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    return originalString
}


internal fun SpannableString.setTextColorWithBackground(subString: String, @ColorInt textColor: Int, @ColorInt backgroundColor: Int, cornerRadius: Float): SpannableString {
    val startIndex = this.indexOf(subString)
    val endIndex = startIndex + subString.length

    val originalString = SpannableString(this)
    originalString.setSpan(
        RoundedBackgroundSpan(textColor, backgroundColor, cornerRadius),
        startIndex,
        endIndex,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return originalString
}

internal fun String?.getFirstName(): String{
    return this?.split(" ").takeIf { !it.isNullOrEmpty() }?.get(0) ?: ""
}

internal fun String?.ifNullOrEmpty(default: String): String = if(this.isNullOrBlank() || this == "null"){ default } else{ this }

internal fun String?.toCapitalise() = if(this.isNullOrBlank()){ "" }else{ replace("_", " ").lowercase().replaceFirstChar{ if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() } }

internal fun String?.long(): Long = this?.takeIf { it.isNotEmpty() }?.toLong() ?: 0L

internal fun String?.int(): Int = this?.takeIf { it.isNotEmpty() }?.toInt() ?: 0

internal fun String?.double(): Double = this?.takeIf { it.isNotEmpty() }?.toDouble() ?: 0.0

internal fun Double?.double(): Double = this?.toDouble() ?: 0.0

internal fun Long?.long(): Long = this?.toLong() ?: 0L

internal fun Int?.int(): Int = this?.toInt() ?: 0

internal fun ArrayList<String>.removeIfMatchesString(id: String?): ArrayList<String> {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (item == id) {
            iterator.remove()
        }
    }
    return this
}
