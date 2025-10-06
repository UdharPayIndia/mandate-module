package com.rocketpay.mandate.main.presentation.view

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.BackPressListener
import com.rocketpay.mandate.common.basemodule.common.BaseFragmentListener
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.ifNullOrEmpty
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.UiUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.bankaccount.data.BankAccountSyncer
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.view.BankAccountListFragment
import com.rocketpay.mandate.feature.business.data.BusinessPropertySyncer
import com.rocketpay.mandate.feature.business.data.EnterprisePropertySyncer
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.view.PaymentTrackerMainFragment
import com.rocketpay.mandate.feature.kyc.data.KycSyncer
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycScreen
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view.KycFragment
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginScreen
import com.rocketpay.mandate.feature.login.presentation.ui.login.view.LoginFragment
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view.MandateAddFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.view.MandateListFragment
import com.rocketpay.mandate.feature.product.data.ProductOrderSyncer
import com.rocketpay.mandate.feature.product.data.ProductWalletSyncer
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainScreen
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.view.SettlementMainFragment
import com.rocketpay.mandate.main.GlobalState
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.main.presentation.injection.RpMainComponent
import com.rocketpay.mandate.main.presentation.injection.RpMainVMFactory
import com.rocketpay.mandate.main.presentation.viewmodel.RpMainVM
import org.json.JSONObject
import javax.inject.Inject

internal class RpMainActivity : AppCompatActivity(), BaseFragmentListener {

    private lateinit var mViewModel: RpMainVM
    @Inject
    internal lateinit var mVMFactory: RpMainVMFactory

    private var fragment: Fragment? = null
    private var insetView: View? = null

    companion object{
        const val BUNDLE_FLOW = "BUNDLE_FLOW"
        const val BUNDLE_REFERENCE_ID = "BUNDLE_REFERENCE_ID"
        const val FLOW_MANDATE_ADDITION = "FLOW_MANDATE_ADDITION"
        const val FLOW_MANDATE_DETAIL = "FLOW_MANDATE_DETAIL"
        const val FLOW_PAYMENT_TRACKER = "FLOW_PAYMENT_TRACKER"
        const val FLOW_SETTLEMENT = "FLOW_SETTLEMENT"
        const val FLOW_KYC = "FLOW_KYC"
        const val FLOW_BANK_ACCOUNT_LIST = "FLOW_BANK_ACCOUNT_LIST"
        const val FLOW_BANK_ACCOUNT_ADD = "FLOW_BANK_ACCOUNT_ADD"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.RpMainScreen)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RpMainComponent.Initializer.init().inject(this)
        mViewModel = ViewModelProvider(this, mVMFactory)[RpMainVM::class.java]
        retrieveExtras()
        setContentView(R.layout.activity_main_rp)
        registerBackStackChangedListener()
        initSync()
        initValueFromRemoteConfig {
            if (savedInstanceState == null) {
                launchFragment()
            }
        }
        initListener()
    }

    private fun retrieveExtras(){
        mViewModel.flowType = intent.getStringExtra(BUNDLE_FLOW).toString()
        mViewModel.referenceId = intent.getStringExtra(BUNDLE_REFERENCE_ID).toString()
        mViewModel.customerName = intent.getStringExtra(MandateAddFragment.BUNDLE_CUSTOMER_NAME).toString()
        mViewModel.customerNumber = intent.getStringExtra(MandateAddFragment.BUNDLE_CUSTOMER_NUMBER).toString()
        mViewModel.note = intent.getStringExtra(MandateAddFragment.BUNDLE_NOTE).toString()
        mViewModel.amount = intent.getLongExtra(MandateAddFragment.BUNDLE_AMOUNT, 0)
    }


    override fun onStart() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, windowInsets ->
            this.insetView = view
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
            setInsetBackgroundColor(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        super.onStart()

        FragmentResultBus.register(LoginFragment.RESULT_LOGGED_IN) {
            val data = it as Boolean?
            if(data == true && mViewModel.flowType != FLOW_KYC) {
                handleMandateRedirection()
            }else{
                launchFragment()
            }
        }
        FragmentResultBus.register(KycFragment.UPDATE_KYC_STATE) {
            launchFragment()
        }
    }

    private fun initValueFromRemoteConfig(launchFragmentA: () -> Unit) {
        launchFragmentA()
    }

    private fun initSync() {
        SyncManager.getInstance().enqueue(BankAccountSyncer.TYPE)
        SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
        SyncManager.getInstance().enqueue(PaymentOrderSyncer.TYPE)
        SyncManager.getInstance().enqueue(KycSyncer.TYPE)
        SyncManager.getInstance().enqueue(ProductWalletSyncer.TYPE)
        SyncManager.getInstance().enqueue(ProductOrderSyncer.TYPE)
        SyncManager.getInstance().enqueue(EnterprisePropertySyncer.TYPE)
        SyncManager.getInstance().enqueue(BusinessPropertySyncer.TYPE)
    }

    private fun launchFragment() {
        when{
            !mViewModel.isLoggedIn() -> {
                onNavigate(LoginFragment.newInstance(null), fragmentTag = LoginScreen.name, addToBackStack = false)
            }
            (!mViewModel.isKycCompleted() && !MandateManager.getInstance().skipKyc()) || mViewModel.flowType == FLOW_KYC  -> {
                GlobalState.isLogin.value = true
                val bundle = Bundle()
                bundle.putBoolean(KycFragment.BUNDLE_IS_ONBOARDING, mViewModel.flowType != FLOW_KYC)
                onNavigate(KycFragment.newInstance(bundle), fragmentTag = KycScreen.name,
                    addToBackStack = mViewModel.flowType == FLOW_KYC)
            }
            else -> {
                handleMandateRedirection()
            }
        }
    }

    private fun handleMandateRedirection(){
        GlobalState.isLogin.value = true
        when{
            mViewModel.flowType == FLOW_MANDATE_DETAIL && mViewModel.referenceId.isNotEmpty() -> {
                val bundle = Bundle()
                bundle.putString(MandateDetailFragment.BUNDLE_SUPER_KEY_ID, mViewModel.referenceId)
                onNavigate(MandateDetailFragment.newInstance(bundle), fragmentTag = MandateDetailScreen.name)
            }
            mViewModel.flowType == FLOW_MANDATE_ADDITION -> {
                val bundle = Bundle()
                bundle.putString(MandateAddFragment.BUNDLE_REFERENCE_ID, mViewModel.referenceId)
                bundle.putString(MandateAddFragment.BUNDLE_REFERENCE_TYPE, PropertyUtils.MAIN_SDK)
                bundle.putString(MandateAddFragment.BUNDLE_CUSTOMER_NAME, mViewModel.customerName)
                bundle.putString(MandateAddFragment.BUNDLE_CUSTOMER_NUMBER, mViewModel.customerNumber)
                bundle.putString(MandateAddFragment.BUNDLE_NOTE, mViewModel.note)
                bundle.putDouble(MandateAddFragment.BUNDLE_AMOUNT, mViewModel.amount.toDouble())
                onNavigate(MandateAddFragment.newInstance(bundle), fragmentTag = MandateAddScreen.name)
            }
            mViewModel.flowType == FLOW_PAYMENT_TRACKER -> {
                onNavigate(PaymentTrackerMainFragment.newInstance(null), fragmentTag = PaymentTrackerMainScreen.name)
            }
            mViewModel.flowType == FLOW_SETTLEMENT -> {
                onNavigate(SettlementMainFragment.newInstance(null), fragmentTag = SettlementMainScreen.name)
            }
            mViewModel.flowType == FLOW_BANK_ACCOUNT_LIST -> {
                onNavigate(BankAccountListFragment.newInstance(null), fragmentTag = BankAccountListScreen.name)
            }
            mViewModel.flowType == FLOW_BANK_ACCOUNT_ADD -> {
                val bundle = Bundle()
                bundle.putBoolean(BankAccountAddFragment.IS_FROM_ONBOARDING, false)
                bundle.putString(BankAccountAddFragment.BUNDLE_SOURCE, PropertyUtils.MAIN_SDK)
                onNavigate(BankAccountAddFragment.newInstance(bundle), fragmentTag = BankAccountAddScreen.name)
            }
            else -> {
                onNavigate(MandateListFragment.newInstance(null), fragmentTag = MandateListScreen.name)
            }
        }
    }

    private fun initListener() {
        GlobalState.isReportStatus.collectIn(lifecycleScope){ data ->
            when(data.first){
                ProgressDialogStatus.Progress -> {
                    if(UiUtils.isActivityInCorrectState(this)) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            data.second.ifNullOrEmpty(
                                ResourceManager.getInstance()
                                    .getString(R.string.rp_download_report_has_been_initiated)
                            ),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    GlobalState.isReportStatus.value = ProgressDialogStatus.Init to null
                }
                ProgressDialogStatus.Error -> {
                    if(UiUtils.isActivityInCorrectState(this)) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            data.second.ifNullOrEmpty(
                                ResourceManager.getInstance()
                                    .getString(R.string.rp_report_generation_failed)
                            ),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    GlobalState.isReportStatus.value = ProgressDialogStatus.Init to null
                }
                ProgressDialogStatus.Success -> {
                    if(UiUtils.isActivityInCorrectState(this)) {
                        val snackbar = Snackbar.make(
                            findViewById(android.R.id.content),
                            ResourceManager.getInstance()
                                .getString(R.string.rp_report_generated_successfully),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setAction(
                            ResourceManager.getInstance().getString(R.string.rp_open),
                            {
                                data.second?.let {
                                    FileUtils.openFile(this, it.toUri())
                                }
                            })
                        snackbar.show()

                    }
                    GlobalState.isReportStatus.value = ProgressDialogStatus.Init to null
                }
                else ->{

                }
            }
        }
    }


    private fun registerBackStackChangedListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.fragments.isNotEmpty()) {
                fragment = supportFragmentManager.fragments.last()
            }
        }
    }

    override fun updateStatusBar(colorId: Int) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.statusBarColor = colorId
        setInsetBackgroundColor(colorId)
    }

    private fun setInsetBackgroundColor(colorId: Int) {
        try {
            val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    colorId,
                    ResourceManager.getInstance().getColor(R.color.rp_grey_6)
                )
            )
            insetView?.background = gd
        }catch (ex: Exception){

        }
    }

    override fun updateToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
    }

    override fun onShowSnackBar(
        description: String,
        ctaText: String,
        ctaColor: Int,
        navFragment: Fragment,
        eventDes: String,
        parameterName: JSONObject
    ) {
        Snackbar.make(this.findViewById(R.id.navigation_container), description, Snackbar.LENGTH_LONG)
            .setAction(ctaText) { onNavigate(navFragment, fragmentTag = navFragment.tag ?: "") }
            .setActionTextColor(ctaColor)
            .show()
    }

    override fun onNavigate(fragment: Fragment, addToBackStack: Boolean, isFragmentToBeAdded: Boolean, enterAnimation: Int, exitAnimation: Int, fragmentTag: String) {
        if (!isFinishing) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(enterAnimation, exitAnimation)
            if (isFragmentToBeAdded) {
                transaction.add(R.id.navigation_container, fragment, fragmentTag)
            } else {
                transaction.replace(R.id.navigation_container, fragment, fragmentTag)
            }
            if (addToBackStack) {
                transaction.addToBackStack(fragmentTag)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun clearBackStack() {
        for (i in 0 until supportFragmentManager.backStackEntryCount - 1) {
            if (!isFinishing) {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun attacheChildFragment(fm: FragmentManager, fragment: Fragment, addToBackStack: Boolean, containerId: Int, tag: String, forceRefresh: Boolean) {
        val tempFragment = fm.findFragmentByTag(tag)
        if (tempFragment != null && !forceRefresh) {
            return
        }
        val transaction = fm.beginTransaction()
        transaction.replace(containerId, fragment, tag)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }



    override fun onBackPressed() {
        val backHandled = supportFragmentManager.fragments.isNotEmpty()
                && supportFragmentManager.fragments.last() is BackPressListener
                && (supportFragmentManager.fragments.last() as BackPressListener).isBackPressHandled()
        if (!backHandled) {
            if (supportFragmentManager.backStackEntryCount > 1 && supportFragmentManager.fragments.last() is BackPressListener) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

}
