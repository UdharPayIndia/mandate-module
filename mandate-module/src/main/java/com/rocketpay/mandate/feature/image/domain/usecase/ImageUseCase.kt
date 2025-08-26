package com.rocketpay.mandate.feature.image.domain.usecase

import android.net.Uri
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedRequestDto
import com.rocketpay.mandate.feature.image.data.entities.ImageUploadRequestDto
import com.rocketpay.mandate.feature.image.domain.entities.SuperKeyDocument
import com.rocketpay.mandate.feature.image.domain.repositories.ImageRepository
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class ImageUseCase internal constructor(
    private val imageRepository: ImageRepository,
) {

    suspend fun uploadImage(superKeyId: String,
                            documentType: String,
                            fileSizeInMB: String,
                            imageUri: Uri,
    ): Outcome<String>{
        val fileType = FileUtils.resolveFile(MandateManager.getInstance().getContext(), imageUri)
        val fileExtension = if(fileType == FileUtils.PDF){
            "PDF"
        }else{
            "JPEG"
        }
        val request = ImagePreSignedRequestDto(
            documentType = documentType,
            size = fileSizeInMB,
            fileExtension = fileExtension
        )
        return when(val result = imageRepository.genPreSignedUrl(superKeyId, request)){
            is Outcome.Success -> {
                val uploadUrl = imageRepository.uploadToS3Bucket(result.data.uploadUrl, result.data.accessUrl, imageUri)
                if(!uploadUrl.isNullOrEmpty()) {
                    val imageUploadRequest = getImageUploadRequest(documentType, uploadUrl)
                    if (imageUploadRequest != null) {
                        uploadImage(superKeyId, uploadUrl, imageUploadRequest)
                    } else {
                        Outcome.Error(GenericErrorResponse(code = "400", message = "Document Type mismatch"))
                    }
                }else{
                    Outcome.Error(GenericErrorResponse(code = "400", message = "Upload to S3 Failed"))
                }
            }
            is Outcome.Error -> {
                result
            }
        }
    }

    suspend fun uploadImage(documentType: String,
                            fileSizeInMB: String,
                            imageUri: Uri,
    ): Outcome<String>{
        val fileType = FileUtils.resolveFile(MandateManager.getInstance().getContext(), imageUri)
        val fileExtension = if(fileType == FileUtils.PDF){
            "PDF"
        }else{
            "JPEG"
        }
        val request = ImagePreSignedRequestDto(documentType = documentType, size = fileSizeInMB,
            fileExtension = fileExtension)
        return when(val result = imageRepository.genPreSignedUrl(request)){
            is Outcome.Success -> {
                val uploadUrl = imageRepository.uploadToS3Bucket(result.data.uploadUrl, result.data.accessUrl, imageUri)
                if(!uploadUrl.isNullOrEmpty()) {
                    Outcome.Success(uploadUrl)
                }else{
                    Outcome.Error(GenericErrorResponse(code = "400", message = "Upload to S3 Failed"))
                }
            }
            is Outcome.Error -> {
                result
            }
        }
    }

    private suspend fun uploadImage(
        superKeyId: String,
        uploadUrl:String, imageUploadRequestDto: ImageUploadRequestDto
    ): Outcome<String> {
        return when(val result = imageRepository.uploadImage(superKeyId, imageUploadRequestDto)){
            is Outcome.Success ->{
                Outcome.Success(uploadUrl)
            }
            is Outcome.Error -> {
                result
            }
        }
    }

    suspend fun deleteImage(superKeyId: String, documents: List<String>): Outcome<GenericSuccessResponse>{
        return imageRepository.deleteImage(superKeyId, documents)
    }

    private fun getImageUploadRequest(documentType: String, url: String): ImageUploadRequestDto? {
        return when(documentType){
            SuperKeyDocument.Photo.value -> ImageUploadRequestDto(profile_picture = url)
            SuperKeyDocument.AadharBack.value -> ImageUploadRequestDto(aadhar_back = url)
            SuperKeyDocument.AadharFront.value -> ImageUploadRequestDto(aadhar_front = url)
            SuperKeyDocument.Pan.value -> ImageUploadRequestDto(pan = url)
            SuperKeyDocument.Signature.value -> ImageUploadRequestDto(signature = url)
            else -> { null }
        }
    }
}