package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.feature.mandate.presentation.ui.utils.WhatsAppMessageParserUtils
import com.rocketpay.mandate.feature.permission.common.PermissionType
import com.rocketpay.mandate.feature.permission.feature.domain.entities.ProminentDisclosureStatus
import com.rocketpay.mandate.feature.permission.feature.domain.usecases.PermissionUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogModel
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.PhoneUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.TimeConstant
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.common.domain.CommonUseCase
import com.rocketpay.mandate.feature.mandate.data.entities.PenaltyMetaDto
import com.rocketpay.mandate.feature.mandate.data.entities.RetryInstallmentMetaDto
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.text.format

internal class MandateAddStateMachine(
    private val mandateUseCase: MandateUseCase,
    private val bankAccountUseCase: BankAccountUseCase,
    private val permissionUseCase: PermissionUseCase,
    private val chargeUseCase: ChargeUseCase,
    private val productUseCase: ProductUseCase,
    private val propertyUseCase: PropertyUseCase
) : SimpleStateMachineImpl<MandateAddEvent, MandateAddState, MandateAddASF, MandateAddUSF>(
    BaseAnalyticsHandler()
) {

    var job: Job? = null
    override fun startState(): MandateAddState {
        return MandateAddState()
    }

    override fun handleEvent(
        event: MandateAddEvent,
        state: MandateAddState
    ): Next<MandateAddState?, MandateAddASF?, MandateAddUSF?> {
        return when (event) {
            is MandateAddEvent.Init -> {
                val installments = mutableListOf<ItemDialogBottomSheet>()
                if (state.installments.isEmpty()) {
                    for (installment in 2..13) {
                        val installmentInString = installment.toString()
                        val itemDialogBottomSheet = if (installment == 13) {
                            ItemDialogBottomSheet(
                                installmentInString, ResourceManager.getInstance().getString(
                                    R.string.rp_custom_no_of_installment
                                )
                            )
                        } else {
                            ItemDialogBottomSheet(
                                installmentInString, ResourceManager.getInstance().getString(
                                    R.string.rp_x_no_of_installments, installmentInString
                                )
                            )
                        }
                        installments.add(itemDialogBottomSheet)
                    }
                }
                next(
                    state.copy(
                        name = event.customerName,
                        number = event.customerNumber,
                        amount = if (event.amount > 0.0) {
                            event.amount.toString()
                        } else {
                            ""
                        },
                        downPayment = if (event.downPayment > 0.0) {
                            event.downPayment.toString()
                        } else {
                            ""
                        },
                        note = event.note,
                        referenceId = event.referenceId,
                        referenceType = event.referenceType,
                        installments = installments,
                        charge = chargeUseCase.getCharge(),
                        showSkip = event.showSkip,
                        paymentMethod = PaymentMethod.get(event.paymentMode),
                        termsAndConditionUrl = state.termsAndConditionUrl,
                        financier = event.financier
                    ), MandateAddASF.LoadSupportedFrequency(state.isAdhocEnabled)
                )
            }
            is MandateAddEvent.LoadProductWallet -> {
                next(MandateAddASF.LoadProductWallet)
            }
            is MandateAddEvent.UpdateProductWallet -> {
                val balanceCount = abs(event.productWallet?.outstanding.double())
                if(balanceCount != 0.0){
                    next(state.copy(productWallet = event.productWallet))
                }else{
                    next(state.copy(productWallet = event.productWallet),
                        MandateAddUSF.ShowWalletError(
                            ResourceManager.getInstance().getString(R.string.rp_no_token_available),
                            ResourceManager.getInstance().getString(R.string.rp_please_contact_your_distributor),
                            ResourceManager.getInstance().getString(R.string.rp_go_back)
                        ))
                }
            }
            is MandateAddEvent.CustomerNameChanged -> {
                val customerName = event.customerName
                val newState = if (mandateUseCase.isValidName(customerName)) {
                    state.copy(nameError = null, name = customerName)
                } else {
                    state.copy(
                        name = customerName,
                        nameError = ResourceManager.getInstance().getString(R.string.rp_error_name)
                    )
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable))
            }

            is MandateAddEvent.CustomerNumberChanged -> {
                val customerNumber = PhoneUtils.purifiedIndianMobileNumber(event.customerNumber)
                val newState = if (mandateUseCase.isValidMobileNumber(customerNumber)) {
                    state.copy(numberError = null, number = customerNumber)
                } else {
                    state.copy(
                        number = event.customerNumber,
                        numberError = ResourceManager.getInstance()
                            .getString(R.string.rp_error_mobile_number)
                    )
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable))
            }

            is MandateAddEvent.NoteChanged -> {
                val newState = if (mandateUseCase.isValidNotes(event.note)) {
                    state.copy(noteError = null, note = event.note)
                } else {
                    state.copy(
                        note = event.note,
                        noteError = ResourceManager.getInstance().getString(R.string.rp_error_notes)
                    )
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable))
            }

            is MandateAddEvent.AmountChanged -> {
                val newState = if (mandateUseCase.isValidAmount(event.amount)) {
                    val pair = getPaymentMode(
                        event.amount,
                        state.installment,
                        state.installmentFrequency,
                        state.paymentMethod,
                        state.maxUpiAmountLimit
                    )
                    state.copy(
                        amount = event.amount,
                        amountError = null,
                        isUpiEnable = pair.first,
                        paymentMethod = pair.second
                    )
                } else {
                    state.copy(
                        amount = event.amount,
                        amountError = ResourceManager.getInstance().getString(R.string.rp_error_amount)
                    )
                }
                val isPenaltyApplicable =
                    isPenaltyApplicable(
                        event.amount,
                        state.installment,
                        state.paymentMethod,
                        state.minimumInstallmentAmountForPenalty
                    )
                val verifyEnable = shouldEnableVerify(newState)
                if(state.isPenaltySelected && !isPenaltyApplicable.first){
                    next(
                        newState.copy(
                            isGeneratePaymentLinkEnable = verifyEnable,
                            isPenaltySelected = false,
                            penaltyEnableError = isPenaltyApplicable.second.orEmpty()
                        ),
                        MandateAddUSF.ShowSnackBar(isPenaltyApplicable.second.orEmpty())
                    )
                }else{
                    next(
                        newState.copy(
                            isGeneratePaymentLinkEnable = verifyEnable,
                            penaltyEnableError = isPenaltyApplicable.second.orEmpty()
                        )
                    )
                }
            }

            is MandateAddEvent.DownPaymentChanged -> {
                val newState = if (mandateUseCase.isValidAmount(event.downPayment)) {
                    state.copy(downPaymentError = null, downPayment = event.downPayment)
                } else {
                    state.copy(
                        downPayment = event.downPayment,
                        downPaymentError = ResourceManager.getInstance()
                            .getString(R.string.rp_error_amount)
                    )
                }
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable))
            }

            MandateAddEvent.StartDateClick -> {
                    next(MandateAddUSF.OpenStartDateSelection(state.installmentFrequency, state.startDate))
            }

            MandateAddEvent.InstallmentFrequencyClick -> {
                if (state.amount.isEmpty()) {
                    next(
                        MandateAddUSF.ShowToast(
                            ResourceManager.getInstance().getString(R.string.rp_error_enter_amount)
                        )
                    )
                } else {
                    next(
                        MandateAddUSF.OpenFrequencySelection(
                            state.installmentFrequency,
                            state.installmentFrequencies
                        )
                    )
                }
            }

            MandateAddEvent.InstallmentClick -> {
                if (state.installmentFrequency == null) {
                    next(
                        MandateAddUSF.ShowToast(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_error_select_installment_frequency)
                        )
                    )
                } else {
                    state.installments.forEach {
                        val amount = AmountUtils.stringToDouble(state.amount) / it.type.toInt()
                        val tempAmount = if (it.type.toInt() == 13) {
                            "₹--"
                        } else {
                            AmountUtils.format(amount)
                        }
                        it.suffix = String.format(
                            ResourceManager.getInstance()
                                .getString(state.installmentFrequency.suffix_per_ly), tempAmount
                        )
                    }
                    next(
                        MandateAddUSF.OpenInstallmentSelection(
                            state.installment,
                            state.installments
                        )
                    )
                }
            }

            MandateAddEvent.NachClick -> {
                val isPenaltyApplicable = isPenaltyApplicable(
                        state.amount,
                        state.installment,
                        PaymentMethod.Nach,
                        state.minimumInstallmentAmountForPenalty
                    )
                val newState = state.copy(paymentMethod = PaymentMethod.Nach)
                val verifyEnable = shouldEnableVerify(newState)
                if(state.isPenaltySelected && !isPenaltyApplicable.first){
                    next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable,
                        isPenaltySelected = false,
                        penaltyEnableError = isPenaltyApplicable.second.orEmpty()),
                        MandateAddUSF.ShowSnackBar(isPenaltyApplicable.second.orEmpty()))
                }else{
                    next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable,
                        penaltyEnableError = isPenaltyApplicable.second.orEmpty()))
                }
            }

            MandateAddEvent.UpiClick -> {
                val isPenaltyApplicable = isPenaltyApplicable(
                        state.amount,
                        state.installment,
                        PaymentMethod.Upi,
                        state.minimumInstallmentAmountForPenalty
                    )
                val newState = state.copy(
                    paymentMethod = PaymentMethod.Upi,
                    penaltyEnableError = isPenaltyApplicable.second.orEmpty()
                )
                val verifyEnable = shouldEnableVerify(newState)
                next(newState.copy(isGeneratePaymentLinkEnable = verifyEnable))
            }

            is MandateAddEvent.PaymentLinkGenerated -> {
                if (state.paymentMethod == PaymentMethod.Manual) {
                    next(
                        state.copy(mandate = event.mandate), MandateAddUSF.GotoMandateDetail(
                            event.mandate.id,
                            state.referenceId,
                            event.mandate.paymentMethodDetail.method == PaymentMethod.Manual
                        )
                    )
                } else {
                    next(
                        state.copy(mandate = event.mandate),
                        MandateAddASF.OpenMandatePreview(event.mandate)
                    )
                }
            }

            is MandateAddEvent.OpenMandatePreview -> {
                next(
                    MandateAddASF.StartPolling(event.mandate.id),
                    MandateAddUSF.OpenMandatePreview(
                        event.mandate,
                        state.referenceId,
                        state.showSkip,
                        state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                        state.maximumPenaltyAmount,
                        state.isPenaltyEnabled
                    ),
                )
            }

            is MandateAddEvent.UnableToGeneratePaymentLink -> {
                if (state.paymentMethod == PaymentMethod.Manual) {
                    next(
                        state.copy(isMandateCreationInitiated = false),
                        MandateAddUSF.ShowError(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_payment_schedule_creation_failed), event.message
                        )
                    )
                } else {
                    next(
                        state.copy(isMandateCreationInitiated = false),
                        MandateAddUSF.ShowError(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_link_generation_failed), event.message
                        )
                    )
                }
            }

            is MandateAddEvent.SharePaymentLinkClick -> {
                next(MandateAddASF.ShareOnLinkClick(event.mandate, event.viaSms))
            }

            is MandateAddEvent.SkipSharePaymentLinkClick -> {
                next(MandateAddUSF.CloseDialog)
            }

            is MandateAddEvent.WhatsAppTemplateCreated -> {
                val isManual = event.mandate.paymentMethodDetail.method == PaymentMethod.Manual
                if(event.viaSms){
                    next(
                        MandateAddUSF.ShareOnSms(
                            event.mandate.customerDetail.mobileNumber,
                            event.messageTemplate,
                            event.mandate.id,
                            state.referenceId,
                            isManual
                        )
                    )
                }else {
                    next(
                        MandateAddUSF.ShareOnWhatsApp(
                            event.mandate.customerDetail.mobileNumber,
                            event.messageTemplate,
                            event.mandate.id,
                            state.referenceId,
                            isManual
                        )
                    )
                }
            }

            is MandateAddEvent.ContactSelected -> {
                if (event.contactName != null && event.contactNumber != null) {
                    val newState =
                        state.copy(name = event.contactName, number = event.contactNumber)
                    val verifyEnable = shouldEnableVerify(newState)
                    next(
                        newState.copy(
                            isGeneratePaymentLinkEnable = verifyEnable,
                            isContactSelected = true
                        )
                    )
                } else {
                    next(
                        MandateAddUSF.ShowToast(
                            ResourceManager.getInstance().getString(R.string.rp_invalid_contact)
                        )
                    )
                }
            }

            is MandateAddEvent.InstallmentFrequencySelected -> {
                val newState =
                    if (event.installmentFrequency is InstallmentFrequency.OneTimePayment) {
                        state.copy(
                            installmentFrequency = event.installmentFrequency,
                            installment = 1,
                            startDate = null,
                            isCollectionInInstallmentEnabled = false,
                            product = MandateProduct.Default
                        )
                    } else if (event.installmentFrequency is InstallmentFrequency.Adhoc) {
                        state.copy(
                            installmentFrequency = event.installmentFrequency,
                            installment = 1,
                            product = MandateProduct.Khata,
                            startDate = System.currentTimeMillis()
                        )
                    } else {
                        state.copy(
                            installmentFrequency = event.installmentFrequency,
                            product = MandateProduct.Default
                        )
                    }
                val pair = getPaymentMode(
                    newState.amount,
                    newState.installment,
                    newState.installmentFrequency,
                    newState.paymentMethod,
                    newState.maxUpiAmountLimit
                )
                val stateAfterPaymentMode = newState.copy(
                    isUpiEnable = pair.first,
                    paymentMethod = pair.second,
                    installmentFrequencyError = null
                )
                val verifyEnable = shouldEnableVerify(stateAfterPaymentMode)
                next(
                    stateAfterPaymentMode.copy(
                        isGeneratePaymentLinkEnable = verifyEnable,
                    )
                )
            }

            is MandateAddEvent.WrongInstallmentSelected->{
                next(
                    MandateAddUSF.ShowToast(
                        ResourceManager.getInstance().getString(R.string.rp_error_wrong_installment)
                    )
                )
            }

            is MandateAddEvent.InstallmentSelected -> {
                val pair = getPaymentMode(
                    state.amount,
                    event.installment,
                    state.installmentFrequency,
                    state.paymentMethod,
                    state.maxUpiAmountLimit
                )
                val isPenaltyApplicable = isPenaltyApplicable(
                        state.amount,
                        event.installment,
                        pair.second,
                        state.minimumInstallmentAmountForPenalty
                    )
                val newState = state.copy(
                    installment = event.installment,
                    isUpiEnable = pair.first,
                    paymentMethod = pair.second,
                    installmentError = null,
                    installmentFrequencyError = null
                )
                val verifyEnable = shouldEnableVerify(newState)
                if(state.isPenaltySelected && !isPenaltyApplicable.first){
                    next(
                        newState.copy(
                            isGeneratePaymentLinkEnable = verifyEnable,
                            isPenaltySelected = false,
                            penaltyEnableError = isPenaltyApplicable.second.orEmpty()
                        ),
                        MandateAddUSF.ShowSnackBar(isPenaltyApplicable.second.orEmpty())
                    )

                }else{
                    next(
                        newState.copy(
                            isGeneratePaymentLinkEnable = verifyEnable,
                            penaltyEnableError = isPenaltyApplicable.second.orEmpty()
                        )
                    )
                }
            }

            is MandateAddEvent.StartDateSelected -> {
                val newState = state.copy(startDate = event.startDate, startDateError = null)
                val verifyEnable = shouldEnableVerify(newState)
                next(
                    newState.copy(
                        isGeneratePaymentLinkEnable = verifyEnable,
                    )
                )
            }

            is MandateAddEvent.ProgressActionClick -> {
                if (event.progressDialogStatus is ProgressDialogStatus.Success) {
                    if (state.mandate == null) {
                        noChange()
                    } else {
                        next(
                            MandateAddUSF.GotoMandateDetail(
                                state.mandate.id,
                                state.referenceId,
                                state.mandate.paymentMethodDetail.method == PaymentMethod.Manual
                            )
                        )
                    }
                } else {
                    next(MandateAddUSF.CloseProgressDialog)
                }
            }

            is MandateAddEvent.SelectContactClick -> {
                when (permissionUseCase.getProminentDisclosureStatus(PermissionType.Contact)) {
                    ProminentDisclosureStatus.Accepted -> {
                        next(MandateAddUSF.OpenContactSelection)
                    }

                    ProminentDisclosureStatus.Denied -> {
                        next(
                            MandateAddUSF.ShowProminentDisclosure(
                                ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                                ResourceManager.getInstance()
                                    .getString(R.string.rp_allow_contact_permission),
                                ResourceManager.getInstance()
                                    .getString(R.string.rp_why_we_need_contact_permission),
                                ResourceManager.getInstance().getDrawable(
                                    R.drawable.rp_ic_contact, ResourceManager.getInstance().getColor(
                                        R.color.rp_grey_6
                                    )
                                ),
                                ResourceManager.getInstance().getString(R.string.rp_allow),
                                ResourceManager.getInstance().getString(R.string.rp_dismiss)
                            )
                        )
                    }
                }
            }

            is MandateAddEvent.ProminentDisclosureAllowed -> {
                permissionUseCase.setProminentDisclosureStatus(
                    PermissionType.Contact,
                    ProminentDisclosureStatus.Accepted
                )
                next(MandateAddUSF.OpenContactSelection)
            }

            is MandateAddEvent.ProminentDisclosureDenied -> {
                next(MandateAddUSF.DismissContactSelectionDialog)
            }

            MandateAddEvent.NumberFocusChanged -> {
                noChange()
            }

            MandateAddEvent.NameFocusChanged -> {
                noChange()
            }

            MandateAddEvent.NoteFocusChanged -> {
                next(MandateAddUSF.NoteFocusChanged)
            }

            MandateAddEvent.AmountFocusChanged -> {
                next(MandateAddUSF.AmountFocusChanged)
            }


            MandateAddEvent.CustomInstallmentClick -> {
                next(MandateAddUSF.OpenCustomInstallmentDialog)
            }

            MandateAddEvent.DismissCustomInstallment -> {
                next(MandateAddUSF.DismissCustomInstallmentDialog)
            }

            is MandateAddEvent.CustomInstallmentTextChange -> {
                val amount = AmountUtils.stringToDouble(state.amount) / event.installment
                val helperText = String.format(
                    ResourceManager.getInstance().getString(
                        state.installmentFrequency?.suffix_per_ly ?: R.string.rp_per_installment_2
                    ), AmountUtils.format(amount)
                )

                val pair = getPaymentMode(
                    state.amount,
                    event.installment,
                    state.installmentFrequency,
                    state.paymentMethod,
                    state.maxUpiAmountLimit
                )
                val newState = state.copy(
                    installment = event.installment,
                    isUpiEnable = pair.first,
                    paymentMethod = pair.second
                )
                val verifyEnable = shouldEnableVerify(newState)
                next(
                    newState.copy(
                        isGeneratePaymentLinkEnable = verifyEnable,
                    ), MandateAddUSF.UpdateHelperText(helperText)
                )
            }

            is MandateAddEvent.ShowChargeDialog -> {
                next(MandateAddUSF.OpenChargeDialog(state.isCashFreeMandateUpiEnabled))
            }

            is MandateAddEvent.ContinueAnywayClick -> {
                getCreateMandateSideEffect(state.copy(upiIdError = null))
            }

            is MandateAddEvent.ChangeDateClick -> {
                next(MandateAddUSF.OpenStartDateSelection(state.installmentFrequency, state.startDate))
            }

            is MandateAddEvent.InstallmentFrequencyLoaded -> {
                next(state.copy(installmentFrequencies = event.installmentFrequencies.map {
                    ItemDialogBottomSheet(
                        it.value,
                        ResourceManager.getInstance().getString(it.suffix_ly)
                    )
                }))
            }

            is MandateAddEvent.CreateMandateNextClick -> {
                when {
                    !state.isPersonalInfoVerified -> {
                        val errorPair = getStateBasedOnPersonalDetailError(state)
                        if (errorPair.first) {
                            next(errorPair.second, MandateAddUSF.CloseKeyboard)
                        } else {
                            next(
                                errorPair.second,
                                MandateAddUSF.ShowToast(
                                    ResourceManager.getInstance()
                                        .getString(R.string.rp_please_fill_above_required_filled)
                                )
                            )
                        }
                    }

                    !state.isCollectionInfoVerified -> {
                        val errorPair = getStateBasedOnCollectionDetailError(state)
                        if (errorPair.first) {
                            if (state.paymentMethod != PaymentMethod.Manual) {
                                if (errorPair.second.paymentMethod != null
                                    && state.installmentFrequency !is InstallmentFrequency.Adhoc) {
                                    next(
                                        errorPair.second, MandateAddASF.CheckChargeAndDiscount(
                                            amount = AmountUtils.stringToDouble(errorPair.second.amount),
                                            frequency = errorPair.second.installmentFrequency!!.value,
                                            installments = errorPair.second.installment!!,
                                            chargeBearer = getChargeBearer(errorPair.second.chargeResponse, errorPair.second.isCustomerChargeBearer),
                                            paymentMethod = errorPair.second.paymentMethod!!
                                        )
                                    )
                                } else {
                                    next(errorPair.second)
                                }
                            } else {
                                getCreateMandateSideEffect(state)
                            }
                        } else {
                            next(
                                errorPair.second,
                                MandateAddUSF.ShowToast(
                                    ResourceManager.getInstance()
                                        .getString(R.string.rp_please_fill_above_required_filled)
                                )
                            )
                        }
                    }

                    state.paymentMethod != PaymentMethod.Manual && !state.isPaymentInfoVerified -> {
                        val error = getStateBasedOnPaymentDetailError(state)
                        if (error.isNullOrEmpty()) {
                            val progressDialogModel = checkStartDateCondition(state.startDate,
                                state.installmentFrequency,
                                state.paymentMethod,
                                state.isCashFreeMandateUpiEnabled)
                            if (progressDialogModel != null) {
                                next(MandateAddUSF.ShowDateChangeDialog(
                                    progressDialogModel.headerDrawable,
                                    progressDialogModel.headerBackground,
                                    progressDialogModel.title,
                                    progressDialogModel.detail,
                                    progressDialogModel.actionText,
                                    progressDialogModel.secondaryBtnText))
                            } else {
                                getCreateMandateSideEffect(state.copy(upiIdError = null))
                            }
                        } else {
                            next(
                                state.copy(upiIdError = error),
                                MandateAddUSF.ShowToast(
                                    ResourceManager.getInstance()
                                        .getString(R.string.rp_please_fill_above_required_filled)
                                )
                            )
                        }
                    }

                    else -> {
                       val progressDialogModel = checkStartDateCondition(state.startDate,
                            state.installmentFrequency,
                            state.paymentMethod,
                            state.isCashFreeMandateUpiEnabled)
                        if (progressDialogModel != null) {
                            next(MandateAddUSF.ShowDateChangeDialog(
                                progressDialogModel.headerDrawable,
                                progressDialogModel.headerBackground,
                                progressDialogModel.title,
                                progressDialogModel.detail,
                                progressDialogModel.actionText,
                                progressDialogModel.secondaryBtnText))
                        } else {
                            getCreateMandateSideEffect(state)
                        }
                    }
                }
            }

            is MandateAddEvent.ComputeCharges -> {
                next(
                    MandateAddASF.ComputeCharges(
                        amount = event.amount,
                        installments = event.installments,
                        frequency = event.frequency,
                        paymentMethod = event.paymentMethod,
                        chargeBearer = event.chargeBearer,
                        coupon = state.selectedCoupon,
                        referenceId = state.referenceId,
                        referenceType = state.referenceType
                    )
                )
            }

            is MandateAddEvent.EditPersonalInfoClick -> {
                next(
                    state.copy(
                        isPersonalInfoVerified = false,
                        isCollectionInfoVerified = false,
                        isPaymentInfoVerified = false,
                        chargeResponse = null
                    )
                )
            }

            is MandateAddEvent.EditCollectionInfoClick -> {
                next(
                    state.copy(
                        isCollectionInfoVerified = false,
                        isPaymentInfoVerified = false,
                        chargeResponse = null
                    )
                )
            }

            is MandateAddEvent.CollectInInstallmentEnabled -> {
                val newState = state.copy(
                    isCollectionInInstallmentEnabled = true,
                    installmentFrequency = null,
                    installment = null
                )
                next(newState)
            }

            is MandateAddEvent.BackPressed -> {
                if (state.isCollectionInfoVerified) {
                    next(
                        state.copy(
                            isCollectionInfoVerified = false,
                            isPaymentInfoVerified = false,
                            chargeResponse = null
                        )
                    )
                } else if (state.isPersonalInfoVerified && state.isPersonalEditEnabled) {
                    next(
                        state.copy(
                            isPersonalInfoVerified = false,
                            isCollectionInfoVerified = false,
                            isPaymentInfoVerified = false
                        )
                    )
                } else {
                    noChange()
                }
            }

            is MandateAddEvent.ChargeBearerChanged -> {
                state.isCustomerChargeBearer = !state.isCustomerChargeBearer
                next(
                    state.copy(chargeResponse = null), MandateAddASF.ComputeCharges(
                        amount = AmountUtils.stringToDouble(state.amount),
                        frequency = state.installmentFrequency!!.value,
                        installments = state.installment!!,
                        chargeBearer = getChargeBearer(state.chargeResponse, state.isCustomerChargeBearer),
                        paymentMethod = state.paymentMethod!!,
                        coupon = state.selectedCoupon,
                        referenceId = state.referenceId,
                        referenceType = state.referenceType
                    )
                )
            }

            is MandateAddEvent.MandateChargesComputed -> {
                next(
                    state.copy(chargeResponse = event.chargeResponse),
                    MandateAddUSF.CloseProgressDialog
                )
            }

            is MandateAddEvent.MandateChargesFailed -> {
                next(
                    MandateAddUSF.ShowErrorDialog(
                        ResourceManager.getInstance().getDrawable(
                            R.drawable.rp_ic_info_filled,
                            ResourceManager.getInstance().getColor(R.color.rp_red_1)
                        ),
                        ResourceManager.getInstance().getDrawable(R.color.rp_red_5),
                        event.error.message.orEmpty(),
                        ResourceManager.getInstance()
                            .getString(R.string.rp_please_contact_us_if_issue_persists),
                        ResourceManager.getInstance().getString(R.string.rp_retry),
                        null
                    )
                )
            }

            is MandateAddEvent.DismissErrorDialog -> {
                next(MandateAddUSF.DismissErrorDialog)
            }

            is MandateAddEvent.RetryComputation -> {
                next(
                    MandateAddASF.ComputeCharges(
                        amount = AmountUtils.stringToDouble(state.amount),
                        installments = state.installment!!,
                        frequency = state.installmentFrequency!!.value,
                        paymentMethod = state.paymentMethod!!,
                        chargeBearer = getChargeBearer(state.chargeResponse, state.isCustomerChargeBearer),
                        coupon = state.selectedCoupon,
                        isCouponSelectedByUser = true,
                        referenceId = state.referenceId,
                        referenceType = state.referenceType
                    ), MandateAddUSF.DismissErrorDialog
                )
            }

            is MandateAddEvent.TermsAndConditionClick -> {
                if (state.termsAndConditionUrl == null) {
                    noChange()
                } else {
                    next(MandateAddUSF.OpenLink(state.termsAndConditionUrl))
                }
            }

            is MandateAddEvent.MandateLinkClick -> {
                if (state.mandate?.mandateUrl == null) {
                    noChange()
                } else {
                    next(MandateAddUSF.OpenLink(state.mandate.mandateUrl))
                }
            }

            is MandateAddEvent.RefreshMandate -> {
                next(MandateAddASF.RefreshMandate(event.mandateId))
            }

            is MandateAddEvent.StopPolling -> {
                next(MandateAddASF.StopPolling)
            }

            is MandateAddEvent.GoToMandateDetail -> {
                next(
                    MandateAddUSF.GotoMandateDetail(
                        state.mandate?.id ?: "",
                        state.referenceId,
                        state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual
                    )
                )
            }
            is MandateAddEvent.CloseScreen -> {
                next(MandateAddUSF.CloseScreen)
            }
            is MandateAddEvent.UpdatePenaltyFlag -> {
                val isPenaltyApplicable = isPenaltyApplicable(
                    state.amount,
                    state.installment,
                    state.paymentMethod,
                    state.minimumInstallmentAmountForPenalty)
                if(isPenaltyApplicable.first){
                    next(state.copy(
                        isPenaltySelected = event.isEnabled,
                        isPenaltyAuto = event.isAuto,
                        penaltyEnableError = ""
                    ))
                }else{
                    next(
                        state.copy(penaltyEnableError = isPenaltyApplicable.second.orEmpty())
                    )
                }
            }
            is MandateAddEvent.PenaltyAmountChanged -> {
                val isValid = isPenaltyAmountValid(state.amount,
                    state.installment,
                    event.penaltyAmount,
                    state.minimumPenaltyAmount,
                    state.maximumPenaltyAmount)
                next(state.copy(
                    penaltyAmount = event.penaltyAmount,
                    penaltyAmountError = isValid.second),
                )
            }
            is MandateAddEvent.LoadConfig -> {
                next(MandateAddASF.LoadConfig)
            }
            is MandateAddEvent.ConfigLoaded -> {
                next(state.copy(
                    isPenaltyEnabled = event.isPenaltyEnabled,
                    termsAndConditionUrl = event.termsAndConditionUrl,
                    isAdhocEnabled =  event.isAdhocEnabled,
                    minimumPenaltyAmount = event.penaltyMinimumAmount.toInt(),
                    maximumPenaltyAmount = event.penaltyMaximumAmount.toInt(),
                    maxUpiAmountLimit = event.maxUpiNonMonetisedInstallmentAmount.toInt()
                ))
            }
            else -> {
                noChange()
            }
        }
    }

    private fun getCreateMandateSideEffect(state: MandateAddState): Next<MandateAddState?, MandateAddASF?, MandateAddUSF?> {
        return if (!state.isMandateCreationInitiated) {
            val chargeBearer: String?
            val chargeId: String?
            val discountId: String?
            val amount: Double
            val amountWithoutCharges: Double?
            if (state.installmentFrequency !is InstallmentFrequency.Adhoc && state.paymentMethod != PaymentMethod.Manual) {
                chargeBearer = getChargeBearer(state.chargeResponse, state.isCustomerChargeBearer)
                chargeId = state.chargeResponse?.chargeId
                discountId = state.chargeResponse?.discountId
                amount = state.chargeResponse?.amount ?: 0.0
                amountWithoutCharges = state.chargeResponse?.amountWithoutCharges
            } else {
                chargeBearer = null
                chargeId = null
                discountId = null
                amount = AmountUtils.stringToDouble(state.amount)
                amountWithoutCharges = null
            }

            next(
                state.copy(isMandateCreationInitiated = true), MandateAddASF.CreateMandate(
                    state.product.value,
                    state.name,
                    state.number,
                    state.note,
                    amount,
                    AmountUtils.stringToDouble(state.downPayment),
                    state.installment!!,
                    state.startDate!!,
                    state.installmentFrequency!!.value,
                    state.paymentMethod!!,
                    state.upiId,
                    amountWithoutCharges,
                    chargeBearer,
                    chargeId,
                    discountId,
                    AmountUtils.stringToDouble(state.amount),
                    referenceId = state.referenceId,
                    referenceType = state.referenceType,
                    state.chargeResponse,
                    financier = state.financier,
                    isPenaltyEnabled = state.isPenaltyEnabled,
                    isPenaltySelected = state.isPenaltySelected,
                    isPenaltyAuto = state.isPenaltyAuto ?: false,
                    penaltyAmount = AmountUtils.stringToDouble(state.penaltyAmount)
                ), if (state.paymentMethod == PaymentMethod.Manual) {
                    MandateAddUSF.ShowLoading(
                        ResourceManager.getInstance().getString(R.string.rp_creating_payment_schedule),
                        ResourceManager.getInstance().getString(R.string.rp_please_wait),
                    )

                } else {
                    MandateAddUSF.ShowLoading(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_link_generation_in_progress_header),
                        ResourceManager.getInstance()
                            .getString(R.string.rp_link_generation_in_progress_message),
                    )
                }
            )
        } else {
            noChange()
        }
    }

    private fun getStateBasedOnPersonalDetailError(state: MandateAddState): Pair<Boolean, MandateAddState> {
        val numberError = if (state.number.isEmpty()) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else if (!mandateUseCase.isValidMobileNumber(state.number)) {
            ResourceManager.getInstance().getString(R.string.rp_error_mobile_number)
        } else {
            null
        }

        val nameError = if (state.name.isEmpty()) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else if (!mandateUseCase.isValidName(state.name)) {
            ResourceManager.getInstance().getString(R.string.rp_error_name)
        } else {
            null
        }

        val noteError = if (state.note.isEmpty()) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else {
            null
        }

        val amountError = if (state.amount.isEmpty()) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else if (!mandateUseCase.isValidAmount(state.amount)) {
            ResourceManager.getInstance().getString(R.string.rp_error_amount)
        } else if (AmountUtils.stringToDouble(state.amount) < DataValidator.minimumAmount) {
            "*${AmountUtils.format(DataValidator.minimumAmount.toDouble())} ${
                ResourceManager.getInstance().getString(R.string.rp_minimum_installment_required)
            }"
        } else {
            null
        }

        state.isPersonalInfoVerified =
            numberError.isNullOrEmpty() && nameError.isNullOrEmpty() && noteError.isNullOrEmpty() && amountError.isNullOrEmpty()

        return Pair(
            state.isPersonalInfoVerified, state.copy(
                numberError = numberError,
                nameError = nameError,
                amountError = amountError,
                noteError = noteError
            )
        )
    }

    private fun getStateBasedOnCollectionDetailError(state: MandateAddState): Pair<Boolean, MandateAddState> {
        val startDateError = if (state.startDate == null) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else {
            null
        }

        val installmentFrequencyError = if (state.installmentFrequency == null) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else {
            null
        }
        var installmentError = if (state.installment == null) {
            ResourceManager.getInstance().getString(R.string.rp_filled_required)
        } else {
            null
        }

        if (state.installmentFrequency != null && state.installment != null) {
            val installmentAmount = AmountUtils.stringToDouble(state.amount) / state.installment
            if (installmentAmount < DataValidator.minimumAmount) {
                installmentError =
                    "*${AmountUtils.format(DataValidator.minimumAmount.toDouble())} ${
                        ResourceManager.getInstance()
                            .getString(R.string.rp_minimum_installment_required)
                    }"
            } else if(state.installment > abs(state.productWallet?.outstanding.double())){
                installmentError = ResourceManager.getInstance().getString(R.string.rp_not_enough_token_available)
            }
        }

        state.isCollectionInfoVerified = startDateError.isNullOrEmpty()
                && installmentFrequencyError.isNullOrEmpty()
                && installmentError.isNullOrEmpty()

        return Pair(
            state.isCollectionInfoVerified, state.copy(
                startDateError = startDateError,
                installmentFrequencyError = installmentFrequencyError,
                installmentError = installmentError,
            )
        )
    }

    private fun getStateBasedOnPaymentDetailError(state: MandateAddState): String? {
        val upiIdError =
            if (state.paymentMethod is PaymentMethod.Nach || state.paymentMethod is PaymentMethod.Upi) {
                null
            } else {
                ResourceManager.getInstance().getString(R.string.rp_filled_required)
            }
        state.isPaymentInfoVerified = upiIdError.isNullOrEmpty()
        return upiIdError
    }

    private fun shouldEnableVerify(state: MandateAddState): Boolean {
        return state.nameError.isNullOrEmpty() && state.name.isNotEmpty() && state.numberError.isNullOrEmpty()
                && state.number.isNotEmpty() && state.noteError.isNullOrEmpty()
                && state.note.isNotEmpty() && state.amountError.isNullOrEmpty()
                && state.amount.isNotEmpty() && state.installment != null
                && state.installmentFrequency != null && state.startDate != null
                && state.installment > abs(state.productWallet?.outstanding.double())
                && (state.paymentMethod is PaymentMethod.Nach || state.paymentMethod is PaymentMethod.Upi)
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: MandateAddASF,
        dispatchEvent: (MandateAddEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is MandateAddASF.LoadProductWallet -> {
                productUseCase.getProductWallet(ProductTypeEnum.Installment.value).collectIn(viewModelScope) {
                    dispatchEvent(MandateAddEvent.UpdateProductWallet(it))
                }
            }
            is MandateAddASF.CreateMandate -> {
                createMandate(sideEffect)
            }

            is MandateAddASF.LoadSupportedFrequency -> {
                val installmentFrequencies = InstallmentFrequency.map.values.toMutableList()
                if (!sideEffect.isAdhocEnabled) {
                    installmentFrequencies.removeAt(installmentFrequencies.lastIndex)
                }
                installmentFrequencies.removeAll { it.value == InstallmentFrequency.OneTimePayment.value }
                dispatchEvent(MandateAddEvent.InstallmentFrequencyLoaded(installmentFrequencies))
            }

            is MandateAddASF.ShareOnLinkClick -> {
                val whatsAppMessageConfig = mandateUseCase.getWhatsAppMessageConfig()
                val messageTemplate = WhatsAppMessageParserUtils.getMessageForSharePaymentLink(
                    whatsAppMessageConfig, sideEffect.mandate, CommonUseCase.getInstance().getName()
                )
                dispatchEvent(
                    MandateAddEvent.WhatsAppTemplateCreated(
                        sideEffect.mandate,
                        whatsAppMessageConfig.experiment,
                        messageTemplate,
                        sideEffect.viaSms
                    )
                )
            }

            is MandateAddASF.OpenMandatePreview -> {
                dispatchEvent(MandateAddEvent.OpenMandatePreview(sideEffect.mandate))
            }

            is MandateAddASF.CheckChargeAndDiscount -> {
                dispatchEvent(
                    MandateAddEvent.ComputeCharges(
                        amount = sideEffect.amount,
                        frequency = sideEffect.frequency,
                        installments = sideEffect.installments,
                        chargeBearer = sideEffect.chargeBearer,
                        paymentMethod = sideEffect.paymentMethod
                    )
                )
            }

            is MandateAddASF.ComputeCharges -> {
                when (val outcome = mandateUseCase.computeCharges(
                    amount = sideEffect.amount,
                    installments = sideEffect.installments,
                    frequency = sideEffect.frequency,
                    paymentMethod = sideEffect.paymentMethod,
                    bearer = sideEffect.chargeBearer,
                    coupon = sideEffect.coupon,
                    referenceId = null,
                    referenceType = null
                )) {
                    is Outcome.Error -> {
                        dispatchEvent(MandateAddEvent.MandateChargesFailed(outcome.error))
                    }

                    is Outcome.Success -> {
                        dispatchEvent(
                            MandateAddEvent.MandateChargesComputed(
                                outcome.data,
                                sideEffect.coupon,
                                sideEffect.isCouponSelectedByUser
                            )
                        )
                    }
                }
            }
            is MandateAddASF.StartPolling -> {
                if (job == null) {
                    job = viewModelScope.launch {
                        channelFlow {
                            while (!isClosedForSend) {
                                delay(5000)
                                send(dispatchEvent(MandateAddEvent.RefreshMandate(sideEffect.mandateId)))
                            }
                        }.flowOn(Dispatchers.IO).collect {

                        }
                    }
                }
            }

            is MandateAddASF.StopPolling -> {
                job?.cancel()
            }

            is MandateAddASF.RefreshMandate -> {
                when (val outcome = mandateUseCase.refreshMandate(sideEffect.mandateId)) {
                    is Outcome.Success -> {
                        if (outcome.data.state != MandateState.Pending) {
                            dispatchEvent(MandateAddEvent.StopPolling)
                            dispatchEvent(MandateAddEvent.GoToMandateDetail)
                        }
                    }

                    is Outcome.Error -> {

                    }
                }
            }
            is MandateAddASF.LoadConfig -> {
                propertyUseCase.getMultiplePropertyLive(listOf(
                    PropertyUtils.IS_PENALTY_ENABLED,
                    PropertyUtils.PENALTY_MINIMUM_AMOUNT,
                    PropertyUtils.PENALTY_MAXIMUM_AMOUNT,
                    PropertyUtils.IS_ADHOC_ENABLED,
                    PropertyUtils.TERMS_AND_CONDITION_URL,
                    PropertyUtils.MAX_UPI_NON_MONETISED_INSTALLMENT_AMOUNT
                )).collectIn(viewModelScope){
                    val isPenaltyEnabled = it.find { it?.id == PropertyUtils.IS_PENALTY_ENABLED }?.value.toBoolean()
                    val isAdhocEnabled = it.find { it?.id == PropertyUtils.IS_ADHOC_ENABLED }?.value.toBoolean()
                    val penaltyMinimumAmount = it.find { it?.id == PropertyUtils.PENALTY_MINIMUM_AMOUNT }?.value.double()
                    val penaltyMaximumAmount = it.find { it?.id == PropertyUtils.PENALTY_MAXIMUM_AMOUNT }?.value.double()
                    val termsAndConditionUrl = it.find { it?.id == PropertyUtils.TERMS_AND_CONDITION_URL }?.value.orEmpty()
                    val maxUpiNonMonetisedInstallmentAmount = it.find { it?.id == PropertyUtils.MAX_UPI_NON_MONETISED_INSTALLMENT_AMOUNT }?.value.double()
                    dispatchEvent(MandateAddEvent.ConfigLoaded(
                        isPenaltyEnabled = isPenaltyEnabled,
                        isAdhocEnabled = isAdhocEnabled,
                        penaltyMinimumAmount = penaltyMinimumAmount,
                        penaltyMaximumAmount = penaltyMaximumAmount,
                        termsAndConditionUrl = termsAndConditionUrl,
                        maxUpiNonMonetisedInstallmentAmount = maxUpiNonMonetisedInstallmentAmount
                    ))
                }
            }
        }
    }

    private suspend fun createMandate(sideEffect: MandateAddASF.CreateMandate) {
        when (val outcome = mandateUseCase.createMandate(
            name = sideEffect.name,
            mobileNumber = sideEffect.number,
            description = sideEffect.note,
            amount = sideEffect.amount,
            installments = sideEffect.installments,
            startDate = sideEffect.startDate,
            frequency = sideEffect.frequency,
            paymentMethod = sideEffect.paymentMethod,
            upiId = sideEffect.upiId,
            product = sideEffect.product,
            bearer = sideEffect.bearer,
            chargeId = sideEffect.chargeId,
            discountId = sideEffect.discountId,
            amountWithoutCharges = sideEffect.amountWithoutCharges,
            originalAmount = sideEffect.originalAmount,
            referenceId = sideEffect.referenceId,
            referenceType = sideEffect.referenceType,
            chargeResponseDto = sideEffect.chargeResponse,
            penaltyMetaDto = PenaltyMetaDto(
                    isEnabled = sideEffect.isPenaltySelected,
                    isAuto = sideEffect.isPenaltyAuto,
                    amountInRs = if (sideEffect.isPenaltyAuto) {
                        sideEffect.penaltyAmount
                    } else {
                        null
                    }
                ),
            retryInstallmentMetaDto = RetryInstallmentMetaDto(isAuto = false)
        )) {
            is Outcome.Error -> {
                dispatchEvent(MandateAddEvent.UnableToGeneratePaymentLink(outcome.error.message.orEmpty()))
            }

            is Outcome.Success -> {
                dispatchEvent(
                    MandateAddEvent.PaymentLinkGenerated(
                        outcome.data,
                        CommonUseCase.getInstance().getMobileNumber()
                    )
                )
            }
        }
    }

    companion object {
        fun getPaymentMode(
            amount: String,
            installment: Int?,
            installmentFrequency: InstallmentFrequency?,
            paymentMethod: PaymentMethod?,
            maxUpiAmountLimit: Int
        ): Pair<Boolean, PaymentMethod?> {
            return if (paymentMethod != PaymentMethod.Manual) {
                val isUpiEnable = AmountUtils.stringToDouble(amount).div(
                    installment ?: 1
                ) <= maxUpiAmountLimit && installmentFrequency !is InstallmentFrequency.Adhoc
                val paymentMethod = if (isUpiEnable) {
                    PaymentMethod.Upi
                } else {
                    null
                }
                Pair(isUpiEnable, paymentMethod)
            } else {
                Pair(false, PaymentMethod.Manual)
            }
        }

        fun isPenaltyApplicable(
            amount: String,
            installment: Int?,
            paymentMethod: PaymentMethod?,
            minimumInstallmentForCharges: Int
        ): Pair<Boolean, String?> {
            return if(paymentMethod != null) {
                if (paymentMethod == PaymentMethod.Upi) {
                    if (installment != null) {
                        val installmentAmount = AmountUtils.stringToDouble(amount) / installment
                        if (installmentAmount >= minimumInstallmentForCharges) {
                            true to null
                        } else {
                            false to ResourceManager.getInstance().getString(
                                R.string.rp_automatic_charge_penalty_is_not_available_for_amount_less_than,
                                AmountUtils.format(minimumInstallmentForCharges.toDouble())
                            )
                        }
                    } else {
                        true to null
                    }
                } else {
                    false to ResourceManager.getInstance().getString(
                        R.string.rp_automatic_charge_penalty_is_not_available_for_nach_payments,
                        paymentMethod?.transalion
                    )
                }
            }else{
                false to null
            }
        }

        fun isPenaltyAmountValid(
            amount: String,
            installment: Int?,
            penaltyAmount: String,
            minimumPenaltyAmount: Int,
            maximumPenaltyAmount: Int
        ): Pair<Boolean, String?> {
            val penaltyAmount = AmountUtils.stringToDouble(penaltyAmount)
            val installmentAmount = AmountUtils.stringToDouble(amount) / (installment ?: 1)
            val maxPenaltyAmount = minOf(maximumPenaltyAmount.toDouble(), installmentAmount)
            return if(penaltyAmount < minimumPenaltyAmount){
                false to ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_not_be_less_than,
                    AmountUtils.format(minimumPenaltyAmount.toDouble()))
            }else if(penaltyAmount > maxPenaltyAmount){
                false to ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_not_be_greater_than,
                    AmountUtils.format(maxPenaltyAmount))
            }else{
                true to null
            }
        }

        fun getChargeBearer(chargeResponseDto: ChargeResponseDto?, isCustomerChargeBearer: Boolean): String {
            return if (chargeResponseDto?.isBearerControlAvailable == true && isCustomerChargeBearer) {
                ChargeBearer.Customer.value
            } else {
                chargeResponseDto?.chargeBearer ?: ChargeBearer.Both.value
            }
        }

        fun checkStartDateCondition(
            startDate: Long?,
            installmentFrequency: InstallmentFrequency?,
            paymentMethod: PaymentMethod?,
            isCashFreeMandateUpiEnabled: Boolean): ProgressDialogModel?{
            if (startDate != null) {
                if (paymentMethod is PaymentMethod.Nach && shouldShowNachDateChangeDialog(startDate)) {
                   return ProgressDialogModel(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle),
                        ResourceManager.getInstance().getDrawable(R.color.rp_orange_1),
                        ResourceManager.getInstance().getString(R.string.rp_change_date_header),
                        ResourceManager.getInstance().getString(R.string.rp_change_date_detail),
                        ResourceManager.getInstance().getString(R.string.rp_change_date),
                        ResourceManager.getInstance().getString(R.string.rp_continue_anyway)
                    )
                }
            }
            return null
        }

        private fun shouldShowNachDateChangeDialog(startDate: Long): Boolean {
            return startDate < (System.currentTimeMillis() + (2 * TimeConstant.DAY))
        }

    }

}

