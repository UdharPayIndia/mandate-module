package com.rocketpay.mandate.feature.image.presentation.ui.view.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.databinding.FragmentImageViewerRpBinding
import com.rocketpay.mandate.feature.image.presentation.injection.ImageComponent
import com.rocketpay.mandate.feature.image.presentation.injection.ImageStateMachineFactory
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerEvent
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerSimpleStateMachine
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerState
import com.rocketpay.mandate.feature.image.presentation.ui.view.statemachinde.ImageViewerUSF
import com.rocketpay.mandate.feature.image.presentation.ui.view.viewmodel.ImageViewerUM
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import javax.inject.Inject

internal class ImageViewerFragment : BaseMainFragment<ImageViewerEvent, ImageViewerState, ImageViewerUSF>() {

    private lateinit var binding: FragmentImageViewerRpBinding
    private lateinit var vm: ImageViewerUM
    @Inject
    lateinit var imageStateMachineFactory: ImageStateMachineFactory

    companion object {
        const val BUNDLE_URL = "BUNDLE_URL"
        fun newInstance(bundle: Bundle?): ImageViewerFragment {
            val imageViewerFragment = ImageViewerFragment()
            imageViewerFragment.arguments = bundle
            return imageViewerFragment
        }
    }

    override fun injection() {
        super.injection()
        ImageComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, imageStateMachineFactory)[ImageViewerSimpleStateMachine::class.java]
        vm = ImageViewerUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val url = savedInstanceState?.getString(BUNDLE_URL) ?: ""
        stateMachine.dispatchEvent(ImageViewerEvent.LoadData(url))
    }

    override fun handleState(state: ImageViewerState) {
        vm.handleState(state)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentImageViewerRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }

    override fun handleUiSideEffect(sideEffect: ImageViewerUSF) {
        when (sideEffect) {
            is ImageViewerUSF.CloseScreen -> {
                onBackPress()
            }
        }
    }
}
