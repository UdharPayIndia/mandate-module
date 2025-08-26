package com.rocketpay.mandate.feature.image.domain.entities

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.TaskExecutors
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.main.init.MandateManager
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

internal class SaveImageLocal {

    val RESIZED_IMAGE_SIZE = 1080

    fun saveImage(inputUri: Uri, onSave: (result: Uri?) -> Unit) {
        val tcs = TaskCompletionSource<Uri?>()
        Thread(
            Runnable {
                try {
                    val r = scaleAndSaveImage(inputUri)
                    Log.d("SaveImageLocal", "saveImage : $r")
                    tcs.setResult(r)
                } catch (e: Exception) {
                    tcs.setResult(null)
                }
            }
        ).start()
        tcs.task.continueWith<Uri>(
            TaskExecutors.MAIN_THREAD,
            Continuation { task ->
                onSave(task.result)
                null
            }
        )
    }

    @Throws(Exception::class)
    fun scaleAndSaveImage(inputUri: Uri): Uri {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val khataImagesFolder = FileUtils.getCachedPicsFolder()
        khataImagesFolder.mkdirs()
        val outFile = File(khataImagesFolder, fileName)
        scaleWithGlide(inputUri, outFile)
        val savedUri = FileUtils.getUriForFile(outFile)
        return savedUri
    }

    @Throws(Exception::class)
    private fun scaleWithGlide(inputUri: Uri, outFile: File): Boolean {
        val myOptions = RequestOptions()
            .fitCenter()
            .override(RESIZED_IMAGE_SIZE, RESIZED_IMAGE_SIZE)
        val tempScaledBitmap = Glide
            .with(MandateManager.getInstance().getContext())
            .asBitmap()
            .load(inputUri)
            .apply(myOptions)
            .submit()
            .get()


        val scaledBitmap = modifyOrientation(tempScaledBitmap, inputUri)
        val stream = FileOutputStream(outFile)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
        ImageUtils.copyMetadata(inputUri, outFile.absolutePath)
        return true
    }

    private fun modifyOrientation(bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = MandateManager.getInstance().getContext().contentResolver.openInputStream(uri)
        val ei = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            ExifInterface(inputStream!!)
        } else {
            ExifInterface(uri.path!!)
        }
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 270F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(bitmap, false, true)
            else -> bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
