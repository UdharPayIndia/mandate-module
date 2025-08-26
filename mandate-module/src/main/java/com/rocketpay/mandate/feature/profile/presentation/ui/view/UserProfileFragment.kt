package com.rocketpay.mandate.feature.profile.presentation.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentUserProfileRpBinding
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.view.BankAccountListFragment
import com.rocketpay.mandate.feature.business.presentation.ui.view.BusinessProfileFragment
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycScreen
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view.KycFragment
import com.rocketpay.mandate.feature.profile.presentation.injection.ProfileComponent
import com.rocketpay.mandate.feature.profile.presentation.injection.ProfileVMFactory
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileEvent
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileState
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileStateMachine
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileUSF
import com.rocketpay.mandate.feature.profile.presentation.ui.viewmodel.UserProfileUM
import javax.inject.Inject

internal class UserProfileFragment : BaseMainFragment<UserProfileEvent, UserProfileState, UserProfileUSF>(){

    private lateinit var binding: FragmentUserProfileRpBinding
    private lateinit var vm: UserProfileUM
    @Inject
    internal lateinit var profileVMFactory: ProfileVMFactory

    companion object {
        fun newInstance(bundle: Bundle?): UserProfileFragment {
            val userProfileFragment = UserProfileFragment()
            userProfileFragment.arguments = bundle
            return userProfileFragment
        }
    }

    override fun injection() {
        super.injection()
        ProfileComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, profileVMFactory)[UserProfileStateMachine::class.java]
        vm = UserProfileUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(UserProfileEvent.LoadUserProfile)
        stateMachine.dispatchEvent(UserProfileEvent.LoadBusinessProfile)
        stateMachine.dispatchEvent(UserProfileEvent.LoadKyc)
        stateMachine.dispatchEvent(UserProfileEvent.RefreshKyc)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentUserProfileRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_my_profile))
        setupToolbar(vm)
        binding.vm = vm
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun handleState(state: UserProfileState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: UserProfileUSF) {
        when (sideEffect) {
            is UserProfileUSF.GotoBusinessProfile -> {
                listener?.onNavigate(BusinessProfileFragment.newInstance(null), fragmentTag = BankAccountListScreen.name)
            }
            is UserProfileUSF.OpenBankAccountList -> {
                listener?.onNavigate(BankAccountListFragment.newInstance(null), fragmentTag = BankAccountListScreen.name)
            }
            is UserProfileUSF.GotoKyc -> {
                listener?.onNavigate(KycFragment.newInstance(null), fragmentTag = KycScreen.name)
            }
        }
    }

}
