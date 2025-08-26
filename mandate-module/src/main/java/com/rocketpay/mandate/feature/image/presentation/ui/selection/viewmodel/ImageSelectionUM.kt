package com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionEvent
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionState
import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ImageSelectionUM(private val dispatchEvent: (ImageSelectionEvent) -> Unit) : BaseMainUM() {

    val permissionVisibility = ObservableBoolean()
    val permissionTitle = ObservableField<String>()

    val titleText = ObservableField<String>()
    val subTitleText = ObservableField<String>()

    fun handleState(state: ImageSelectionState) {
        titleText.set(if(!state.title.isNullOrEmpty()){
            state.title
        }else{
            ResourceManager.getInstance().getString(R.string.rp_select_photo_from)
        })
        subTitleText.set(state.subTitle)
        permissionTitle.set(if(state.permissionType == PermissionType.Camera){
            ResourceManager.getInstance().getString(R.string.rp_camera_permission_denied)
        }else{
            ResourceManager.getInstance().getString(R.string.rp_storage_permission_denied)
        })
    }

    fun onSettingClick(){
        permissionVisibility.set(false)
        dispatchEvent(ImageSelectionEvent.SettingClick)
    }
}
