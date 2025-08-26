package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.view.PaymentTrackerListFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.view.PaymentTrackerMainFragment

internal class PaymentTrackerAdapter(
    private val f: Fragment,
    private val arguments: Bundle?
) : FragmentStateAdapter(f) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_IS_SUPER_KEY_FLOW, arguments?.getBoolean(
            PaymentTrackerMainFragment.BUNDLE_IS_SUPER_KEY_FLOW) ?: false)
        when(position){
            PaymentTrackerType.Outstanding.index -> {
                bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_ORDER_BY_DESC, true)
            }
            PaymentTrackerType.Upcoming.index -> {
                bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_ORDER_BY_DESC, false)
            }
            else -> {
                bundle.putBoolean(PaymentTrackerListFragment.BUNDLE_ORDER_BY_DESC, true)
            }
        }
        bundle.putString(PaymentTrackerListFragment.BUNDLE_PAYMENT_TRACKER_TYPE, PaymentTrackerType.getPaymentTrackerTypeByIndex(position).value)
        return PaymentTrackerListFragment.newInstance(bundle)
    }
}