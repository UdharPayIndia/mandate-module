package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import co.hyperverge.hyperkyc.data.models.HyperKycConfig
import co.hyperverge.hyperkyc.data.models.result.HyperKycResult
import co.hyperverge.hyperkyc.data.models.result.HyperKycStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.image.presentation.ui.selection.statemachine.ImageSelectionScreen
import com.rocketpay.mandate.feature.image.presentation.ui.selection.view.ImageSelectionFragment
import com.rocketpay.mandate.feature.image.presentation.ui.selection.viewmodel.ImageSelectionListener
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentKycRpBinding
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemHyperVergeFlowType
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycComponent
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycStateMachineFactory
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroScreen
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.view.KycIdentityIntroBottomSheetFragment
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.adapter.KycAdapter
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycState
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycUSF
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.KycUM
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.DialogBottomSheetSelection
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.int
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.openWebPage
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.showDialogFragment
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.databinding.DialogBottomSheetConfirmationRpBinding
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class KycFragment : BaseMainFragment<KycEvent, KycState, KycUSF>(), ImageSelectionListener {

    @Inject
    internal lateinit var kycAdapter: KycAdapter
    private lateinit var binding: FragmentKycRpBinding
    private lateinit var vm: KycUM
    @Inject
    internal lateinit var kycStateMachineFactory: KycStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    private var optionSelection: DialogBottomSheetSelection? = null
    private var bottomSheetConfirmationDialog: BottomSheetDialog? = null
    private lateinit var bottomSheetConfirmationBinding: DialogBottomSheetConfirmationRpBinding
    var isOnBoarding = false
    companion object {
        const val SKIP_ENABLED = "skip_enabled"
        const val UPDATE_KYC_STATE = "update_kyc_state"
        const val BUNDLE_IS_ONBOARDING = "BUNDLE_IS_ONBOARDING"
        fun newInstance(bundle: Bundle?): KycFragment {
            val kycFragment = KycFragment()
            kycFragment.arguments = bundle
            return kycFragment
        }
    }

    override fun injection() {
        super.injection()
        KycComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, kycStateMachineFactory)[KycStateMachine::class.java]
        vm = KycUM { stateMachine.dispatchEvent(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_support_white_rp, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_support ->{
                handleContactUsClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val skipEnabled = savedInstanceState?.getBoolean(SKIP_ENABLED) ?: false
        isOnBoarding = savedInstanceState?.getBoolean(BUNDLE_IS_ONBOARDING) ?: false
        stateMachine.dispatchEvent(KycEvent.UpdateSkipFlag(skipEnabled))
        stateMachine.dispatchEvent(KycEvent.LoadKyc)
        stateMachine.dispatchEvent(KycEvent.RefreshKyc)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentKycRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm
        kycAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = kycAdapter
    }

    override fun registerListener() {
        super.registerListener()
        FragmentResultBus.register(RpKycActivity.OWNER_IDENTITY_SUMITTED) {
            handleHyperKycResult(it as HyperKycResult)
        }
        FragmentResultBus.register(KycIdentityIntroBottomSheetFragment.OPEN_IDENTITY_STEP) {
            stateMachine.getState()?.value?.currentKycItem?.let {
                stateMachine.dispatchEvent(KycEvent.InitItem(it))
            }
        }
    }

    override fun handleState(state: KycState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: KycUSF) {
        when (sideEffect) {
            is KycUSF.ShowProgress -> {
                bottomSheetConfirmationDialog?.dismiss()
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is KycUSF.ShowFailed -> {
                vm.isErrorDialogShown = true
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message, sideEffect.primaryButtonText, sideEffect.secondaryButtonText)
                progressDialog.show()
            }
            is KycUSF.DismissProgressDialog -> {
                progressDialog.dismiss()
            }
            is KycUSF.UpdateKyc -> {
                kycAdapter.kycEnumState = sideEffect.kyc.state
                kycAdapter.selectedItem.value = sideEffect.currentKycItem
                kycAdapter.kycType = sideEffect.kycType
                kycAdapter.swapData(sideEffect.kyc.workflow.sortedBy { it.priority }, sideEffect.ownerName)
            }
            is KycUSF.GotoHyperVerge -> {
                when(KycItemHyperVergeFlowType.get(sideEffect.type)) {
                    KycItemHyperVergeFlowType.Android -> openAndroidSdk(sideEffect.kycItemInitMetaHyperVerge)
                    KycItemHyperVergeFlowType.Web -> openWeb(sideEffect.kycItemInitMetaHyperVerge)
                }
                progressDialog.dismiss()
            }
            is KycUSF.ContactUs -> {
                handleContactUsClick()
            }
            is KycUSF.ShowBackPressConfirmationDialog -> {
                vm.isBackPressDialogShown = true
                vm.progressDialogVM.setInitState(
                    ResourceManager.getInstance().getDrawable(R.color.rp_yellow_1),
                    ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle),
                    SpannableString(ResourceManager.getInstance().getString(R.string.rp_you_are_almost_done_are_you_sure_you_want_to_exit)),
                    "",
                    ResourceManager.getInstance().getString(R.string.rp_continue_kyc_form),
                    ResourceManager.getInstance().getString(R.string.rp_exit)
                )
                progressDialog.show()
            }
            is KycUSF.CloseKyc -> {
                progressDialog.dismiss()
                onBackPress()
            }
            is KycUSF.ShowIdentityIntroBottomSheet -> {
                val bundle = Bundle()
                bundle.putString(KycIdentityIntroBottomSheetFragment.BUNDLE_NAME, sideEffect.name)
                val fragment = KycIdentityIntroBottomSheetFragment.newInstance(bundle)
                showDialogFragment(KycIdentityIntroScreen.name, fragment)
            }
            is KycUSF.ShowKycStateDialog -> {
                vm.progressDialogVM.setInitState(
                    sideEffect.drawable,
                    sideEffect.icon,
                    SpannableString(sideEffect.title),
                    sideEffect.subTitle,
                    sideEffect.primaryButton,
                    sideEffect.secondaryButton
                )
                progressDialog.show()
            }
            is KycUSF.OpenMultiChoiceOptionSelection -> {
                bottomSheetConfirmationDialog?.dismiss()
                optionSelection = DialogBottomSheetSelection(
                    requireContext(),
                    sideEffect.title,
                    "",
                    sideEffect.subTitle,
                ) { itemDialogSelection ->
                    optionSelection?.dismiss()
                    stateMachine.dispatchEvent(
                        KycEvent.MultiChoiceOptionSelected(
                            sideEffect.workFlow,
                            sideEffect.inputName,
                            itemDialogSelection.type,
                            sideEffect.jsonObject
                        )
                    )
                }
                optionSelection?.updateList(sideEffect.options)
                optionSelection?.show()
            }
            is KycUSF.ShowUploadDocumentDialog -> {
                bottomSheetConfirmationDialog?.dismiss()
                vm.kycWorkFlow = sideEffect.workFlow
                vm.kycInputName = sideEffect.inputName
                vm.jsonObject = sideEffect.jsonObject
                val bundle = Bundle()
                bundle.putString(ImageSelectionFragment.BUNDLE_TITLE, sideEffect.title)
                bundle.putString(ImageSelectionFragment.BUNDLE_SUBTITLE, sideEffect.subTitle)
                bundle.putInt(ImageSelectionFragment.BUNDLE_SIZE_LIMIT, sideEffect.sizeLimit.int())
                bundle.putStringArrayList(ImageSelectionFragment.BUNDLE_ALLOWED_EXTENSIONS, ArrayList(sideEffect.allowedExtension))
                showDialogFragment(
                    ImageSelectionScreen.name,
                    ImageSelectionFragment.newInstance(bundle)
                )
            }
            is KycUSF.TriggerNextInputAction -> {
                progressDialog.dismiss()
                val nextEvent = KycStateMachine.getActionEvent(sideEffect.workFlow, sideEffect.nextInput, sideEffect.jsonObject)
                if(nextEvent != null){
                    stateMachine.dispatchEvent(nextEvent)
                }else{
                    stateMachine.dispatchEvent(KycEvent.SubmitItem(sideEffect.workFlow, sideEffect.jsonObject))
                }
            }
            is KycUSF.ShowNonBusinessConfirmationDialog -> {
                vm.kycWorkFlow = sideEffect.kycWorkFlow
                vm.jsonObject = sideEffect.jsonObject
                bottomSheetConfirmationBinding = DialogBottomSheetConfirmationRpBinding.inflate(layoutInflater)
                bottomSheetConfirmationBinding.vm = vm.bottomSheetConfirmationVM
                vm.bottomSheetConfirmationVM.setInitState(sideEffect.drawable, sideEffect.icon,
                    SpannableString(sideEffect.title), sideEffect.subTitle, sideEffect.primaryButton, sideEffect.secondaryButton)
                bottomSheetConfirmationDialog = BottomSheetDialog(requireContext())
                bottomSheetConfirmationDialog?.setContentView(bottomSheetConfirmationBinding.root)
                bottomSheetConfirmationDialog?.show()
            }
        }
    }

    private fun openWeb(kycItemInitMetaHyperVergeWeb: JsonObject?) {
        openWebPage(kycItemInitMetaHyperVergeWeb?.get("url")?.asString ?: "")
    }

    private fun openAndroidSdk(
        baseKycItemInitMetaHyperVerge: JsonObject?,
    ) {
        val config = HyperKycConfig(
            accessToken = baseKycItemInitMetaHyperVerge?.get("access_token")?.asString ?: "",
            workflowId = baseKycItemInitMetaHyperVerge?.get("workflow")?.asString ?: "",
            transactionId = baseKycItemInitMetaHyperVerge?.get("transaction_id")?.asString ?: ""
        )

        val intent = Intent(requireContext(), RpKycActivity::class.java)
        intent.putExtra(RpKycActivity.CONFIG, config)
        startActivity(intent)
    }

    private fun handleHyperKycResult(hyperKycResult: HyperKycResult) {
        when (hyperKycResult.status) {
            HyperKycStatus.USER_CANCELLED -> {
                stateMachine.dispatchEvent(KycEvent.SdkError(hyperKycResult.errorCode, ResourceManager.getInstance().getString(R.string.rp_please_contact_us_if_issue_persists)))
            }
            HyperKycStatus.ERROR -> {
                stateMachine.dispatchEvent(KycEvent.SdkError(hyperKycResult.errorCode, hyperKycResult.errorMessage ?: ResourceManager.getInstance().getString(R.string.rp_kyc_sdk_error)))
            }
            HyperKycStatus.STARTED,
            HyperKycStatus.AUTO_APPROVED,
            HyperKycStatus.AUTO_DECLINED,
            HyperKycStatus.NEEDS_REVIEW -> {
                stateMachine.dispatchEvent(KycEvent.OwnerIdentityDetailSubmitted)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentResultBus.unRegister(RpKycActivity.OWNER_IDENTITY_SUMITTED)
        FragmentResultBus.unRegister(KycIdentityIntroBottomSheetFragment.OPEN_IDENTITY_STEP)
    }

    override fun isBackPressHandled(): Boolean {
        return if(!vm.isBackPressConfirmed && !vm.isKycCompletedOrUnderReview){
            stateMachine.dispatchEvent(KycEvent.ShowBackPressConfirmationDialog)
            true
        }else{
            if(isOnBoarding) {
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                FragmentResultBus.fire(UPDATE_KYC_STATE, true)
            }
            false
        }
    }

    override fun onImageChange(uri: String?, fileSize: Double) {
        uri?.let { uri ->
            if(vm.kycWorkFlow != null && vm.kycInputName != null){
                stateMachine.dispatchEvent(KycEvent.DocumentSelected(
                    vm.kycWorkFlow!!,
                    vm.kycInputName!!,
                    uri,
                    vm.jsonObject,
                    fileSize))
            }
        }
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}
