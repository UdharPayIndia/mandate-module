package com.rocketpay.mandate.common.basemodule.common.presentation.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.core.graphics.createBitmap
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

object BitmapUtils {
    fun getBitmapFromView(view: View): Bitmap? {
        try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }catch (ex: Exception){
            return null
        }
    }

    fun getUriFromBitmap(context: Context, bitmap: Bitmap?, fileName: String, extension: String): Uri? {
        bitmap?.let {
            val sharedDirectory = FileUtils.getPublicPicsFolder()
            val file: File?
            if (sharedDirectory?.exists() == true || sharedDirectory?.mkdir() == true) {
                val fileOutputStream: FileOutputStream
                try {
                    file = File.createTempFile(fileName, extension, sharedDirectory)
                    fileOutputStream = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                    fileOutputStream.close()
                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                    return FileUtils.getUriForFile(file)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}