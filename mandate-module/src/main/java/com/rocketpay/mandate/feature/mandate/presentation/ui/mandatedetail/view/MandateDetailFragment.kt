package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentMandateDetailRpBinding
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.OtpTimer
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.view.InstallmentAddFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.view.InstallmentDetailFragment
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateStateMachineFactory
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.adapter.MandateDetailAdapter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailStateMachine
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailUSF
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.MandateDetailUM
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShareUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class MandateDetailFragment : BaseMainFragment<MandateDetailEvent, MandateDetailState, MandateDetailUSF>() {

    private lateinit var binding: FragmentMandateDetailRpBinding
    @Inject
    internal lateinit var mandateDetailAdapter: MandateDetailAdapter
    private lateinit var vm: MandateDetailUM
    @Inject
    internal lateinit var mandateStateMachineFactory: MandateStateMachineFactory
    private lateinit var otpTimer: OtpTimer
    private val dateChangeDialog by lazy { ProgressDialog(requireContext(), vm.dateChangeDialogVM) }
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    private val mandateDeleteConfirmationDialog by lazy { ProgressDialog(requireContext(), vm.mandateDeleteConfirmationDialogVM) }
    private val mandateCancelConfirmationDialog by lazy { ProgressDialog(requireContext(), vm.mandateCancelConfirmationDialogVM) }

    private var referenceId: String? = null
    companion object {
        const val MANDATE_ID = "mandate_id"
        const val BUNDLE_SUPER_KEY_ID = "super_key_id"
        const val BUNDLE_IS_MANUAL = "is_manual"
        const val BUNDLE_INSTALLMENT_SERIAL_NUMBER = "installment_serial_number"

        fun newInstance(bundle: Bundle?): MandateDetailFragment {
            val mandateDetailFragment = MandateDetailFragment()
            mandateDetailFragment.arguments = bundle
            return mandateDetailFragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, mandateStateMachineFactory)[MandateDetailStateMachine::class.java]
        vm = MandateDetailUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val mandateId = savedInstanceState?.getString(MANDATE_ID)
        referenceId = savedInstanceState?.getString(BUNDLE_SUPER_KEY_ID)
        val installmentSerialNumber = savedInstanceState?.getInt(BUNDLE_INSTALLMENT_SERIAL_NUMBER, -1) ?: -1
        stateMachine.dispatchEvent(MandateDetailEvent.Init)
        if(!referenceId.isNullOrEmpty()){
            stateMachine.dispatchEvent(MandateDetailEvent.LoadSuperKey(referenceId.orEmpty()))
        }else{
            stateMachine.dispatchEvent(MandateDetailEvent.LoadMandate(mandateId, installmentSerialNumber))
        }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMandateDetailRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        stateMachine.dispatchEvent(MandateDetailEvent.StartPolling)
    }

    private var menu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete_rp, menu)
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
            val menuItemRefresh = menu.findItem(R.id.refresh)
            val textRefresh = ResourceManager.getInstance().getString(R.string.rp_refresh)
            val refreshText = textRefresh
                .getSpannable()
                .setTextColor(textRefresh, ResourceManager.getInstance().getColor(R.color.rp_grey_2))
            menuItemRefresh.title = refreshText

            val menuItem = menu.findItem(R.id.delete)
            val text = ResourceManager.getInstance().getString(R.string.rp_delete)
            val deleteText = text
                .getSpannable()
                .setTextColor(text, ResourceManager.getInstance().getColor(R.color.rp_red_1))
            menuItem.title = deleteText

            val menuItemCancel = menu.findItem(R.id.cancel)
            val textCancel = ResourceManager.getInstance().getString(R.string.rp_cancel)
            val cancelText = textCancel
                .getSpannable()
                .setTextColor(textCancel, ResourceManager.getInstance().getColor(R.color.rp_red_1))
            menuItemCancel.title = cancelText
        }
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.call -> stateMachine.dispatchEvent(MandateDetailEvent.CallCustomerClick)
            R.id.delete -> stateMachine.dispatchEvent(MandateDetailEvent.DeleteMandateClick)
            R.id.cancel -> stateMachine.dispatchEvent(MandateDetailEvent.CancelMandateClick)
            R.id.refresh -> stateMachine.dispatchEvent(MandateDetailEvent.RefreshMandate(true))
            R.id.action_help -> {
                handleContactUsClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(getString(R.string.rp_mandate_details))
        setupToolbar(vm)
        binding.vm = vm
        mandateDetailAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = mandateDetailAdapter
    }

    override fun registerListener() {
        super.registerListener()
        FragmentResultBus.register(InstallmentDetailFragment.REFRESH_MANDATE) {
            stateMachine.dispatchEvent(MandateDetailEvent.RefreshClick(isAutomatically = true))
        }
        vm.isDeleteEnabled.observe(viewLifecycleOwner, {
                menu?.findItem(R.id.delete)?.isVisible = it
            }
        )
        vm.isCancelEnabled.observe(viewLifecycleOwner,{
                menu?.findItem(R.id.cancel)?.isVisible = it
            }
        )
    }

    override fun handleState(state: MandateDetailState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: MandateDetailUSF) {
        when (sideEffect) {
            is MandateDetailUSF.UpdateInstallments -> {
                mandateDetailAdapter.swapData(
                    sideEffect.installments,
                    sideEffect.nextInstallmentId,
                    sideEffect.showMandateTag,
                    sideEffect.manualMandate,
                    sideEffect.mandate
                )
                binding.rvList.postDelayed({
                    val recyclerViewIndex = sideEffect.installmentSerialNumber
                    if(recyclerViewIndex in 0..<sideEffect.installments.size) {
                        binding.appBar.setExpanded(false)
                        binding.rvList.smoothScrollToPosition(recyclerViewIndex)
                        val delayInMillis = ((recyclerViewIndex/50) + 1) * 1000L
                        binding.rvList.postDelayed({
                            val viewHolder = binding.rvList.findViewHolderForAdapterPosition(recyclerViewIndex)
                            if (viewHolder != null) {
                                viewHolder.itemView?.let { itemView ->
                                    // Highlight animation: Flash effect
                                    val originalColor = itemView.background
                                    val highlightColor = ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_item_active_filled)

                                    itemView.background = highlightColor
                                    itemView.animate()
                                        .setDuration(750)
                                        .alpha(0.9f)
                                        .withEndAction {
                                            itemView.alpha = 1f
                                            itemView.background = originalColor
                                        }
                                        .start()
                                }
                            }
                        }, delayInMillis)
                    }
                }, 100)
            }
            is MandateDetailUSF.ShowToast -> {
                Snackbar.make(binding.root, sideEffect.message, Snackbar.LENGTH_SHORT).show()
            }
            is MandateDetailUSF.Copy -> {
                ShareUtils.copyToClipboard(requireContext(), sideEffect.paymentLink)
                ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_copied_link))
            }
            is MandateDetailUSF.ShareOnWhatsApp -> {
                if (!ShareUtils.sendWhatsApp(requireContext(), sideEffect.message, sideEffect.mobileNumber)) {
                    ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_found_to_handle_data))
                }
            }
            is MandateDetailUSF.UpdateHeader -> {
                if (sideEffect.mode is PaymentMethod.Upi && sideEffect.mandateState is MandateState.Pending) {
                    if (!::otpTimer.isInitialized) {
                        otpTimer = OtpTimer(
                            otpTimeout = sideEffect.pollDuration,
                            interval = sideEffect.pollInterval,
                            {
                                // No action required, because of infinite interval
                            },
                            {
                                stateMachine.dispatchEvent(MandateDetailEvent.LoadMandateState)
                            }
                        )
                        otpTimer.start()
                    }
                } else if (::otpTimer.isInitialized) {
                    otpTimer.cancel()
                }
            }
            is MandateDetailUSF.ShowDateChangeDialog -> {
                progressDialog.dismiss()
                vm.dateChangeDialogVM.setInitState(
                    headerBackground = sideEffect.headerBackground,
                    headerIcon = sideEffect.headerDrawable,
                    SpannableString(sideEffect.title),
                    sideEffect.detail,
                    sideEffect.actionText
                )
                dateChangeDialog.show()
            }
            MandateDetailUSF.DismissDateChangeDialog -> {
                dateChangeDialog.dismiss()
            }
            MandateDetailUSF.OpenChatWithUs -> {
                handleContactUsClick()
            }
            is MandateDetailUSF.ShowDeleteMandateDialog -> {
                vm.mandateDeleteConfirmationDialogVM.setInitState(
                    headerIcon = sideEffect.headerDrawable,
                    headerBackground = sideEffect.headerBackground,
                    titleText = SpannableString(sideEffect.title),
                    detailText = sideEffect.detail,
                    actionText = sideEffect.actionText,
                    secondaryBtnText = sideEffect.secondaryBtnText
                )
                mandateDeleteConfirmationDialog.show()
            }
            is MandateDetailUSF.DismissDeleteMandateDialog -> {
                mandateDeleteConfirmationDialog.dismiss()
            }
            is MandateDetailUSF.ShowProgressDialog -> {
                vm.progressDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
                progressDialog.show()
            }
            is MandateDetailUSF.DismissProgressDialog -> {
                progressDialog.dismiss()
            }
            is MandateDetailUSF.ShowErrorDialog -> {
                vm.progressDialogVM.setErrorState(sideEffect.title, "")
                progressDialog.show()
            }
            is MandateDetailUSF.DeleteMandateInProgress -> {
                vm.mandateDeleteConfirmationDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
            }
            is MandateDetailUSF.MandateDeleted -> {
                vm.mandateDeleteConfirmationDialogVM.setSuccessState(SpannableString(sideEffect.title), SpannableString(sideEffect.detail))
            }
            is MandateDetailUSF.MandateDeletionFailed -> {
                vm.mandateDeleteConfirmationDialogVM.setErrorState(sideEffect.title, sideEffect.detail)
            }
            is MandateDetailUSF.ShowCancelMandateDialog -> {
                vm.mandateCancelConfirmationDialogVM.setInitState(
                    headerIcon = sideEffect.headerDrawable,
                    headerBackground = sideEffect.headerBackground,
                    titleText = SpannableString(sideEffect.title),
                    detailText = sideEffect.detail,
                    actionText = sideEffect.actionText,
                    secondaryBtnText = sideEffect.secondaryBtnText
                )
                mandateCancelConfirmationDialog.show()
            }
            is MandateDetailUSF.DismissCancelMandateDialog -> {
                mandateCancelConfirmationDialog.dismiss()
            }
            is MandateDetailUSF.CancelMandateInProgress -> {
                vm.mandateCancelConfirmationDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
            }
            is MandateDetailUSF.MandateCancelled -> {
                vm.mandateCancelConfirmationDialogVM.setSuccessState(SpannableString(sideEffect.title), SpannableString(sideEffect.detail))
            }
            is MandateDetailUSF.MandateCancelFailed -> {
                vm.mandateCancelConfirmationDialogVM.setErrorState(sideEffect.title, sideEffect.detail)
            }
            is MandateDetailUSF.GotoMandateList -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
                mandateDeleteConfirmationDialog.dismiss()
                onBackPress()
            }
            is MandateDetailUSF.GotoInstallmentDetail -> {
                val bundle = Bundle()
                bundle.putString(InstallmentDetailFragment.INSTALLMENT_ID, sideEffect.installmentId)
                bundle.putString(InstallmentDetailFragment.MANDATE_ID, sideEffect.mandateId)
                bundle.putString(InstallmentDetailFragment.BUNDLE_SUPER_KEY_ID, sideEffect.referenceId)
                listener?.onNavigate(InstallmentDetailFragment.newInstance(bundle), fragmentTag = InstallmentDetailScreen.name)
            }
            is MandateDetailUSF.GotoCreateNewInstallmentClick -> {
                val bundle = Bundle()
                bundle.putString(InstallmentAddFragment.MANDATE_ID, sideEffect.mandateId)
                listener?.onNavigate(InstallmentAddFragment.newInstance(bundle), fragmentTag = InstallmentAddScreen.name)
            }
            is MandateDetailUSF.CallNumber -> {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sideEffect.mobileNumber}"))
                startActivity(intent)
            }
            is MandateDetailUSF.CloseScreen -> {
                listener?.onBackPressed()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stateMachine.dispatchEvent(MandateDetailEvent.StopPolling)
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentResultBus.unRegister(InstallmentDetailFragment.REFRESH_MANDATE)
    }
}
