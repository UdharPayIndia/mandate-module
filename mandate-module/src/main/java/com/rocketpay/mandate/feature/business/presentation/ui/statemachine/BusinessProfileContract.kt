package com.rocketpay.mandate.feature.business.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.business.domain.entities.BusinessField

internal data class BusinessProfileState(
    val businessFields: List<BusinessField> = emptyList(),
    val businessProperties: Map<String, String?> = mapOf(),
    val primaryActionEnable: Boolean = false
) : BaseState(BusinessProfileScreen)


internal sealed class BusinessProfileEvent(name: String? = null) : BaseEvent(name) {
    data object LoadBusinessProfileData : BusinessProfileEvent()
    data class BusinessProfileDataLoaded(val businessFields: List<BusinessField>, val businessProperties: Map<String, String?>) : BusinessProfileEvent()
    object SaveBusinessProfile: BusinessProfileEvent("business_profile_save_click")
    object BusinessProfileSaved: BusinessProfileEvent("business_profile_saved")
    object SkipBusinessProfile: BusinessProfileEvent("business_profile_skipped")
    data class BusinessFieldClick(val businessField: BusinessField): BusinessProfileEvent("business_filed_click")
    data class BusinessFieldSelected(val type: String, val value: String): BusinessProfileEvent("business_filed_selected")
    object DisablePrimaryAction: BusinessProfileEvent()
}


internal sealed class BusinessProfileASF : AsyncSideEffect {
    object LoadBusinessProfile: BusinessProfileASF()
    data class SaveBusinessProfile(val properties: Map<String, String?>): BusinessProfileASF()
}


internal sealed class BusinessProfileUSF : UiSideEffect {
    data class UpdateFields(
        val businessFields: List<BusinessField>,
        val businessProperties: Map<String, String?>
    ) : BusinessProfileUSF()
    data class UpdateBusinessProperties(val businessProperties: Map<String, String?>) : BusinessProfileUSF()
    object BusinessProfileAdded : BusinessProfileUSF()
    data class OpenFieldValueSelection(val businessField: BusinessField, val businessFieldType: String): BusinessProfileUSF()
}

internal object BusinessProfileScreen : Screen("business_profile")
