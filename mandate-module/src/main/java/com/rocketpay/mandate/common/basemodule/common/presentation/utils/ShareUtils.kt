package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.Fragment
import java.net.URLEncoder

internal object ShareUtils {

    private const val WHATSAPP_BUSINESS_PACKAGE_NAME = "com.whatsapp.w4b"
    private const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"

    fun sendWhatsApp(context: Context, message: String, mobileNumber: String): Boolean {
        val shareIntent = getIntentForShareOnWhatsApp(message, mobileNumber)
        return if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            true
        } else {
            sendSms(context, mobileNumber, message)
        }
    }

    fun sendSms(context: Context, mobileNumber: String, message: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("smsto:$mobileNumber")
            intent.putExtra("sms_body", message)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getIntentForShareOnWhatsApp(
        message: String,
        mobileNumber: String
    ): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$mobileNumber/?text=${URLEncoder.encode(message)}"))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return intent
    }


    fun sendWhatsApp(context: Context, message: String, contentUri: Uri?, mobileNumber: String?): Boolean {
        val packageName = getWhatsAppPackage(context)
        val shareIntent = getIntentForShare(context, message, contentUri)
        val finalShareIntent = if (packageName.isNullOrEmpty()) {
            shareIntent
        } else {
            if (mobileNumber != null) {
                shareIntent.putExtra("jid", "$mobileNumber@s.whatsapp.net")
            }
            shareIntent.setPackage(packageName)
        }

        return if (finalShareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"))
            true
        } else {
            false
        }
    }

    private fun getIntentForShare(
        context: Context,
        message: String,
        contentUri: Uri?
    ): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        if (contentUri != null) {
            shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        } else {
            shareIntent.setDataAndType(contentUri, "text/plain")
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        return shareIntent
    }

    private fun getWhatsAppPackage(context: Context): String? {
        val manager = context.packageManager
        val isWaInstalled: Boolean = isPackageInstalled(WHATSAPP_PACKAGE_NAME, manager)
        val isWaBusinessInstalled: Boolean = isPackageInstalled(WHATSAPP_BUSINESS_PACKAGE_NAME, manager)
        return if (isWaInstalled && isWaBusinessInstalled) {
            WHATSAPP_BUSINESS_PACKAGE_NAME
        } else if (isWaInstalled) {
            WHATSAPP_PACKAGE_NAME
        } else if (isWaBusinessInstalled) {
            WHATSAPP_BUSINESS_PACKAGE_NAME
        } else {
            null
        }
    }


    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("label", text)
        clipboard?.setPrimaryClip(clip)
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun goto(context: Context, action: String?, actionLink: String?): Boolean {
        return if (actionLink == null || action == null) {
            false
        } else {
            try {
                val intent = Intent(action, Uri.parse(actionLink))
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    fun gotoWithResult(fragment: Fragment, requestCode: Int, action: String?, actionLink: String?): Boolean {
        return if (actionLink == null || action == null) {
            false
        } else {
            try {
                val intent = Intent(action, Uri.parse(actionLink))
                fragment.startActivityForResult(intent, requestCode)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

}
