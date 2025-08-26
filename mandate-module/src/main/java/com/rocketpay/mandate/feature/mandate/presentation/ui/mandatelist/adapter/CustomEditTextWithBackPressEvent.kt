package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

internal class CustomEditTextWithBackPressEvent(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    private var onBackPressListener: MyEditTextListener? = null

    public fun setOnBackPressListener(onBackPressListener: MyEditTextListener?) {
        this.onBackPressListener = onBackPressListener
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            // back button pressed
            if (Objects.requireNonNull(ViewCompat.getRootWindowInsets(rootView))!!.isVisible(WindowInsetsCompat.Type.ime())) {
                // keyboard is open
                onBackPressListener?.callback()
            }
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    interface MyEditTextListener {
        fun callback()
    }
}
