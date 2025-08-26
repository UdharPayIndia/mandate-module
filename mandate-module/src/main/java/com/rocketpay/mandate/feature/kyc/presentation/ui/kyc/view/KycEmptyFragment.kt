package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.rocketpay.mandate.databinding.FragmentEmptyKycRpBinding

internal class KycEmptyFragment : Fragment() {

    private lateinit var binding: FragmentEmptyKycRpBinding

    companion object {
        fun newInstance(bundle: Bundle?): KycEmptyFragment {
            val kycFragment = KycEmptyFragment()
            kycFragment.arguments = bundle
            return kycFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmptyKycRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    }
}
