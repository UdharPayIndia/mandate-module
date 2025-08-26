package com.rocketpay.mandate.common.basemodule.common.presentation.ext

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal fun Fragment.openWebPage(url: String) {
    val webPage: Uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webPage).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        startActivity(intent)
    } catch (e: Exception) {

    }
}

internal fun Fragment.showDialogFragment(tag: String, fragment: DialogFragment) {
    activity?.let {
        if (!it.isFinishing && this.isAdded && !this.isStateSaved) {
            val fragmentTransaction = childFragmentManager.beginTransaction()
            val prev = childFragmentManager.findFragmentByTag(tag)
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)
            fragment.show(childFragmentManager, tag)
        }
    }
}

internal fun Fragment.showDialogFragment(fragmentManager: FragmentManager, tag: String, fragment: DialogFragment) {
    activity?.let {
        if (!it.isFinishing && this.isAdded && !this.isStateSaved) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            val prev = fragmentManager.findFragmentByTag(tag)
            if (prev != null) {
                fragmentTransaction.remove(prev)
            }
            fragmentTransaction.addToBackStack(null)
            fragment.show(fragmentManager, tag)
        }
    }
}
