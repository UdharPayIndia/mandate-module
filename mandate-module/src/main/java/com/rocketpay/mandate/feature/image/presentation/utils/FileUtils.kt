package com.rocketpay.mandate.feature.image.presentation.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.main.init.MandateManager
import java.io.File
import java.util.Locale

internal object FileUtils {
    const val IMAGE = "image"
    const val PDF = "pdf"
    const val VIDEO = "video"

    fun getCachedPicsFolder(): File {
        return File(MandateManager.getInstance().getContext().getCacheDir(), Environment.DIRECTORY_PICTURES)
    }

    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(MandateManager.getInstance().getContext(),
            "${MandateManager.getInstance().getContext().packageName}.provider", file)
    }

    fun getPublicPicsFolder(): File? {
        return MandateManager.getInstance().getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }

    fun resolveFile(context: Context, uri: Uri?): String? {
        var fileType: String? = null
        if (uri != null) {
            fileType =
                if (context.contentResolver != null && context.contentResolver.getType(uri) != null) {
                    context.contentResolver.getType(uri)
                } else {
                    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                }
        }
        return if (fileType != null) {
            if (fileType.lowercase(Locale.getDefault())
                    .contains(PDF)
            ) {
                PDF
            } else if (fileType.lowercase(Locale.getDefault())
                    .contains(IMAGE)
            ) {
                IMAGE
            } else if (fileType.lowercase(Locale.getDefault())
                    .contains(VIDEO)
            ) {
                VIDEO
            } else {
                null
            }
        } else {
            null
        }
    }

}