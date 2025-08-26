package com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionEvent
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class DocumentSelectionMedia (val name: String, val icon: Drawable?, val event: ImageSelectionEvent){
    data object Camera: DocumentSelectionMedia(
        ResourceManager.getInstance().getString(R.string.rp_camera_2),
        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_post_camera), ImageSelectionEvent.OpenCamera)
    data object Photo: DocumentSelectionMedia(
        ResourceManager.getInstance().getString(R.string.rp_photo_album),
        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_image_24), ImageSelectionEvent.OpenPhoto)
    data object Document: DocumentSelectionMedia(
        ResourceManager.getInstance().getString(R.string.rp_document),
        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_document), ImageSelectionEvent.OpenDocument)


    companion object{
        fun getDocumentSelectionMedia(isCameraOnly: Boolean, isDocumentAllowed: Boolean): List<DocumentSelectionMedia>{
            return if(isCameraOnly){
                listOf(Camera)
            }else if(isDocumentAllowed){
                listOf(Camera, Photo, Document)
            }else{
                listOf(Camera, Photo)
            }
        }
    }
}