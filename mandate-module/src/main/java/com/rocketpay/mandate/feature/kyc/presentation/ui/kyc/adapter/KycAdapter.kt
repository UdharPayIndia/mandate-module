package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.adapter

import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemKycBusinessBankVM
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemKycBusinessPanVM
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemKycBusinessProofVM
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemKycDetailsVM
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemKycOwnerIdentityVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class KycAdapter : RecyclerViewAdapter() {

    val selectedItem = MutableLiveData<KycWorkFlow>()
    lateinit var itemClick: (KycEvent) -> Unit
    lateinit var kycEnumState: KycStateEnum
    private var ownerName: String? = ""
    var kycType: String? = ""

    companion object{
        const val ITEM_DETAILS_TYPE = "ITEM_DETAILS_TYPE"
    }

    override fun getItemViewType(position: Int): Int {
        return if(KycStateEnum.isInReviewOrCompleted(kycEnumState.value)){
            R.layout.item_kyc_details_rp
        }else {
            when (list[position].viewType) {
                KycWorkFlowName.BusinessProof.value -> R.layout.item_kyc_business_proof_rp
                KycWorkFlowName.BankVerification.value -> R.layout.item_kyc_business_bank_rp
                KycWorkFlowName.SdkWorkFlow.value -> R.layout.item_kyc_owner_identity_rp
                KycWorkFlowName.PanDetailsVerification.value -> R.layout.item_kyc_business_pan_rp
                else -> R.layout.item_kyc_business_bank_rp
            }
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return if(KycStateEnum.isInReviewOrCompleted(kycEnumState.value)) {
            ItemKycDetailsVM(list[position].any as List<KycWorkFlow>, kycEnumState.value, kycType)
        }else {
            when (list[position].viewType) {
                KycWorkFlowName.BusinessProof.value -> ItemKycBusinessProofVM(
                    list[position].any as KycWorkFlow,
                    itemClick,
                    selectedItem,
                    kycEnumState,
                    position
                )
                KycWorkFlowName.BankVerification.value -> ItemKycBusinessBankVM(
                    list[position].any as KycWorkFlow,
                    itemClick,
                    selectedItem,
                    kycEnumState,
                    position
                )
                KycWorkFlowName.SdkWorkFlow.value -> ItemKycOwnerIdentityVM(
                    list[position].any as KycWorkFlow,
                    itemClick,
                    selectedItem,
                    kycEnumState,
                    position
                )
                KycWorkFlowName.PanDetailsVerification.value -> ItemKycBusinessPanVM(
                    list[position].any as KycWorkFlow,
                    itemClick,
                    selectedItem,
                    kycEnumState,
                    position,
                    ownerName
                )
                else -> ItemKycBusinessBankVM(list[position].any as KycWorkFlow, itemClick, selectedItem, kycEnumState, position)
            }
        }
    }

    fun swapData(items: List<KycWorkFlow>, ownerName: String?) {
        list.clear()
        this.ownerName = ownerName
        if(KycStateEnum.isInReviewOrCompleted(kycEnumState.value)){
            list.add(RecyclerViewItem(ITEM_DETAILS_TYPE, items))
        }else{
            list.addAll(items.map { RecyclerViewItem(it.name.value, it) })
        }
        notifyDataSetChanged()
    }
}
