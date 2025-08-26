package com.rocketpay.mandate.common.basemodule.common

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.rocketpay.mandate.R
import org.json.JSONObject

internal interface BaseFragmentListener {

        fun onNavigate(fragment: Fragment, addToBackStack: Boolean = true, isFragmentToBeAdded: Boolean = false, enterAnimation: Int = R.anim.rp_fade_in, exitAnimation: Int = R.anim.rp_fade_out, fragmentTag: String)

        fun attacheChildFragment(fm: FragmentManager, fragment: Fragment, addToBackStack: Boolean, containerId: Int, tag: String, forceRefresh: Boolean = false)

        fun clearBackStack()

        fun onBackPressed()

        fun updateStatusBar(colorId: Int)

        fun updateToolbar(toolbar: Toolbar)

        fun onShowSnackBar(description: String, ctaText: String, ctaColor: Int, navFragment: Fragment, eventDes: String, parameterName: JSONObject)
}
