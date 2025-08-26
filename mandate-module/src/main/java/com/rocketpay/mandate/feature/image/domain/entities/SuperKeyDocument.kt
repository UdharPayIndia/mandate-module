package com.rocketpay.mandate.feature.image.domain.entities

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.image.data.entities.ImageUploadRequestDto
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class SuperKeyDocument(val value: String, val name: String, var url: String){
    object Photo: SuperKeyDocument("profile_picture", ResourceManager.getInstance().getString(R.string.rp_customer_photo), "")
    object AadharFront: SuperKeyDocument("aadhar_front", ResourceManager.getInstance().getString(R.string.rp_aadhar_card_front), "")
    object AadharBack: SuperKeyDocument("aadhar_back", ResourceManager.getInstance().getString(R.string.rp_aadhar_card_back), "")
    object Pan: SuperKeyDocument("pan", ResourceManager.getInstance().getString(R.string.rp_pan_card), "")
    object Signature: SuperKeyDocument("signature", ResourceManager.getInstance().getString(R.string.rp_customer_signature), "")

    companion object{
        fun getDocumentList(): List<SuperKeyDocument>{
            return listOf(Photo, Signature, AadharFront, AadharBack, Pan)
        }

        fun getUploadedDocument(imageUploadRequestDto: ImageUploadRequestDto?): List<String>{
            val arrayList = arrayListOf<String>()
            if(imageUploadRequestDto?.pan != null){
                arrayList.add(Pan.value)
            }
            if(imageUploadRequestDto?.aadhar_front != null){
                arrayList.add(AadharFront.value)
            }
            if(imageUploadRequestDto?.aadhar_back != null){
                arrayList.add(AadharBack.value)
            }
            if(imageUploadRequestDto?.profile_picture != null){
                arrayList.add(Photo.value)
            }
            if(imageUploadRequestDto?.signature != null){
                arrayList.add(Signature.value)
            }
            return arrayList
        }
    }
}