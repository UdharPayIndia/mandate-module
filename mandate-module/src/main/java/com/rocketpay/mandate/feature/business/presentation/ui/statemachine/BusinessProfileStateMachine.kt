package com.rocketpay.mandate.feature.business.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.business.data.BusinessPropertySyncer
import com.rocketpay.mandate.feature.business.domain.usecase.BusinessProfileUseCase
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import kotlinx.coroutines.CoroutineScope

internal class BusinessProfileStateMachine(
    private val businessProfileUseCase: BusinessProfileUseCase,
    private val propertyUseCase: PropertyUseCase
): SimpleStateMachineImpl<BusinessProfileEvent,
        BusinessProfileState, BusinessProfileASF, BusinessProfileUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): BusinessProfileState {
        return BusinessProfileState()
    }

    override fun handleEvent(
        event: BusinessProfileEvent,
        state: BusinessProfileState
    ): Next<BusinessProfileState?, BusinessProfileASF?, BusinessProfileUSF?> {
        return when (event) {
            is BusinessProfileEvent.LoadBusinessProfileData -> {
                next(
                    BusinessProfileASF.LoadBusinessProfile
                )
            }
            is BusinessProfileEvent.BusinessProfileDataLoaded -> {
                val newState = state.copy(businessFields = event.businessFields, businessProperties = event.businessProperties)
                next(
                    newState.copy(primaryActionEnable = true),
                    BusinessProfileUSF.UpdateFields(event.businessFields, event.businessProperties)
                )
            }
            is BusinessProfileEvent.SaveBusinessProfile -> {
                next(BusinessProfileASF.SaveBusinessProfile(state.businessProperties))
            }
            is BusinessProfileEvent.BusinessProfileSaved -> {
                next(BusinessProfileUSF.BusinessProfileAdded)
            }
            is BusinessProfileEvent.SkipBusinessProfile -> {
                next(BusinessProfileASF.SaveBusinessProfile(state.businessProperties))
            }
            is BusinessProfileEvent.DisablePrimaryAction -> {
                next(state.copy(primaryActionEnable = false))
            }
            is BusinessProfileEvent.BusinessFieldClick -> {
                val businessFieldType = state.businessProperties[event.businessField.type] ?: ""
                next(BusinessProfileUSF.OpenFieldValueSelection(event.businessField, businessFieldType))
            }
            is BusinessProfileEvent.BusinessFieldSelected -> {
                val businessProperties = state.businessProperties.toMutableMap()
                businessProperties[event.type] = event.value
                next(
                    state.copy(businessProperties = businessProperties, primaryActionEnable = true),
                    BusinessProfileUSF.UpdateBusinessProperties(businessProperties)
                )
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: BusinessProfileASF,
        dispatchEvent: (BusinessProfileEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is BusinessProfileASF.LoadBusinessProfile -> {
                val businessProfile = businessProfileUseCase.getBusinessProfileConfig()?.fields?.sortedBy { it.priority } ?: emptyList()
                val businessProperties = propertyUseCase.getProperties(PropertyType.Merchant)
                dispatchEvent(BusinessProfileEvent.BusinessProfileDataLoaded(businessProfile, businessProperties))
            }
            is BusinessProfileASF.SaveBusinessProfile -> {
                propertyUseCase.setProperties(sideEffect.properties, PropertyType.Merchant)
                SyncManager.getInstance().enqueue(BusinessPropertySyncer.TYPE)
                dispatchEvent(BusinessProfileEvent.BusinessProfileSaved)
            }
        }
    }

}
