package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentMandateAddRpBinding
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeScreen
import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFlowType
import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFragment
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateStateMachineFactory
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddStateMachine
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddUSF
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.MandateAddUM
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.MandatePreviewDialogVM
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.presentation.utils.PermissionsUtils
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.DialogBottomSheetSelection
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.makeBold
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.openWebPage
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.showDialogFragment
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DatePickerUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShareUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.databinding.BottomSheetEnterRpBinding
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class MandateAddFragment : BaseMainFragment<MandateAddEvent, MandateAddState, MandateAddUSF>() {

    @Inject
    internal lateinit var mandateStateMachineFactory: MandateStateMachineFactory
    private lateinit var binding: FragmentMandateAddRpBinding
    private lateinit var vm: MandateAddUM
    private var installmentFrequencySelection: DialogBottomSheetSelection? = null
    private var installmentSelection: DialogBottomSheetSelection? = null
    private var mandatePreviewDialog: MandatePreviewDialog? = null
    private lateinit var customInstallmentDialog: BottomSheetDialog
    private lateinit var bottomSheetEnterBinding: BottomSheetEnterRpBinding
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    private val contactPermissionDialog by lazy { ProgressDialog(requireContext(), vm.contactPermissionDialogVM) }
    private val dateChangeDialog by lazy { ProgressDialog(requireContext(), vm.dateChangeDialogVM) }
    private val errorDialog by lazy { ProgressDialog(requireContext(), vm.errorDialogVM) }
    private val walletErrorDialog by lazy { ProgressDialog(requireContext(), vm.walletErrorDialogVM) }

    private var skipRedirection: Boolean = false
    private var paymentMode: String? = null
    lateinit var permissionType: PermissionType

    companion object {
        private const val RESULT_PICK_CONTACT: Int = 212
        const val BUNDLE_CUSTOMER_NAME = "BUNDLE_CUSTOMER_NAME"
        const val BUNDLE_CUSTOMER_NUMBER = "BUNDLE_CUSTOMER_NUMBER"
        const val BUNDLE_AMOUNT = "BUNDLE_AMOUNT"
        const val BUNDLE_DOWN_PAYMENT = "BUNDLE_DOWN_PAYMENT"
        const val BUNDLE_NOTE = "BUNDLE_NOTE"
        const val BUNDLE_SHOW_SKIP = "BUNDLE_SHOW_SKIP"
        const val BUNDLE_PAYMENT_MODE = "BUNDLE_PAYMENT_MODE"
        const val BUNDLE_FINANCIER = "BUNDLE_FINANCIER"
        const val BUNDLE_REFERENCE_ID = "reference_id"
        const val BUNDLE_REFERENCE_TYPE = "reference_type"

        const val MANDATE_CREATED_SUCCESSFULLY = "MANDATE_CREATED_SUCCESSFULLY"
        fun newInstance(bundle: Bundle?): MandateAddFragment {
            val mandateAddFragment = MandateAddFragment()
            mandateAddFragment.arguments = bundle
            return mandateAddFragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, mandateStateMachineFactory)[MandateAddStateMachine::class.java]
        vm = MandateAddUM { stateMachine.dispatchEvent(it) }
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
        val customerName = savedInstanceState?.getString(BUNDLE_CUSTOMER_NAME, "") ?: ""
        val customerNumber = savedInstanceState?.getString(BUNDLE_CUSTOMER_NUMBER, "") ?: ""
        val note = savedInstanceState?.getString(BUNDLE_NOTE, "") ?: ""
        val amount = savedInstanceState?.getDouble(BUNDLE_AMOUNT, 0.0) ?: 0.0
        val downPayment = savedInstanceState?.getDouble(BUNDLE_DOWN_PAYMENT, 0.0) ?: 0.0
        val referenceId = savedInstanceState?.getString(BUNDLE_REFERENCE_ID)
        val referenceType = savedInstanceState?.getString(BUNDLE_REFERENCE_TYPE)
        val showSkip = savedInstanceState?.getBoolean(BUNDLE_SHOW_SKIP) ?: false
        val financier = savedInstanceState?.getString(BUNDLE_FINANCIER)

        paymentMode = savedInstanceState?.getString(BUNDLE_PAYMENT_MODE)
        stateMachine.dispatchEvent(MandateAddEvent.Init(customerName, customerNumber, note,
            amount, downPayment, referenceId, referenceType, showSkip, paymentMode, financier))
        stateMachine.dispatchEvent(MandateAddEvent.LoadProductWallet)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMandateAddRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun registerListener() {
        super.registerListener()
        binding.lytCustomerDetail.etMobile.setEndIconOnClickListener {
            stateMachine.dispatchEvent(MandateAddEvent.SelectContactClick)
        }
    }

    override fun initView() {
        super.initView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm

    }

    override fun handleState(state: MandateAddState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: MandateAddUSF) {
        when (sideEffect) {
            MandateAddUSF.OpenContactSelection -> {
                contactPermissionDialog.dismiss()
                handleOpenContactSelection()
            }
            MandateAddUSF.DismissContactSelectionDialog -> {
                contactPermissionDialog.dismiss()
            }
            is MandateAddUSF.OpenFrequencySelection -> {
                if (installmentFrequencySelection == null) {
                    installmentFrequencySelection = DialogBottomSheetSelection(
                        requireContext(),
                        ResourceManager.getInstance().getString(R.string.rp_select_installment_frequency),
                        (-1).toString()
                    ) { itemDialogSelection ->
                        installmentFrequencySelection?.dismiss()
                        val installmentFrequency = InstallmentFrequency.get(itemDialogSelection.type)
                        stateMachine.dispatchEvent(MandateAddEvent.InstallmentFrequencySelected(installmentFrequency))
                    }

                    installmentFrequencySelection?.updateList(sideEffect.frequencies)
                }
                installmentFrequencySelection?.show()
            }
            is MandateAddUSF.OpenInstallmentSelection -> {
                if (installmentSelection == null) {
                    installmentSelection = DialogBottomSheetSelection(
                        requireContext(),
                        ResourceManager.getInstance().getString(R.string.rp_select_installments),
                        (sideEffect.currentNoOfInstallments ?: -1).toString()
                    ) { itemDialogSelection ->
                        installmentSelection?.dismiss()
                        if (itemDialogSelection.type.toInt() == 13) {
                            stateMachine.dispatchEvent(MandateAddEvent.CustomInstallmentClick)
                        } else {
                            stateMachine.dispatchEvent(
                                MandateAddEvent.InstallmentSelected(
                                    AmountUtils.stringToInt(itemDialogSelection.type)))
                        }
                    }
                }
                installmentSelection?.updateList(sideEffect.installments)
                installmentSelection?.show()
            }
            is MandateAddUSF.OpenStartDateSelection -> {

              val minDate =   if(sideEffect.installmentFrequency == InstallmentFrequency.OneTimePayment){
                  DateUtils.getDate(DateUtils.addDay(System.currentTimeMillis(), 1))
                }else{
                    DateUtils.getCurrentDate()
              }

                dateChangeDialog.dismiss()
                DatePickerUtils.showDatePicker(requireContext(), minDate = minDate, addRemoveButton = false) { _, time ->
                    stateMachine.dispatchEvent(MandateAddEvent.StartDateSelected(time))
                }
            }
            is MandateAddUSF.ShowLoading -> {
                dateChangeDialog.dismiss()
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is MandateAddUSF.ShowError -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
            }
            is MandateAddUSF.CloseProgressDialog -> {
                progressDialog.dismiss()
            }
            is MandateAddUSF.OpenMandatePreview -> {
                showSharePaymentLinkPopUp(sideEffect.mandate, sideEffect.referenceId, sideEffect.showSkip, sideEffect.isManual)
                progressDialog.dismiss()
            }
            is MandateAddUSF.OpenUpiApp -> {
                val title = ResourceManager.getInstance().getString(
                    R.string.rp_request_sent_title_with_customer,
                    sideEffect.mandate.paymentMethodDetail.upiId
                ).getSpannable()

                val detail = ResourceManager.getInstance().getString(
                    R.string.rp_request_sent_subtitle_with_customer,
                    sideEffect.mandate.customerDetail.name,
                    sideEffect.upiApplication.name
                ).getSpannable().makeBold(sideEffect.upiApplication.name)

                vm.progressDialogVM.setSuccessState(
                    title,
                    detail
                )
            }
            is MandateAddUSF.GotoMandateDetail -> {
                mandatePreviewDialog?.dismiss()
                progressDialog.dismiss()
                gotoMandateDetail(sideEffect.mandateId, sideEffect.referenceId, sideEffect.isManual)
            }
            is MandateAddUSF.ShareOnSms -> {
                skipRedirection = false
                if (!ShareUtils.sendSms(requireContext(), sideEffect.mobileNumber, sideEffect.message)) {
                    ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_found_to_handle_sms))
                }
                progressDialog.dismiss()
                mandatePreviewDialog?.dismiss()
                gotoMandateDetail(sideEffect.mandateId, sideEffect.referenceId, sideEffect.isManual)
            }
            is MandateAddUSF.ShareOnWhatsApp -> {
                skipRedirection = false
                if (!ShareUtils.sendWhatsApp(requireContext(), sideEffect.message, sideEffect.mobileNumber)) {
                    ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_found_to_handle_data))
                }
                progressDialog.dismiss()
                mandatePreviewDialog?.dismiss()
                gotoMandateDetail(sideEffect.mandateId, sideEffect.referenceId, sideEffect.isManual)
            }
            is MandateAddUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is MandateAddUSF.ShowSnackBar -> {
                progressDialog.dismiss()
                Snackbar.make(binding.root, sideEffect.message, Snackbar.LENGTH_SHORT).show()
            }
            is MandateAddUSF.ShowProminentDisclosure -> {
                vm.contactPermissionDialogVM.setInitState(
                    sideEffect.background,
                    sideEffect.icon,
                    SpannableString(sideEffect.title),
                    sideEffect.subtitle,
                    sideEffect.actionText,
                    sideEffect.secondaryActionText
                )
                contactPermissionDialog.show()
            }
            MandateAddUSF.NumberFocusChanged -> {
            }
            MandateAddUSF.NameFocusChanged -> {
            }
            MandateAddUSF.NoteFocusChanged -> {
                smoothScrollToY(binding.scrollViewNew.bottom/3)
            }
            MandateAddUSF.AmountFocusChanged -> {
            }
            MandateAddUSF.OpenCustomInstallmentDialog -> {
                if (!::customInstallmentDialog.isInitialized) {
                    bottomSheetEnterBinding = BottomSheetEnterRpBinding.inflate(layoutInflater)
                    bottomSheetEnterBinding.vm = vm.customInstallmentVM
                    customInstallmentDialog = BottomSheetDialog(requireContext())
                    customInstallmentDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    customInstallmentDialog.setContentView(bottomSheetEnterBinding.root)
                }
                customInstallmentDialog.show()
                bottomSheetEnterBinding.etGeneric.requestFocus()
            }
            MandateAddUSF.DismissCustomInstallmentDialog -> {
                customInstallmentDialog.dismiss()
            }
            is MandateAddUSF.UpdateHelperText -> {
                vm.customInstallmentVM.helperText.set(sideEffect.helperText)
            }
            is MandateAddUSF.OpenChargeDialog -> {
                val bundle = Bundle()
                bundle.putBoolean(ChargeFragment.IS_CASH_FREE_ENABLED, sideEffect.isCashFreeEnabled)
                bundle.putString(ChargeFragment.FLOW_TYPE, ChargeFlowType.AutoPay.value)
                val fragment = ChargeFragment.newInstance(bundle)
                showDialogFragment(ChargeScreen.name, fragment)
            }
            is MandateAddUSF.ShowDateChangeDialog -> {
                vm.dateChangeDialogVM.setInitState(
                    headerBackground = sideEffect.headerBackground,
                    headerIcon = sideEffect.headerDrawable,
                    SpannableString(sideEffect.title),
                    sideEffect.detail,
                    sideEffect.actionText,
                    sideEffect.secondaryBtnText
                )
                dateChangeDialog.show()
            }
            MandateAddUSF.DismissDateChangeDialog -> {
                dateChangeDialog.dismiss()
            }
            is MandateAddUSF.CloseKeyboard -> {
                KeyboardUtils.hideKeyboard(binding.lytCustomerDetail.etMobile, requireContext())
            }
            is MandateAddUSF.ShowErrorDialog -> {
                progressDialog.dismiss()
                vm.errorDialogVM.setInitState(
                    headerBackground = sideEffect.headerBackground,
                    headerIcon = sideEffect.headerDrawable,
                    SpannableString(sideEffect.title),
                    sideEffect.detail,
                    sideEffect.actionText,
                    sideEffect.secondaryBtnText
                )
                errorDialog.show()
            }
            MandateAddUSF.DismissErrorDialog -> {
                errorDialog.dismiss()
            }
            MandateAddUSF.CloseDialog -> {
                progressDialog.dismiss()
                mandatePreviewDialog?.dismiss()
                walletErrorDialog.dismiss()
            }
            MandateAddUSF.CloseScreen -> {
                progressDialog.dismiss()
                walletErrorDialog.dismiss()
                onCloseScreen()
            }
            is MandateAddUSF.OpenLink -> {
                openWebPage(sideEffect.link)
            }
            is MandateAddUSF.ShowWalletError -> {
                vm.walletErrorDialogVM.setErrorState(
                    sideEffect.title,
                    sideEffect.subtitle,
                    sideEffect.button,
                    null
                )
                walletErrorDialog.show()
            }
        }
    }

    private fun showSharePaymentLinkPopUp(mandate: Mandate, referenceId: String?, showSkip: Boolean, isManual: Boolean) {
        if (mandatePreviewDialog == null) {
            skipRedirection = !referenceId.isNullOrEmpty()
            val mandatePreviewDialogVM = MandatePreviewDialogVM(mandate, showSkip) {
                stateMachine.dispatchEvent(it)
            }
            mandatePreviewDialog = MandatePreviewDialog(requireContext(), mandatePreviewDialogVM)
            mandatePreviewDialog?.setOnDismissListener {
                if(skipRedirection){
                    onCloseScreen()
                }else{
                    gotoMandateDetail(mandate.id, referenceId, isManual)
                }
            }
        }
        mandatePreviewDialog?.show()
    }

    private fun smoothScrollToY(scrollHeight: Int) {
        binding.scrollView.smoothScrollTo(0, scrollHeight)
    }

    private fun onCloseScreen(){
        vm.isCloseEnabled = true
        onBackPress()

    }

    private fun gotoMandateDetail(mandateId: String, referenceId: String?, isManual: Boolean) {
        vm.isCloseEnabled = true
        onBackPress()
        val bundle = Bundle()
        bundle.putString(MandateDetailFragment.MANDATE_ID, mandateId)
        bundle.putBoolean(MandateDetailFragment.BUNDLE_IS_MANUAL, isManual)
        listener?.onNavigate(MandateDetailFragment.newInstance(bundle), fragmentTag = MandateDetailScreen.name)
    }

    private fun handleOpenContactSelection() {
        permissionType = PermissionType.Contact
        PermissionsUtils.checkAndRequestPermission(this, permissionType, {
            val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            try {
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
            } catch (e: Exception) {
                ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_to_support_contact_selection))
            }
        }, {
            ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_contact_permission_permanently_denied))
        }, {
            PermissionsUtils.requestPermissions(this, permissionType)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissionType is PermissionType.Contact) {
            PermissionsUtils.checkAndRequestPermission(this, permissionType, {
                val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                try {
                    startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
                } catch (e: Exception) {
                    ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_no_apps_to_support_contact_selection))
                }
            }, {
                ShowUtils.shortToast(requireContext(), ResourceManager.getInstance()
                    .getString(R.string.rp_contact_permission_permanently_denied))
            }, {
                ShowUtils.shortToast(requireContext(), ResourceManager.getInstance().getString(R.string.rp_contact_permission_denied))
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            val contact = loadContact(requireContext(), data)
            stateMachine.dispatchEvent(MandateAddEvent.ContactSelected(contact.second, contact.first))
        }
    }

    private fun loadContact(context: Context, data: Intent?): Pair<String?, String?> {
        var mobileNumber: String? = null
        var contactName: String? = null

        data?.data?.let { uri ->
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    val phoneIndex: Int = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val nameIndex: Int = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    mobileNumber = it.getString(phoneIndex).replace(" ","")
                    contactName = it.getString(nameIndex)
                }
                cursor?.close()
            } catch (e: Exception) {
                // something went wrong
            }
        }

        return Pair(mobileNumber, contactName)
    }

    override fun onDestroy() {
        super.onDestroy()
        stateMachine.dispatchEvent(MandateAddEvent.StopPolling)
    }

    override fun isBackPressHandled(): Boolean {
        return if(!vm.isCloseEnabled){
            FragmentResultBus.fire(MANDATE_CREATED_SUCCESSFULLY, null)
            stateMachine.dispatchEvent(MandateAddEvent.BackPressed)
            vm.isBackPressHandled
        }else{
            false
        }
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

}
