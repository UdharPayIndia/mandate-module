package com.rocketpay.mandate.common.resourcemanager

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.util.*

internal class ResourceManager private constructor(
    private val context: Context,
    private val languageTranslator: LanguageTranslator?
) {

    companion object {
        private var instance: ResourceManager? = null
        fun getInstance(): ResourceManager {
            return instance ?: throw Exception("ResourceManager is not built, Please use Builder to build")
        }
    }

    class Builder {
        private lateinit var context: Context
        private var languageTranslator: LanguageTranslator? = null

        fun setContext(context: Context) = apply {
            this.context = context.applicationContext
        }

        fun setLanguageTranslator(languageTranslator: LanguageTranslator) = apply {
            this.languageTranslator = languageTranslator
        }

        fun build() {
            if (instance != null) {
                throw Exception("ResourceManager is already built, it can not be re built")
            }
            instance = ResourceManager(context, languageTranslator)
        }
    }

    fun getText(@StringRes resId: Int): CharSequence {
        return context.getText(resId)
    }

    fun getTextArray(@ArrayRes resId: Int): Array<CharSequence> {
        return context.resources.getTextArray(resId)
    }

    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return context.getString(resId, *formatArgs)
    }

    fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }

    fun getInteger(@IntegerRes resId: Int): Int {
        return context.resources.getInteger(resId)
    }

    fun getIntArray(@ArrayRes resId: Int): IntArray {
        return context.resources.getIntArray(resId)
    }

    fun getBoolean(@BoolRes resId: Int): Boolean {
        return context.resources.getBoolean(resId)
    }

    fun getDimension(@DimenRes resId: Int): Float {
        return context.resources.getDimension(resId)
    }

    fun getDimensionPixelSize(@DimenRes resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }

    fun getDimensionPixelOffset(@DimenRes resId: Int): Int {
        return context.resources.getDimensionPixelOffset(resId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable {
        val drawable = ResourcesCompat.getDrawable(context.resources, resId, null)
        if (drawable == null) {
            throw Resources.NotFoundException()
        } else {
            return drawable
        }
    }

    fun getDrawable(@DrawableRes resId: Int, @ColorInt tint: Int): Drawable {
        val drawable = DrawableCompat.wrap(getDrawable(resId))
        DrawableCompat.setTint(drawable, tint)
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

    @ColorInt
    fun getColor(@ColorRes resId: Int): Int {
        return ResourcesCompat.getColor(context.resources, resId, null)
    }

    fun getColorStateList(@ColorRes resId: Int): ColorStateList? {
        return ResourcesCompat.getColorStateList(context.resources, resId, null)
    }

    fun getFont(@FontRes id: Int): Typeface {
        val font = ResourcesCompat.getFont(context, id)
        if (font == null) {
            throw Resources.NotFoundException()
        } else {
            return font
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getString(@StringRes resId: Int, locale: Locale): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration).resources.getString(resId)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getString(@StringRes resId: Int, vararg formatArgs: Any, locale: Locale): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration).resources.getString(resId, *formatArgs)
    }

    fun getString(resId: String, defaultValue: String): String {
        return languageTranslator?.getString(resId, defaultValue) ?: defaultValue
    }

    fun getString(resId: String, vararg formatArgs: Any, defaultValue: String): String {
        return if (languageTranslator == null) {
            defaultValue
        } else {
            String.format(languageTranslator.getString(resId, defaultValue), formatArgs)
        }
    }

    fun getString(resId: String, defaultValue: String, locale: Locale): String {
        return languageTranslator?.getString(resId, defaultValue, locale) ?: defaultValue
    }

    fun getString(resId: String, vararg formatArgs: Any?, defaultValue: String, locale: Locale): String {
        return if (languageTranslator == null) {
            defaultValue
        } else {
            String.format(languageTranslator.getString(resId, defaultValue, locale), formatArgs)
        }
    }
}
