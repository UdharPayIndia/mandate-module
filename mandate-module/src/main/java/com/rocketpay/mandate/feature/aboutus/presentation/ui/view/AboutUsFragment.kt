package com.rocketpay.mandate.feature.aboutus.presentation.ui.view

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentAboutUsRpBinding
import com.rocketpay.mandate.feature.aboutus.presentation.injection.AboutUsComponent
import com.rocketpay.mandate.feature.aboutus.presentation.injection.AboutUsStateMachineFactory
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsEvent
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsState
import com.udharpay.feature.aboutus.presentation.ui.statemachine.AboutUsStateMachine
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsUSF
import com.rocketpay.mandate.feature.aboutus.presentation.ui.viewmodel.AboutUsUM
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewScreen
import com.rocketpay.mandate.feature.webview.presentation.ui.view.GenericWebviewFragment
import javax.inject.Inject

internal class AboutUsFragment : BaseMainFragment<AboutUsEvent, AboutUsState, AboutUsUSF>() {

    private lateinit var binding: FragmentAboutUsRpBinding
    private lateinit var vm: AboutUsUM
    @Inject lateinit var aboutUsStateMachineFactory: AboutUsStateMachineFactory

    companion object {
        fun newInstance(bundle: Bundle?): AboutUsFragment {
            val aboutUsFragment = AboutUsFragment()
            aboutUsFragment.arguments = bundle
            return aboutUsFragment
        }
    }

    override fun injection() {
        super.injection()
        AboutUsComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, aboutUsStateMachineFactory)[AboutUsStateMachine::class.java]
        vm = AboutUsUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(AboutUsEvent.LoadUrls)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAboutUsRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_about_collectpe))
        setupToolbar(vm)
        binding.vm = vm
    }

    override fun handleState(state: AboutUsState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: AboutUsUSF) {
        when (sideEffect) {
            is AboutUsUSF.OpenUrl -> {
                val bundle = Bundle()
                bundle.putString(GenericWebviewFragment.URL, sideEffect.url)
                bundle.putBoolean(GenericWebviewFragment.SHOULD_ENABLE_CACHE, true)
                bundle.putString(GenericWebviewFragment.TOOLBAR_TITLE, sideEffect.title)
                listener?.onNavigate(GenericWebviewFragment.newInstance(bundle), fragmentTag = GenericWebviewScreen.name)
            }
        }
    }
}
