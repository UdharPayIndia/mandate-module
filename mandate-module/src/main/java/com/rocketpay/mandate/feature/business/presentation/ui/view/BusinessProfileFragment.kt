package com.rocketpay.mandate.feature.business.presentation.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.DialogBottomSheetSelection
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentBusinessProfileRpBinding
import com.rocketpay.mandate.feature.business.presentation.injection.BusinessProfileComponent
import com.rocketpay.mandate.feature.business.presentation.injection.BusinessProfileStateMachineFactory
import com.rocketpay.mandate.feature.business.presentation.ui.adapter.BusinessProfileAdapter
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileEvent
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileState
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileStateMachine
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileUSF
import com.rocketpay.mandate.feature.business.presentation.ui.viewmodel.BusinessProfileUM
import javax.inject.Inject

internal class BusinessProfileFragment : BaseMainFragment<BusinessProfileEvent, BusinessProfileState, BusinessProfileUSF>() {

    private lateinit var binding: FragmentBusinessProfileRpBinding
    private lateinit var businessProfileAdapter: BusinessProfileAdapter
    private lateinit var vm: BusinessProfileUM
    @Inject
    internal lateinit var businessProfileStateMachineFactory: BusinessProfileStateMachineFactory
    private var businessFieldSelection: DialogBottomSheetSelection? = null

    companion object {
        const val BUSINESS_INFO_ADDED = "business_info_added"

        fun newInstance(bundle: Bundle?): BusinessProfileFragment {
            val businessProfileFragment = BusinessProfileFragment()
            businessProfileFragment.arguments = bundle
            return businessProfileFragment
        }
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

    override fun injection() {
        super.injection()
        BusinessProfileComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, businessProfileStateMachineFactory)[BusinessProfileStateMachine::class.java]
        vm = BusinessProfileUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(BusinessProfileEvent.LoadBusinessProfileData)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBusinessProfileRpBinding.inflate(inflater, container, false)
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
        businessProfileAdapter = BusinessProfileAdapter()
        businessProfileAdapter.dispatchEvent = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = businessProfileAdapter
    }

    override fun handleState(state: BusinessProfileState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: BusinessProfileUSF) {
        when (sideEffect) {
            is BusinessProfileUSF.UpdateFields -> {
                businessProfileAdapter.businessProperties.value = sideEffect.businessProperties
                businessProfileAdapter.swapData(sideEffect.businessFields)
            }
            is BusinessProfileUSF.UpdateBusinessProperties -> {
                businessProfileAdapter.businessProperties.value = sideEffect.businessProperties
            }
            BusinessProfileUSF.BusinessProfileAdded -> {
                FragmentResultBus.fire(BUSINESS_INFO_ADDED, true)
                onBackPress()
            }
            is BusinessProfileUSF.OpenFieldValueSelection -> {
                businessFieldSelection = DialogBottomSheetSelection(
                    requireContext(),
                    sideEffect.businessField.title,
                    sideEffect.businessFieldType
                ) { itemDialogSelection ->
                    businessFieldSelection?.dismiss()
                    stateMachine.dispatchEvent(BusinessProfileEvent.BusinessFieldSelected(sideEffect.businessField.type, itemDialogSelection.type))
                }
                val itemDialogBottomSheets = sideEffect.businessField.type_values.map {
                    ItemDialogBottomSheet(
                        type = it.type,
                        prefix = it.title
                    )
                }
                businessFieldSelection?.updateList(itemDialogBottomSheets)
                businessFieldSelection?.show()
            }
        }
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}
