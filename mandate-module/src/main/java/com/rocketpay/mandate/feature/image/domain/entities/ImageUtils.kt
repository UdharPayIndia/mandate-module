package com.rocketpay.mandate.feature.image.domain.entities

import android.media.ExifInterface
import android.net.Uri
import com.bumptech.glide.Glide
import com.rocketpay.mandate.main.init.MandateManager

internal object ImageUtils {

    fun copyMetadata(inUri: Uri?, outFile: String?) {
        try {
            val file = Glide.with(MandateManager.getInstance().getContext()).downloadOnly().load(inUri).submit().get()
            val oldExif = ExifInterface(file.path)
            val attributes = arrayOf(
                ExifInterface.TAG_APERTURE,
                ExifInterface.TAG_DATETIME,
                ExifInterface.TAG_EXPOSURE_TIME,
                ExifInterface.TAG_FLASH,
                ExifInterface.TAG_FOCAL_LENGTH,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_IMAGE_LENGTH,
                ExifInterface.TAG_IMAGE_WIDTH,
                ExifInterface.TAG_ISO,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.TAG_WHITE_BALANCE
            )
            val newExif = ExifInterface(outFile!!)
            for (attribute in attributes) {
                val value = oldExif.getAttribute(attribute)
                if (value != null) newExif.setAttribute(attribute, value)
            }
            newExif.saveAttributes()
        } catch (e: Exception) {
        }
    }
}
