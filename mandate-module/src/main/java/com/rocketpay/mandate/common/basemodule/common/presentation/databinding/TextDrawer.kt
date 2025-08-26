package com.rocketpay.mandate.common.basemodule.common.presentation.databinding

import android.graphics.Color

internal class TextDrawer {
    private var text: String = ""
    private var backgroundColor: Int = 0
    private var textColor: Int = 0

    init {
        backgroundColor = Color.BLUE
        textColor = Color.WHITE
    }

    // Setters
    fun setText(text: String): TextDrawer {
        this.text = text
        return this
    }

    fun setBackgroundColor(color: Int): TextDrawer {
        this.backgroundColor = color
        return this
    }

    fun setTextColor(color: Int): TextDrawer {
        this.textColor = color
        return this
    }

    // Getters
    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    fun getTextColor(): Int {
        return textColor
    }

    fun getText(): String {
        return text
    }
}
