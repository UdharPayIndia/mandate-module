package com.rocketpay.mandate.main.init

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.feature.common.domain.CommonUseCase
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDataStore
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.login.data.datasource.local.LoginDataStore
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view.MandateAddFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.view.MandateListFragment
import com.rocketpay.mandate.main.database.MandateDatabase
import com.rocketpay.mandate.main.presentation.view.RpMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

object MandateLauncher {

    const val HELP_ACTION_TRIGGERED = "HELP_ACTION_TRIGGERED"
    fun openMandateList(context: Context) {
        val intent = Intent(context, RpMainActivity::class.java)
        context.startActivity(intent)
    }

    fun openMandateDetails(context: Context, referenceId: String) {
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_MANDATE_DETAIL)
        intent.putExtra(RpMainActivity.BUNDLE_REFERENCE_ID, referenceId)
        context.startActivity(intent)
    }

    fun openMandateCreation(
        context: Context,
        referenceId: String,
        customerName: String = "",
        mobileNumber: String = "",
        description: String = "",
        amount: Long = 0L,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_MANDATE_ADDITION)
        intent.putExtra(RpMainActivity.BUNDLE_REFERENCE_ID, referenceId)
        intent.putExtra(MandateAddFragment.BUNDLE_CUSTOMER_NAME, customerName)
        intent.putExtra(MandateAddFragment.BUNDLE_CUSTOMER_NUMBER, mobileNumber)
        intent.putExtra(MandateAddFragment.BUNDLE_NOTE, description)
        intent.putExtra(MandateAddFragment.BUNDLE_AMOUNT, amount)
        context.startActivity(intent)
    }

    fun openBankAccountList(
        context: Context,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_BANK_ACCOUNT_LIST)
        context.startActivity(intent)
    }

    fun openBankAccountAddition(
        context: Context,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_BANK_ACCOUNT_ADD)
        context.startActivity(intent)
    }

    fun openPaymentTracker(
        context: Context,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_PAYMENT_TRACKER)
        context.startActivity(intent)
    }

    fun openSettlementDetails(
        context: Context,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_SETTLEMENT)
        context.startActivity(intent)
    }

    fun openKyc(
        context: Context,
    ){
        val intent = Intent(context, RpMainActivity::class.java)
        intent.putExtra(RpMainActivity.BUNDLE_FLOW, RpMainActivity.FLOW_KYC)
        context.startActivity(intent)
    }


    fun getMandateList(context: Context): Fragment {
        return MandateListFragment.newInstance(null)
    }

    suspend fun getKycStatus(): KycStateEnum{
        return CommonUseCase.getInstance().getKycStatus()
    }

    fun getKycStatusLive(): Flow<KycStateEnum>{
        return CommonUseCase.getInstance().getKycStatusLive()
    }

    suspend fun logoutUser(){
        withContext(Dispatchers.IO) {
            val kycDataStore = KycDataStore(
                DataStore(
                    MandateManager.getInstance().getContext(),
                    KycDataStore.KYC_DATA_STORE_NAME
                )
            )
            kycDataStore.setIsKycCompleted(false)
            val loginDataStore = LoginDataStore(
                DataStore(
                    MandateManager.getInstance().getContext(),
                    LoginDataStore.LOGIN_DATA_STORE
                )
            )
            loginDataStore.setUserId("")
            loginDataStore.setAccountId("")
            loginDataStore.setToken("")
            MandateDatabase.instance.clearAllTables()
        }
    }
}