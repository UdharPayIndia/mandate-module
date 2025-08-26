package com.rocketpay.mandate.common.basemodule.common.presentation.databinding

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.rocketpay.mandate.R

internal object Binder {

    @JvmStatic
    @BindingAdapter(value = ["android:url", "android:text"], requireAll = true)
    fun loadImage(view: ImageView, url: String?, text: String?) {
        loadImage(view, url, text, true)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:url", "android:text", "app:circularCrop"], requireAll = true)
    fun loadImage(view: ImageView, url: String?, text: String?, circularCrop: Boolean?) {
        var drawable = if (url.isNullOrEmpty()) { null } else { view.drawable }
        if (drawable == null && !text.isNullOrEmpty()) {
            drawable = BitmapDrawable(view.resources, getBitmap(view.context.applicationContext, text))
            drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            view.setImageDrawable(drawable)
        }

        if (!text.isNullOrEmpty()) {
            if (circularCrop == true) {
                Glide.with(view.context.applicationContext).load(url).placeholder(drawable).apply(RequestOptions.circleCropTransform()).into(view)
            } else {
                Glide.with(view.context.applicationContext).load(url).placeholder(drawable).apply(RequestOptions.centerCropTransform()).into(view)
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:url"], requireAll = true)
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context.applicationContext).load(url).into(view)
    }

    private fun getBitmap(context: Context, name: String): Bitmap {
        val textDrawer = ImageHelper.getTextDrawer(name, 2, context)
        val size = context.resources.getDimension(R.dimen.rp_size_40).toInt()
        return ImageHelper.createCircleImageFromText(context, size, size, textDrawer)
    }

    @JvmStatic
    @BindingAdapter(value = ["android:url", "android:placeholder"], requireAll = true)
    fun loadImageFromPlaceholderAndUrl(view: ImageView, url: String?, drawable: Drawable?) {
        if (drawable == null) return
        view.setImageDrawable(drawable)

        if (url.isNullOrEmpty()) return
        Glide.with(view.context.applicationContext)
            .load(url)
            .placeholder(drawable)
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("app:errorText")
    fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
        view.error = errorMessage
        view.isErrorEnabled = !errorMessage.isNullOrEmpty()
    }

    @JvmStatic
    @BindingAdapter("app:textWithNullCheck")
    fun setText(view: TextView, text: String?) {
        view.text = text
        when {
            text == null -> view.visibility = View.GONE
            text.isEmpty() -> view.visibility = View.INVISIBLE
            else -> view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:textWithEmptyCheck")
    fun setNonEmptyText(view: TextView, text: String?) {
        view.text = text
        when {
            text.isNullOrEmpty() -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }


    @JvmStatic
    @BindingAdapter("app:textWithEmptyCheck")
    fun setNonEmptyText(view: TextView, text: SpannableString?) {
        view.text = text
        when {
            text.isNullOrEmpty() -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("app:textWithEmptyCheck")
    fun setNonEmptyText(view: ImageView, text: String?) {
        when {
            text.isNullOrEmpty() -> view.visibility = View.GONE
            else -> view.visibility = View.VISIBLE
        }
    }

    @BindingAdapter("textWithCursor")
    @JvmStatic
    fun setTextWithCursor(editText: EditText, string: CharSequence) {
        if (editText.text.toString() != string.toString()) {
            editText.setText(string)
            editText.setSelection(string.length)
        }
    }

    @JvmStatic
    @BindingAdapter("app:shakeAnimation")
    fun startAnimation(view: View, isVertical: Boolean) {
        val animShake = if (isVertical) {
            AnimationUtils.loadAnimation(view.context, R.anim.rp_up_down_wobble)
        } else {
            AnimationUtils.loadAnimation(view.context, R.anim.rp_shake_wobble)
        }
        animShake.repeatCount = Animation.INFINITE
        view.startAnimation(animShake)
    }

    @JvmStatic
    @BindingAdapter("android:onFocusChange")
    fun setOnFocusChangeListener(editText: EditText, onFocusChangeListener: View.OnFocusChangeListener) {
        editText.onFocusChangeListener = onFocusChangeListener
    }

    @JvmStatic
    @BindingAdapter("app:isBold")
    fun setBold(view: TextView, isBold: Boolean) {
        if (isBold) {
            view.setTypeface(null, Typeface.BOLD)
        } else {
            view.setTypeface(null, Typeface.NORMAL)
        }
    }

    @JvmStatic
    @BindingAdapter("app:customHeight")
    fun setCustomHeight(view: View, customHeight: Float) {
        if(customHeight > 0){
            val layoutParams = view.layoutParams
            layoutParams.height = customHeight.toInt()
            view.layoutParams = layoutParams
        }else{
            val layoutParams = view.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("app:bitmap")
    fun setImage(view: ImageView, bitmap: Bitmap?) {
        if (bitmap != null) {
            view.setImageBitmap(bitmap)
        }
    }

    @JvmStatic
    @BindingAdapter("app:customWidth")
    fun setCustomWidth(view: View, customWidth: Int) {
        val layoutParams = view.layoutParams
        layoutParams.width = customWidth
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("app:backgroundTint")
    fun setBackgroundTint(view: View, tint: Int) {
        view.setBackgroundColor(tint)
    }

    @JvmStatic
    @BindingAdapter("app:showShimmer")
    fun setShimmerVisibility(view: ShimmerFrameLayout, isVisible: Boolean) {
        if (isVisible) {
            view.startShimmer()
        } else {
            view.stopShimmer()
        }
    }

}
