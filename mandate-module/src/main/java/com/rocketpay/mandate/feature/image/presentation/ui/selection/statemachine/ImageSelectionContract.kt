package com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine

import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class ImageSelectionState (
    val title: String? = null,
    val subTitle: String? = null,
    val allowedExtensions: ArrayList<String>? = null,
    val maxSizeLimit: Int? = null,
    val permissionType: PermissionType? = null
) : BaseState(ImageSelectionScreen)


internal sealed class ImageSelectionEvent(name: String? = null) : BaseEvent(name) {
    data class Init(
        val title: String?,
        val subTitle: String?,
        val maxSizeLimit: Int?,
        val allowedExtension: ArrayList<String>?
    ) : ImageSelectionEvent()
    data object OpenCamera: ImageSelectionEvent()
    data object OpenPhoto: ImageSelectionEvent()
    data object OpenDocument: ImageSelectionEvent()
    object LoadData: ImageSelectionEvent()
    object SettingClick: ImageSelectionEvent()
}


internal sealed class ImageSelectionASF : AsyncSideEffect {
}


internal sealed class ImageSelectionUSF : UiSideEffect {
    data object CameraClick: ImageSelectionUSF()
    data object GalleryClick: ImageSelectionUSF()
    data object DocumentClick: ImageSelectionUSF()
    object OpenSetting: ImageSelectionUSF()
}

internal object ImageSelectionScreen : Screen("image_selection")
