package com.rocketpay.mandate.feature.image.data

import android.net.Uri
import com.rocketpay.mandate.feature.image.data.datasource.remote.ImageService
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedRequestDto
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedResponseDto
import com.rocketpay.mandate.feature.image.data.entities.ImageUploadRequestDto
import com.rocketpay.mandate.feature.image.domain.repositories.ImageRepository
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.Source
import okio.buffer
import okio.source
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

internal class ImageRepositoryImpl(private val imageService: ImageService): ImageRepository {

    override suspend fun genPreSignedUrl(superKeyId: String, imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto>{
        return imageService.genPreSignedUrl(superKeyId, imagePreSignedRequestDto)
    }

    override suspend fun genPreSignedUrl(imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto>{
        return imageService.genPreSignedUrl(imagePreSignedRequestDto)
    }
    override suspend fun uploadImage(superKeyId: String,
                                     imageUploadRequestDto: ImageUploadRequestDto
    )
    : Outcome<GenericSuccessResponse> {
        return imageService.uploadImage(superKeyId, imageUploadRequestDto)
    }

    override suspend fun deleteImage(superKeyId: String, documents: List<String>): Outcome<GenericSuccessResponse>{
        return imageService.deleteImage(superKeyId, documents)
    }

    override fun uploadToS3Bucket(uploadUrl: String, accessUrl: String, sourceImageUri: Uri): String? {
        try {
            val fileType = FileUtils.resolveFile(MandateManager.getInstance().getContext(), sourceImageUri)
            var mediaType = if(fileType == FileUtils.PDF){
                "application/pdf".toMediaTypeOrNull()
            }else{
                "image/jpeg".toMediaTypeOrNull()
            }
            val inputStream = MandateManager.getInstance().getContext().contentResolver.openInputStream(sourceImageUri)!!
            val requestBody = create(mediaType, inputStream)
            val countingRequestBody = CountingRequestBody(requestBody)
            val request = Request.Builder()
                .url(uploadUrl)
                .put(countingRequestBody)
                .build()
            val client: OkHttpClient = OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build()
            val response = client.newCall(request).execute()
            return if (response.isSuccessful) {
                accessUrl
            } else {
                null
            }
        } catch (e: FileNotFoundException) {
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private fun create(mediaType: MediaType?, inputStream: InputStream): RequestBody {

        return object : RequestBody() {

            override fun contentType(): MediaType? {
                return mediaType
            }

            override fun contentLength(): Long {
                return try {
                    inputStream.available().toLong()
                } catch (e: IOException) {
                    0
                }
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = inputStream.source()
                    sink.writeAll(source)
                } finally {
                    source?.close()
                }
            }
        }
    }

    inner class CountingRequestBody(
        private val delegate: RequestBody
    ) : RequestBody() {

        override fun contentType(): MediaType? {
            return delegate.contentType()
        }

        override fun contentLength(): Long {
            return try {
                delegate.contentLength()
            } catch (e: IOException) {
                -1
            }
        }

        override fun writeTo(sink: BufferedSink) {
            val countingSink = CountingSink(sink, contentLength())
            val bufferedSink = countingSink.buffer()
            delegate.writeTo(bufferedSink)
            bufferedSink.flush()
        }
    }

    inner class CountingSink(delegate: Sink, private val contentLength: Long) : ForwardingSink(delegate) {

        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
        }
    }


}