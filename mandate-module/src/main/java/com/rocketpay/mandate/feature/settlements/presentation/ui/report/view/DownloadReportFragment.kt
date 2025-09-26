package com.rocketpay.mandate.feature.settlements.presentation.ui.report.view

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DatePickerUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentDownloadReportRpBinding
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementStateMachineFactory
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.adpater.PeriodRangeAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportState
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportUSF
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.PeriodRangeEnum
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.viewmodel.DownloadReportUM
import javax.inject.Inject

internal class DownloadReportFragment :
    StateMachineBottomSheetFragment<DownloadReportEvent, DownloadReportState, DownloadReportUSF>() {

    @Inject
    lateinit var periodRangeAdapter: PeriodRangeAdapter

    private lateinit var binding: FragmentDownloadReportRpBinding
    private lateinit var vm: DownloadReportUM

    @Inject
    lateinit var settlementStateMachineFactory: SettlementStateMachineFactory

    companion object {
        fun newInstance(bundle: Bundle?): DownloadReportFragment {
            val fragment = DownloadReportFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RpAdjustResizeBottomSheetTheme)
    }

    override fun injection() {
        super.injection()
        SettlementComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(
            this,
            settlementStateMachineFactory
        )[DownloadReportStateMachine::class.java]
        vm = DownloadReportUM { stateMachine.dispatchEvent(it) }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        expandFull(false)
        binding = FragmentDownloadReportRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm

        periodRangeAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        periodRangeAdapter.selectedPeriodRange = vm.selectedPeriodRange
        periodRangeAdapter.fromDate = vm.fromDate
        periodRangeAdapter.toDate = vm.toDate
        binding.rvList.adapter = periodRangeAdapter
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_hr),
                showFirstDivider = true,
                showLastDivider = true,
                extraPaddingLeft = 0.toFloat(),
                extraPaddingRight = 0.toFloat(),
                dividerStartPosition = 0
            )
        )
        periodRangeAdapter.swapData(PeriodRangeEnum.getPeriodRangeList())

    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
    }


    override fun handleState(state: DownloadReportState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: DownloadReportUSF) {
        when (sideEffect) {
            is DownloadReportUSF.CloseScreen -> {
                dismiss()
            }
            is DownloadReportUSF.ShowMessage -> {
                if (requireDialog().window?.decorView != null) {
                    requireDialog().window?.decorView?.let {
                        val snackbar = Snackbar.make(
                            it,
                            sideEffect.message, Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                } else {
                    ShowUtils.shortToast(requireContext(), sideEffect.message)
                }
            }
            is DownloadReportUSF.OpenFromDateCalender -> {
                val calendar = Calendar.getInstance()
                // ----- Start of this month -----
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                DatePickerUtils.showDatePicker(requireContext(),
                    maxDate = DateUtils.getDate(System.currentTimeMillis()),
                    date = DateUtils.getDate(calendar.timeInMillis),
                    addRemoveButton = false
                ) { _, time ->
                    stateMachine.dispatchEvent(DownloadReportEvent.FromDateSelected(time))
                }
            }
            is DownloadReportUSF.OpenToDateCalender -> {
                DatePickerUtils.showDatePicker(requireContext(),
                    minDate = DateUtils.getDate(sideEffect.fromDate),
                    maxDate = DateUtils.getDate(System.currentTimeMillis()),
                    addRemoveButton = false) { _, time ->
                    stateMachine.dispatchEvent(DownloadReportEvent.ToDateSelected(time))
                }
            }
        }
    }
}
