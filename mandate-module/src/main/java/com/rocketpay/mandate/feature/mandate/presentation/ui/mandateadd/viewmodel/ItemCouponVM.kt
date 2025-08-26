package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class ItemCouponVM(
    val coupon: Coupon,
    val itemClick: (MandateAddEvent) -> Unit,
    val selectedCoupon: ObservableField<Coupon>
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val couponCodeText = ObservableField<String>(coupon.name)
    val descriptionText = ObservableField<String>(coupon.description)
    val isSelected = ObservableBoolean()
    init {
        updateUI(selectedCoupon.get())
        observeData()
    }

    private fun observeData() {
        selectedCoupon.addOnPropertyChangedCallback(object : OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val value = (sender as ObservableField<Coupon>).get()
                updateUI(value)
            }
        })
    }

    fun updateUI(selected: Coupon?){
        if(selected?.id == coupon.id){
            isSelected.set(true)
        }else{
            isSelected.set(false)
        }
    }
    fun onApplyClick() {
    }
}
