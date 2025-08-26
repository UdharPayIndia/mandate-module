package com.rocketpay.mandate.main.init

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.main.database.MandateDatabase
import com.rocketpay.mandate.main.init.network.EventResponseInterceptor
import com.rocketpay.mandate.main.init.network.NetworkResponseInterceptor
import com.rocketpay.mandate.main.init.network.TokenRequestInterceptor
import com.rocketpay.mandate.main.init.resourcemanager.LanguageTranslatorImpl
import com.rocketpay.mandate.main.init.serializer.GsonAdapterRegister
import com.rocketpay.mandate.main.init.serializer.GsonConverterContract
import com.rocketpay.mandate.main.init.serializer.JsonHandlerImpl
import com.rocketpay.mandate.main.init.sync.SyncConstraintObserverRegister
import com.rocketpay.mandate.main.init.sync.SyncRegister
import com.udharpay.core.networkmanager.client.NetworkManager
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.rocketpay.mandate.feature.login.data.datasource.local.LoginDataStore
import kotlinx.coroutines.GlobalScope

class MandateManager private constructor(
    private val context: Context,
    private val enterpriseId: String,
    private val appName: String,
    private var loginMobileNumber: String,
    private val customerSupportNumber: String,
    private val skipKyc: Boolean,
    private val isDebug: Boolean
){

    //Config
    internal val penaltyMinimumAmount: Double = 15.0
    internal val isDailyFrequencyEnable: Boolean = true
    internal val maxUpiMonetisedInstallmentAmount: Int = AmountUtils.NON_MONETISED_UPI_MAXIMUM_AMOUNT
    internal val isAdhocInstallmentFrequencyEnable: Boolean = false
    internal val termsAndConditionUrl: String = "https://www.rocketpay.co.in/termsofuse"
    internal val nonBusinessEnumString: String = "I don’t have a business"

    companion object {
        private var instance: MandateManager? = null
        fun getInstance(): MandateManager {
            return instance ?: throw Exception("MandateManager is not built, Please use Builder to build")
        }
    }

    init {
        MandateDatabase.initialise(context)
        initJsonConverter()
        initNetworkManager()
        initSyncManager()
        initResourceManager()
    }

    private fun initJsonConverter() {
        JsonConverter.Builder<Gson>()
            .setTypeAdapters(GsonAdapterRegister())
            .setContract(GsonConverterContract())
            .build()
    }

    private fun initNetworkManager() {
        val userDataStore = LoginDataStore(DataStore(context, LoginDataStore.LOGIN_DATA_STORE))
        val builder = NetworkManager.Builder()
            .setBaseUrl(if(isDebug){
                "https://api-staging.rocketpay.co.in"
            }else{
                "https://api.rocketpay.co.in"
            })
            .addNetworkRequestInterceptor(TokenRequestInterceptor(userDataStore))
            .addNetworkResponseInterceptor(EventResponseInterceptor())
            .addNetworkResponseInterceptor(NetworkResponseInterceptor())
            .setConverterFactory(JsonConverter.getInstance().getFactory())
            .setJsonHandler(JsonHandlerImpl())
        if(isDebug){
            builder.addRequestInterceptor(ChuckerInterceptor(context))
        }
        builder.build()
    }

    private fun initSyncManager() {
        SyncManager.Builder()
            .setContext(context)
            .setScope(GlobalScope)
            .setSyncRegister(SyncRegister())
            .setConstraintObserverRegister(SyncConstraintObserverRegister())
            .build()
    }

    private fun initResourceManager() {
        ResourceManager.Builder()
            .setContext(context)
            .setLanguageTranslator(LanguageTranslatorImpl())
            .build()
    }


    /*******
     * Getter/Setter
     *******/

    fun getAppName(): String{
        return appName
    }
    fun getEnterpriseId(): String{
        return enterpriseId
    }

    fun getCustomerCareNumber(): String{
        return customerSupportNumber
    }

    fun getContext(): Context {
        return context
    }

    fun setLoginMobileNumber(loginMobileNumber: String) {
        this.loginMobileNumber = loginMobileNumber
    }

    fun getLoginMobileNumber(): String {
        return loginMobileNumber
    }

    fun skipKyc(): Boolean {
        return skipKyc && isDebug()
    }

    fun isDebug(): Boolean {
        return isDebug
    }

    /*********
     * Builder
     */
    class Builder {
        private lateinit var context: Context
        private var enterpriseId: String = ""
        private var loginMobileNumber: String = ""
        private var customerSupportNumber: String = ""
        private var skipKyc: Boolean = false
        private var appName: String = ""
        private var isDebug: Boolean = false

        fun setContext(context: Context) = apply {
            this.context = context.applicationContext
        }

        fun setEnterpriseId(enterpriseId: String) = apply {
            this.enterpriseId = enterpriseId
        }

        fun setLoginMobileNumber(loginMobileNumber: String) = apply {
            this.loginMobileNumber = loginMobileNumber
        }

        fun setCustomerSupportNumber(customerSupportNumber: String) = apply {
            this.customerSupportNumber = customerSupportNumber
        }

        fun setAppName(appName: String)= apply{
            this.appName = appName
        }

        fun skipKyc(flag: Boolean)= apply{
            this.skipKyc = flag
        }

        fun isDebug(flag: Boolean)= apply{
            this.isDebug = flag
        }

        fun build() {
            if(enterpriseId.isNullOrEmpty()){
                throw Exception("MandateManager is not built, Enterprise Id is missing")
            }
            if (instance != null) {
                throw Exception("MandateManager is already built, it can not be re built")
            }
            instance = MandateManager(context, enterpriseId,appName, loginMobileNumber, customerSupportNumber, skipKyc, isDebug)
        }
    }


}