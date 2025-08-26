package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.view

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentInstallmentDetailRpBinding
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.adapter.InstallmentDetailAdapter
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel.InstallmentDetailUM
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.view.InstallmentUpdateFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.view.EnterPenaltyAmountFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.view.SelectRetryPeriodFragment
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycScreen
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view.KycFragment
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailScreen
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.view.SettlementDetailFragment
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainScreen
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.view.SettlementMainFragment
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.showDialogFragment
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShareUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class InstallmentDetailFragment : BaseMainFragment<InstallmentDetailEvent, InstallmentDetailState, InstallmentDetailUSF>() {

    private lateinit var binding: FragmentInstallmentDetailRpBinding
    @Inject
    internal lateinit var installmentDetailAdapter: InstallmentDetailAdapter
    private lateinit var vm: InstallmentDetailUM
    @Inject
    internal lateinit var mandateStateMachineFactory: InstallmentStateMachineFactory
    private val skipInstallmentConfirmationDialog by lazy { ProgressDialog(requireContext(), vm.skipInstallmentConfirmationDialogVM) }
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    companion object {
        const val MANDATE_ID = "mandate_id"
        const val INSTALLMENT_ID = "installment_id"
        const val BUNDLE_SUPER_KEY_ID = "super_key_id"

        const val REFRESH_MANDATE = "REFRESH_MANDATE"
        fun newInstance(bundle: Bundle?): InstallmentDetailFragment {
            val installmentDetailFragment = InstallmentDetailFragment()
            installmentDetailFragment.arguments = bundle
            return installmentDetailFragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, mandateStateMachineFactory)[InstallmentDetailStateMachine::class.java]
        vm = InstallmentDetailUM { stateMachine.dispatchEvent(it) }
    }

    private var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_refresh_rp, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> stateMachine.dispatchEvent(InstallmentDetailEvent.RefreshInstallment)
            R.id.action_support -> handleContactUsClick()

        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val mandateId = savedInstanceState?.getString(MANDATE_ID)
        val installmentId = savedInstanceState?.getString(INSTALLMENT_ID)
        val referenceId = savedInstanceState?.getString(BUNDLE_SUPER_KEY_ID)
        stateMachine.dispatchEvent(InstallmentDetailEvent.LoadMandateAndInstallment(mandateId, installmentId, referenceId))
        stateMachine.dispatchEvent(InstallmentDetailEvent.FetchInstallment(installmentId))
        stateMachine.dispatchEvent(InstallmentDetailEvent.LoadSettlementBannerInfo)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInstallmentDetailRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(getString(R.string.rp_installment_details))
        setupToolbar(vm)
        binding.vm = vm
        installmentDetailAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = installmentDetailAdapter
    }

    override fun handleState(state: InstallmentDetailState) {
        vm.handleState(state)
        if (vm.stateColor.get() != 0) {
            listener?.updateStatusBar(ResourceManager.getInstance().getColor(vm.stateColor.get()))
        }
    }

    override fun handleUiSideEffect(sideEffect: InstallmentDetailUSF) {
        when (sideEffect) {
            is InstallmentDetailUSF.UpdatePayments -> {
                installmentDetailAdapter.swapData(sideEffect.installmentJourney,
                    sideEffect.isExpanded, sideEffect.totalCount,
                    sideEffect.isManualMandate, sideEffect.dueDate,
                    sideEffect.isMerchantCollected)
            }
            is InstallmentDetailUSF.ShowToast -> {
                progressDialog.dismiss()
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is InstallmentDetailUSF.Copy -> {
                ShareUtils.copyToClipboard(requireContext(), sideEffect.link)
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is InstallmentDetailUSF.ContactUsClick -> {
                handleContactUsClick()
            }
            InstallmentDetailUSF.DismissSkipInstallmentConfirmation -> {
                skipInstallmentConfirmationDialog.dismiss()
            }
            is InstallmentDetailUSF.ShowSkipInstallmentConfirmation -> {
                vm.skipInstallmentConfirmationDialogVM.setInitState(
                    headerIcon = sideEffect.headerDrawable,
                    headerBackground = sideEffect.headerBackground,
                    titleText = SpannableString(sideEffect.title),
                    detailText = sideEffect.detail,
                    actionText = sideEffect.actionText,
                    secondaryBtnText = sideEffect.secondaryBtnText
                )
                skipInstallmentConfirmationDialog.show()
            }
            is InstallmentDetailUSF.SkipInstallmentInProgress -> {
                vm.skipInstallmentConfirmationDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
            }
            is InstallmentDetailUSF.InstallmentSkipped -> {
                vm.skipInstallmentConfirmationDialogVM.setSuccessState(SpannableString(sideEffect.title), SpannableString(sideEffect.detail))
            }
            is InstallmentDetailUSF.UnableToSkipInstallment -> {
                vm.skipInstallmentConfirmationDialogVM.setErrorState(sideEffect.title, sideEffect.detail)
            }
            is InstallmentDetailUSF.OpenInstallmentUpdateScreen -> {
                val bundle = Bundle()
                bundle.putString(InstallmentUpdateFragment.BUNDLE_INSTALLMENT_ID, sideEffect.installmentId)
                bundle.putString(InstallmentUpdateFragment.BUNDLE_MODE, sideEffect.paymentMode?.value)
                bundle.putString(InstallmentUpdateFragment.BUNDLE_COMMENT, sideEffect.comments)
                val fragment = InstallmentUpdateFragment.newInstance(bundle)
                showDialogFragment(InstallmentUpdateScreen.name, fragment)
            }
            is InstallmentDetailUSF.OpenSettlementScreen -> {
                if(!sideEffect.paymentOrderId.isNullOrEmpty()){
                    val bundle = Bundle()
                    bundle.putString(SettlementDetailFragment.BUNDLE_PAY_IN_ORDER_ID, sideEffect.paymentOrderId)
                    listener?.onNavigate(SettlementDetailFragment.newInstance(bundle), fragmentTag = SettlementDetailScreen.name)
                }else{
                    listener?.onNavigate(SettlementMainFragment.newInstance(null), fragmentTag = SettlementMainScreen.name)
                }
            }
            is InstallmentDetailUSF.OpenKyc ->{
                listener?.onNavigate(
                    KycFragment.newInstance(null),
                    fragmentTag = KycScreen.name
                )
            }
            is InstallmentDetailUSF.OpenBankAccount -> {
                val bundle = Bundle()
                bundle.putBoolean(BankAccountAddFragment.IS_FROM_ONBOARDING, false)
                bundle.putString(BankAccountAddFragment.BUNDLE_SOURCE, "installment_banner")
                listener?.onNavigate(
                    BankAccountAddFragment.newInstance(bundle),
                    fragmentTag = BankAccountAddScreen.name
                )
            }
            is InstallmentDetailUSF.ShowLoader -> {
                vm.progressDialogVM.setProgressState(
                    titleText = sideEffect.message,
                    subtitleText = ""
                )
                progressDialog.show()
            }
            is InstallmentDetailUSF.ShowError -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is InstallmentDetailUSF.DismissLoader -> {
                progressDialog.dismiss()
            }
            is InstallmentDetailUSF.OpenEnterPenaltyBottomSheet -> {
                val bundle = Bundle()
                bundle.putString(EnterPenaltyAmountFragment.BUNDLE_MANDATE_ID, sideEffect.mandateId)
                bundle.putString(EnterPenaltyAmountFragment.BUNDLE_INSTALLMENT_ID, sideEffect.installmentId)
                bundle.putString(EnterPenaltyAmountFragment.BUNDLE_INSTALLMENT_AMOUNT, sideEffect.installmentAmount.toString())
                val fragment = EnterPenaltyAmountFragment.newInstance(bundle)
                showDialogFragment(EnterPenaltyAmountScreen.name, fragment)
            }
            is InstallmentDetailUSF.OpenSelectRetryDateBottomSheet -> {
                val bundle = Bundle()
                bundle.putString(SelectRetryPeriodFragment.BUNDLE_MANDATE_ID, sideEffect.mandateId)
                bundle.putString(SelectRetryPeriodFragment.BUNDLE_INSTALLMENT_ID, sideEffect.installmentId)
                val fragment = SelectRetryPeriodFragment.newInstance(bundle)
                showDialogFragment(SelectRetryPeriodScreen.name, fragment)

            }
        }
    }

    override fun deInitView() {
        super.deInitView()
        listener?.updateStatusBar(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

    override fun isBackPressHandled(): Boolean {
        FragmentResultBus.fire(REFRESH_MANDATE, null)
        return super.isBackPressHandled()
    }
}
