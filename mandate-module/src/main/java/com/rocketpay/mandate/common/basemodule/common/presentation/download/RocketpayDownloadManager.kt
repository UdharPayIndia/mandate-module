package com.rocketpay.mandate.common.basemodule.common.presentation.download

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.main.init.MandateManager
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.random.Random

internal object RocketpayDownloadManager {

    private var DOWNLOADS_CHANNEL_ID = "downloads"
    private val notificationId: Int = Random.nextInt(100000, 100000000)
    private var targetUri: Uri? = null

    fun download(fileUrl: String, destinationFile: File, fileName: String, shouldShowProgressUpdateMsg: Boolean): Uri? {
        if (shouldShowProgressUpdateMsg) {
            showNotification(fileName, ResourceManager.getInstance().getString(
                R.string.rp_downloading_report), null)
        }
        try {
            val url = URL(fileUrl)
            val connection = url.openConnection()
            connection.connect()

            // input stream to read file - with 8k buffer
            val input = BufferedInputStream(url.openStream(), 8192)

            // Output stream to write file
            val output = FileOutputStream(destinationFile)

            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int

            while (true) {
                count = input.read(data)
                if (count == -1) {
                    break
                }
                total += count.toLong()

                // writing data to file
                output.write(data, 0, count)
            }

            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()

            targetUri = FileUtils.getUriForFile(destinationFile)
            if (shouldShowProgressUpdateMsg) {
                showNotification(fileName,
                    ResourceManager.getInstance().getString(R.string.rp_download_complete), targetUri)
            }
        } catch (e: Exception) {
            targetUri = null
            if (shouldShowProgressUpdateMsg) {
                showNotification(fileName,
                    ResourceManager.getInstance().getString(R.string.rp_download_failed), null)
            }
            e.printStackTrace()
        }
        return targetUri
    }

    fun showNotification(title: String, text: String, targetUri: Uri?) {
        try {
            val context = MandateManager.getInstance().getContext()
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
            val notificationManager = ensureChannel()

            // Create intent to view the file
            val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(targetUri, "text/csv") // change MIME type as needed
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Wrap in PendingIntent
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openFileIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(
                MandateManager.getInstance().getContext(),
                DOWNLOADS_CHANNEL_ID
            )
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setChannelId(DOWNLOADS_CHANNEL_ID)
            if (MandateManager.getInstance().getAppIcon() != -1) {
                notificationBuilder.setSmallIcon(MandateManager.getInstance().getAppIcon())
            }else{
                notificationBuilder.setSmallIcon(R.drawable.rp_ic_download_report)
            }

            if (targetUri != null) {
                val viewIntent = Intent(Intent.ACTION_VIEW, targetUri)
                viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val pendingIntent = PendingIntent.getActivity(
                    MandateManager.getInstance().getContext(),
                    0, viewIntent, PendingIntent.FLAG_IMMUTABLE
                )
                notificationBuilder.setContentIntent(pendingIntent)
            }

            notificationBuilder.priority = Notification.PRIORITY_MIN
            notificationManager.notify(notificationId, notificationBuilder.build())
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }

    private fun ensureChannel(): NotificationManager {
        val notificationManager = MandateManager.getInstance().getContext()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(DOWNLOADS_CHANNEL_ID, "Downloads", importance)
            mChannel.setSound(null, null)
            notificationManager.createNotificationChannel(mChannel)
        }
        return notificationManager
    }
}
