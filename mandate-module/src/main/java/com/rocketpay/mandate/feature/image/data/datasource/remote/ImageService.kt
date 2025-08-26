package com.rocketpay.mandate.feature.image.data.datasource.remote

import com.rocketpay.mandate.feature.image.data.entities.DeleteImageRequestDto
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedRequestDto
import com.rocketpay.mandate.feature.image.data.entities.ImagePreSignedResponseDto
import com.rocketpay.mandate.feature.image.data.entities.ImageUploadRequestDto
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.delete
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.put
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class ImageService {

    suspend fun genPreSignedUrl(superKeyId: String, imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto> {
        return NetworkRequestBuilder()
            .subUrl("super-key/v1/{id}/pre-signed-url")
            .pathParameter("id", superKeyId)
            .queryParameter("document_type", imagePreSignedRequestDto.documentType)
            .queryParameter("file_extension", imagePreSignedRequestDto.fileExtension)
            .queryParameter("product", "KEY")
            .queryParameter("size", "5")
            .body(imagePreSignedRequestDto)
            .build()
            .get()
    }

    suspend fun genPreSignedUrl(imagePreSignedRequestDto: ImagePreSignedRequestDto): Outcome<ImagePreSignedResponseDto> {
        return NetworkRequestBuilder()
            .subUrl("api/mas/documents/pre-signed-url")
            .queryParameter("document_type", imagePreSignedRequestDto.documentType)
            .queryParameter("file_extension", imagePreSignedRequestDto.fileExtension)
            .queryParameter("product", "KEY")
            .queryParameter("size", "5")
            .body(imagePreSignedRequestDto)
            .build()
            .get()
    }

    suspend fun uploadImage(superKeyId: String, imageUploadRequestDto: ImageUploadRequestDto): Outcome<GenericSuccessResponse> {
        return NetworkRequestBuilder()
            .subUrl("super-key/v1/{id}/documents")
            .pathParameter("id", superKeyId)
            .body(imageUploadRequestDto)
            .build()
            .put()
    }

    suspend fun deleteImage(superKeyId: String, documents: List<String>): Outcome<GenericSuccessResponse> {
        return NetworkRequestBuilder()
            .subUrl("super-key/v1/{id}/documents")
            .pathParameter("id", superKeyId)
            .body(DeleteImageRequestDto(documents))
            .build()
            .delete()
    }
}