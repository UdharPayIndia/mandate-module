package com.rocketpay.mandate.feature.webview.presentation.ui.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.GlobalEvent
import com.rocketpay.mandate.common.basemodule.common.eventbus.GlobalEventBus
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentGenericWebviewRpBinding
import com.rocketpay.mandate.feature.webview.presentation.injection.GenericWebviewComponent
import com.rocketpay.mandate.feature.webview.presentation.injection.GenericWebviewStateMachineFactory
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.BaseWebViewAction
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebViewBridgeData
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewClient
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewEvent
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewJsHandler
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewSimpleStateMachine
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewState
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewUSF
import com.rocketpay.mandate.feature.webview.presentation.ui.viewmodel.GenericWebViewCommunicatorVM
import com.rocketpay.mandate.feature.webview.presentation.ui.viewmodel.GenericWebviewUM
import com.rocketpay.mandate.main.init.MandateManager
import javax.inject.Inject

internal class GenericWebviewFragment : BaseMainFragment<GenericWebviewEvent, GenericWebviewState, GenericWebviewUSF>() {

    private var shouldExitFragment = false
    private lateinit var binding: FragmentGenericWebviewRpBinding
    private lateinit var vm: GenericWebviewUM
    @Inject lateinit var genericWebviewStateMachineFactory: GenericWebviewStateMachineFactory
    private var communicatorVM : GenericWebViewCommunicatorVM? = null
    private var showContact: Boolean = false
    private var bloackScreenshot: Boolean = false

    companion object {
        const val BRIDGE_CALLED = "bridge_called"

        const val URL = "url"
        const val TOOLBAR_TITLE = "toolbar_title"
        const val BLOCK_SCREENSHOT = "block_screenshot"
        const val SHOULD_ENABLE_CACHE = "should_enable_cache"
        const val SHOW_CONTACT = "show_contact"

        const val WEB_VIEW_CLOSED = "web_view_closed"

        fun newInstance(bundle: Bundle?): GenericWebviewFragment {
            val genericWebviewFragment = GenericWebviewFragment()
            genericWebviewFragment.arguments = bundle
            return genericWebviewFragment
        }
    }

    override fun injection() {
        super.injection()
        GenericWebviewComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, genericWebviewStateMachineFactory)[GenericWebviewSimpleStateMachine::class.java]
        if(parentFragment != null) {
            communicatorVM =
                ViewModelProvider(requireParentFragment())[GenericWebViewCommunicatorVM::class.java]
        }
        vm = GenericWebviewUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val url = savedInstanceState?.getString(URL)
        val toolbarTitle = savedInstanceState?.getString(TOOLBAR_TITLE)
        val shouldEnableCache = savedInstanceState?.getBoolean(SHOULD_ENABLE_CACHE)
        bloackScreenshot = savedInstanceState?.getBoolean(BLOCK_SCREENSHOT) ?: false
        showContact = savedInstanceState?.getString(SHOW_CONTACT).toBoolean()
        stateMachine.dispatchEvent(GenericWebviewEvent.LoadPage(url))
        stateMachine.dispatchEvent(GenericWebviewEvent.LoadUi(toolbarTitle, shouldEnableCache))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGenericWebviewRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        if(bloackScreenshot) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm
        setView()
    }

    override fun registerListener() {
        super.registerListener()
        communicatorVM?.communicateToWebView?.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()){
            binding.webview.post {
                binding.webview.evaluateJavascript("javascript: "
                        + "GenericWebviewJsHandler.callbackBalanceCheck" + "(${it})", null)
                }
                communicatorVM?.communicateToWebView?.postValue(null)
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.webview.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
        }
        binding.webview.apply {
            settings.javaScriptEnabled = true
            settings.allowFileAccess = true
            settings.domStorageEnabled = true
            settings.textSize = WebSettings.TextSize.NORMAL
            setInitialScale(1)
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            addJavascriptInterface(
                GenericWebviewJsHandler ({ action: String, data: String?, secret: String? -> stateMachine.dispatchEvent(GenericWebviewEvent.HandleAction(action, data, secret)) },
                    { stateMachine.dispatchEvent(GenericWebviewEvent.HandleAction("close", null, null)) }),
                GenericWebviewJsHandler.TAG
            )
            webViewClient = GenericWebviewClient(requireContext())
        }

        if (MandateManager.getInstance().isDebug()) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        shouldExitFragment = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun handleState(state: GenericWebviewState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: GenericWebviewUSF) {
        when (sideEffect) {
            is GenericWebviewUSF.LoadUrl -> {
                binding.webview.loadUrl(sideEffect.url)
            }
            is GenericWebviewUSF.LoadUi -> {
                if (sideEffect.shouldEnableCache) {
                    binding.webview.settings.cacheMode = WebSettings.LOAD_DEFAULT
                } else {
                    binding.webview.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
            }
            is GenericWebviewUSF.HandleSpecificAction -> {
                when(BaseWebViewAction.get(sideEffect.action)) {
                    BaseWebViewAction.Close -> handleClose()
                    else -> GlobalEventBus.fire(
                        GlobalEvent(
                            BRIDGE_CALLED,
                            GenericWebViewBridgeData(sideEffect.action, sideEffect.data)
                        )
                    )
                }
            }
        }
    }

    private fun handleClose() {
        shouldExitFragment = true
        onBackPress()
    }

    override fun isBackPressHandled(): Boolean {
        return if (shouldExitFragment) {
            FragmentResultBus.fire(WEB_VIEW_CLOSED, true)
            false
        } else if (binding.webview.canGoBack()) {
            binding.webview.goBack()
            true
        } else {
           handleContactSupport()
        }
    }

    private fun handleContactSupport(): Boolean{
        FragmentResultBus.fire(WEB_VIEW_CLOSED, true)
        return false
    }

    override fun deInitView() {
        super.deInitView()
        binding.webview.removeJavascriptInterface(GenericWebviewJsHandler.TAG)
    }
}
