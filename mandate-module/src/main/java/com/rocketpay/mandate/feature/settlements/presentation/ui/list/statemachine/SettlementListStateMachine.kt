package com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine

import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import com.rocketpay.mandate.main.GlobalState
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.product.presentation.ui.utils.ProductUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

internal class SettlementListStateMachine(
    private val paymentOrderUseCase: PaymentOrderUseCase,
    private val propertyUseCase: PropertyUseCase
) : SimpleStateMachineImpl<SettlementListEvent, SettlementListState, SettlementListASF, SettlementListUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): SettlementListState {
        return SettlementListState()
    }

    override fun handleEvent(
        event: SettlementListEvent,
        state: SettlementListState
    ): Next<SettlementListState?, SettlementListASF?, SettlementListUSF?> {
        return when (event) {
            is SettlementListEvent.Init -> {
                next(SettlementListASF.Init)
            }
            is SettlementListEvent.LoadOutstandingSettlementBalance -> {
                next(SettlementListASF.LoadOutstandingSettlementBalance)
            }
            is SettlementListEvent.SetOutstandingSettlementBalance -> {
                next(state.copy(outstandingSettlementBalance = event.value))
            }
            is SettlementListEvent.LoadSettlements -> {
                val lastFetchedTimeStamp = getFetchedTime(event.orderByDesc)
                next(
                    state.copy(
                        orderByDesc = event.orderByDesc,
                        lastFetchedTimeStamp = lastFetchedTimeStamp,
                        isLoading = true
                    ),
                    SettlementListASF.LoadSettlements(
                        lastFetchedTimeStamp,
                        state.limit,
                        event.orderByDesc
                    ))
            }
            is SettlementListEvent.LoadSettlementBannerInfo -> {
                next(SettlementListASF.LoadSettlementBannerInfo)
            }
            is SettlementListEvent.SettlementLoaded -> {
                val settlements = state.paymentOrders
                if(!event.paymentOrders.isNullOrEmpty()){
                    state.paymentOrders.addAll(event.paymentOrders)
                    val isLastPage = event.paymentOrders.size < state.limit
                    next(
                        state.copy(
                            isLoading = false,
                            isLastPage = isLastPage,
                            lastFetchedTimeStamp = event.paymentOrders.last().createdAt,
                            paymentOrders = settlements,
                        ),
                        SettlementListUSF.UpdateSettlements(settlements, isLastPage)

                    )
                }else{
                    next(state.copy(
                        isLoading = false,
                        isLastPage = true,
                    ),
                        SettlementListUSF.UpdateSettlements(settlements, true))
                }

            }
            is SettlementListEvent.SettlementClick -> {
                next(
                    SettlementListUSF.OpenSettlementDetails(
                    event.paymentOrder.id
                    )
                )
            }
            is SettlementListEvent.FetchNextSettlements -> {
                next(
                    state.copy(
                        isLoading = true,
                    ),
                    SettlementListASF.LoadSettlements(
                        state.lastFetchedTimeStamp,
                        state.limit,
                        state.orderByDesc)
                )
            }
            is SettlementListEvent.RefreshClick -> {
                next(state.copy(isRefreshing = true), SettlementListASF.RefreshData)
            }
            is SettlementListEvent.UpdateDataRefreshed -> {
                if(event.isRefreshing != state.isRefreshing){
                    next(state.copy(isRefreshing = event.isRefreshing,
                        isLoading = false,
                        isLastPage = false,
                        lastFetchedTimeStamp = getFetchedTime(state.orderByDesc),
                        paymentOrders = arrayListOf()),
                        SettlementListASF.LoadSettlements(
                            getFetchedTime(state.orderByDesc),
                            state.limit,
                            state.orderByDesc
                        ),
                        SettlementListUSF.UpdateSettlements(emptyList(), false)
                    )
                }else{
                    next(state.copy(isRefreshing = event.isRefreshing))
                }
            }
            is SettlementListEvent.UpdateBannerMessage -> {
                next(state.copy(bannerMessage = event.message))
            }
            is SettlementListEvent.SettlementBannerClick -> {
                next(SettlementListUSF.OpenKyc)
            }
            is SettlementListEvent.SettlementAccountBannerClick -> {
                next(SettlementListUSF.OpenBankAccount)
            }
        }
    }

    private fun getFetchedTime(orderByDesc: Boolean): Long{
        return if(orderByDesc){
            Long.MAX_VALUE
        }else{
            0L
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: SettlementListASF,
        dispatchEvent: (SettlementListEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is SettlementListASF.Init -> {
                GlobalState.isSettlementRefreshing.collectIn(viewModelScope){
                    dispatchEvent(SettlementListEvent.UpdateDataRefreshed(it))
                }
            }
            is SettlementListASF.LoadOutstandingSettlementBalance -> {
                propertyUseCase.getPropertyLive(ProductUtils.OUTSTANDING_SETTLEMENT_BALANCE).collectIn(viewModelScope) {
                    dispatchEvent(
                        SettlementListEvent.SetOutstandingSettlementBalance(
                            it?.value.double()
                        )
                    )
                }
            }
            is SettlementListASF.LoadSettlementBannerInfo -> {
                paymentOrderUseCase.fetchSettlementBannerInfo(propertyUseCase)
                propertyUseCase.getPropertyLive(ProductUtils.SETTLEMENT_BANNER_MESSAGE).collectIn(viewModelScope){
                    dispatchEvent(SettlementListEvent.UpdateBannerMessage(it?.value.orEmpty()))
                }
            }
            is SettlementListASF.LoadSettlements -> {
                val settlements = paymentOrderUseCase.getPaginatedSettlements(
                    sideEffect.lastFetchedTimeStamp,
                    sideEffect.orderByDesc,
                    sideEffect.limit,
                )
                dispatchEvent(
                    SettlementListEvent.SettlementLoaded(ArrayList(settlements))
                )
            }
            is SettlementListASF.RefreshData -> {
                SyncManager.getInstance().enqueue(PaymentOrderSyncer.TYPE)
                delay(2000)
                dispatchEvent(SettlementListEvent.UpdateDataRefreshed(false))
            }
        }
    }

}
