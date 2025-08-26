package com.rocketpay.mandate.common.basemodule.common.presentation.video

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import java.lang.Exception

internal object YoutubeHelper {

    private const val youTubeUrlPrefix = "https://www.youtube.com/watch?v="

    fun openVideo(context: Context, uri: Uri) {
        try {
            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${getVideoIdFromUrl(uri.toString())}"))
            appIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, uri)
                webIntent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(webIntent)
            }catch (ex: Exception){

            }
        }
    }

    private fun getVideoIdFromUrl(videoUrl: String): String {
        return videoUrl.removePrefix(youTubeUrlPrefix)
    }

}
