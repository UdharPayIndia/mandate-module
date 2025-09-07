package com.rocketpay.mandate.feature.settlements.presentation.ui.report.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.PeriodRangeEnum

internal class ItemPeriodRangeVM (
    private val periodRangeEnum: PeriodRangeEnum,
    val itemClick: (DownloadReportEvent) -> Unit,
    private val selectedPeriodRange: ObservableField<PeriodRangeEnum>,
    private val fromDate : ObservableField<String>,
    private val toDate : ObservableField<String>
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val radioDrawable = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
    val titleText = ObservableField<String>(periodRangeEnum.translation)
    val fromText = ObservableField<String>()
    val toText = ObservableField<String>()
    val fromToVisibility = ObservableBoolean(periodRangeEnum.value == PeriodRangeEnum.Custom.value)

    init {
        updateState(selectedPeriodRange.get())
        observeItemSelected(selectedPeriodRange)

        fromDate.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                fromText.set((sender as ObservableField<*>).get() as String?)
            }
        })

        toDate.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                toText.set((sender as ObservableField<*>).get() as String?)
            }
        })
    }

    private fun updateState(selected: PeriodRangeEnum?){
        if (periodRangeEnum.value == selected?.value) {
            radioDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
        } else {
            radioDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
        }
    }

    private fun observeItemSelected(selectedPosition: ObservableField<PeriodRangeEnum>) {
        selectedPosition.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                updateState((sender as ObservableField<*>).get() as PeriodRangeEnum?)
            }
        })
    }

    fun onItemClick() {
        itemClick(DownloadReportEvent.PeriodRangeClicked(periodRangeEnum))
    }

    fun onFromDateClick(){
        itemClick(DownloadReportEvent.FromDateClicked(periodRangeEnum))
    }

    fun onToDateClick(){
        itemClick(DownloadReportEvent.ToDateClicked(periodRangeEnum))
    }
}