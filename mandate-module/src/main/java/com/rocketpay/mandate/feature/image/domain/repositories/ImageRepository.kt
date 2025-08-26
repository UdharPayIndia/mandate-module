package com.rocketpay.mandate.feature.image.domain.repositories

import android.net.Uri
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedRequestDto
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedResponseDto
import com.rocketpay.mandate.feature.image.data.entities.ImageUploadRequestDto
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal interface ImageRepository {
    suspend fun genPreSignedUrl(superKeyId: String, imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto>
    suspend fun genPreSignedUrl(imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto>
    suspend fun uploadImage(superKeyId: String,
                            imageUploadRequestDto: ImageUploadRequestDto
    ): Outcome<GenericSuccessResponse>

    suspend fun deleteImage(superKeyId: String, documents: List<String>): Outcome<GenericSuccessResponse>
    fun uploadToS3Bucket(uploadUrl: String, accessUrl: String, sourceImageUri: Uri): String?
}