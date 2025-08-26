package com.rocketpay.mandate.feature.settlements.presentation.ui.main.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.view.PaymentTrackerListFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.view.SettlementListFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class SettlementMainAdapter(
    private val fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(index: Int): Fragment {
        when(index){
            1 -> {
                val bundle = Bundle()
                bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_ORDER_BY_DESC, true)
                bundle.putString(PaymentTrackerListFragment.BUNDLE_PAYMENT_TRACKER_TYPE, PaymentTrackerType.Collected.value)
                bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_SKIP_MANUAL_MANDATE, true)
                return PaymentTrackerListFragment.newInstance(bundle)
            }
            else -> {
                return SettlementListFragment.newInstance(null)
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when(position){
            1 ->  ResourceManager.getInstance().getString(R.string.rp_transactions)
            else -> ResourceManager.getInstance().getString(R.string.rp_settlements)
        }
    }
}