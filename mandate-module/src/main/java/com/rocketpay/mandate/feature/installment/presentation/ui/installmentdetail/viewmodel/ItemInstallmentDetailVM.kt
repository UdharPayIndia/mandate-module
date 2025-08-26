package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.data.mapper.TimeState
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentJourney
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemInstallmentDetailVM(
    installmentJourney: InstallmentJourney,
    val position: Int,
    isExpanded: Boolean,
    private val itemClick: (InstallmentDetailEvent) -> Unit,
    private val count: Int,
    private val totalCount: Int,
    private val isManualMandate: Boolean,
    private val dueDate: Long,
    private val isMerchantCollected: Boolean
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val bottomDotLineVisibility = ObservableInt()
    val topDotLineVisibility = ObservableInt()
    val bottomDotLineDrawable = ObservableField<Drawable>()
    val topDotLineDrawable = ObservableField<Drawable>()

    val stateIcon = ObservableField<Drawable>()
    val title = ObservableField<String>()
    val date = ObservableField<String>()
    val detail = ObservableField<String>()
    private val iconColor = ObservableInt()
    val detailColor = ObservableInt()

    val rotation = ObservableFloat()
    val forwardIconVisibility = ObservableInt()

    val isFirstPosition = ObservableBoolean(true)

    fun onItemClick() {
        itemClick(InstallmentDetailEvent.InstallmentJourneyClick)
    }

    init {
        if (position == 0) {
            if (isExpanded) {
                rotation.set(90F)
            } else {
                rotation.set(0F)
            }
            if (1 >= totalCount) {
                forwardIconVisibility.set(View.GONE)
            } else {
                forwardIconVisibility.set(View.VISIBLE)
            }
            isFirstPosition.set(true)
        } else {
            forwardIconVisibility.set(View.GONE)
            isFirstPosition.set(false)
        }


        val installmentStateUi = if(isManualMandate){
            if(isMerchantCollected){
                InstallmentStateUi.ManuallyCollected
            }else if(dueDate < DateUtils.getCurrentDateWithoutTimeInMillis()){
                InstallmentStateUi.Outstanding
            }else{
                InstallmentStateUi.Created
            }
        }else{
            if(isMerchantCollected){
                InstallmentStateUi.ManuallyCollected
            }else if(installmentJourney.status == InstallmentState.Skipped){
                InstallmentStateUi.CollectionSkipped
            }else{
                InstallmentStateUi.get(installmentJourney.state, false)
            }
        }


        when(installmentJourney.timeState) {
            TimeState.Future -> {
                iconColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_4))
                detailColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_1))
                topDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_dotted_line, detailColor.get()))
                bottomDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_dotted_line, detailColor.get()))
            }
            TimeState.Past -> {
                iconColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_4))
                detailColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_1))
                topDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_line, iconColor.get()))
                bottomDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_line, iconColor.get()))
            }
            TimeState.Present -> {
                iconColor.set(ResourceManager.getInstance().getColor(installmentStateUi.color))
                detailColor.set(ResourceManager.getInstance().getColor(installmentStateUi.color))
                topDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_line, ResourceManager.getInstance().getColor(R.color.rp_blue_4)))
                bottomDotLineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_dotted_line, ResourceManager.getInstance().getColor(R.color.rp_grey_1)))
            }
        }

        title.set(ResourceManager.getInstance().getString(installmentStateUi.text))
        date.set(DateUtils.getDate(installmentJourney.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
        detail.set(installmentJourney.statusDescription)
        stateIcon.set(ResourceManager.getInstance().getDrawable(installmentStateUi.icon, iconColor.get()))

        if (isFirstItem()) {
            topDotLineVisibility.set(View.INVISIBLE)
        } else {
            topDotLineVisibility.set(View.VISIBLE)
        }

        if (isLastItem()) {
            bottomDotLineVisibility.set(View.INVISIBLE)
        } else {
            bottomDotLineVisibility.set(View.VISIBLE)
        }
    }

    private fun isFirstItem(): Boolean {
        return position == 0
    }

    private fun isLastItem(): Boolean {
        return position == (count - 1)
    }
}
