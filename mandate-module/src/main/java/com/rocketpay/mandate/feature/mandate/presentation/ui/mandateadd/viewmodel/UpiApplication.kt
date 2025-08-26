package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class UpiApplication(val value: String, val name: String, val upiHandles: List<String>) {
    object None : UpiApplication("NONE", ResourceManager.getInstance().getString(R.string.rp_upi_app), listOf())
    object Bhim : UpiApplication("BHIM", ResourceManager.getInstance().getString(R.string.rp_bhim), listOf("@upi"))
    object PhonePe : UpiApplication("PHONE_PE", ResourceManager.getInstance().getString(R.string.rp_phone_pe), listOf("@ybl", "@ibl", "@axl"))
    object GooglePay : UpiApplication("GOOGLE_PAY", ResourceManager.getInstance().getString(R.string.rp_google_pay), listOf("@okhdfcbank", "@okaxis", "@oksbi", "@okicici"))
    object Paytm : UpiApplication("PAYTM", ResourceManager.getInstance().getString(R.string.rp_paytm), listOf("@paytm"))
    object Cred : UpiApplication("CRED", ResourceManager.getInstance().getString(R.string.rp_cred), listOf("@axisb"))

    object AmazonPay : UpiApplication("AMAZON_PAY", ResourceManager.getInstance().getString(R.string.rp_amazon_pay), listOf("@apl", "@yapl"))
    object MobiKwik : UpiApplication("MOBI_KWIK", ResourceManager.getInstance().getString(R.string.rp_mobi_kwik), listOf("@ikwik"))
    object WhatsApp : UpiApplication("WHATS_APP", ResourceManager.getInstance().getString(R.string.rp_whats_app), listOf("@waicici", "@waaxis", "@wahdfcbank", "@wasbi"))

    object Hdfc : UpiApplication("HDFC", ResourceManager.getInstance().getString(R.string.rp_hdfc), listOf("@hdfcbank")) // ****
    object Icici : UpiApplication("ICICI", ResourceManager.getInstance().getString(R.string.rp_icici), listOf("@icici"))
    object Axis : UpiApplication("AXIS", ResourceManager.getInstance().getString(R.string.rp_axis), listOf("@axisbank"))
    object Yes : UpiApplication("YES", ResourceManager.getInstance().getString(R.string.rp_yes_bank), listOf("@yesbank"))
    object Sbi : UpiApplication("SBI", ResourceManager.getInstance().getString(R.string.rp_sbi), listOf("@sbi"))
    object Kotak : UpiApplication("KOTAK", ResourceManager.getInstance().getString(R.string.rp_kotak), listOf("@kotak"))
    object Indusland : UpiApplication("INDUSLAND", ResourceManager.getInstance().getString(R.string.rp_indusland), listOf("@indus"))
    object Idfc : UpiApplication("IDFC", ResourceManager.getInstance().getString(R.string.rp_idfc), listOf("@idfcbank"))
    object Hsbc : UpiApplication("HSBC", ResourceManager.getInstance().getString(R.string.rp_hsbc), listOf("@hsbc"))
    object Federal : UpiApplication("FEDERAL", ResourceManager.getInstance().getString(R.string.rp_federal), listOf("@federal"))

    companion object {
        val map by lazy {
            mapOf(
                "NONE" to None,
                "BHIM" to Bhim,
                "PHONE_PE" to PhonePe,
                "GOOGLE_PAY" to GooglePay,
                "PAYTM" to Paytm,
                "CRED" to Cred,

                "AMAZON_PAY" to AmazonPay,
                "MOBI_KWIK" to MobiKwik,
                "WHATS_APP" to WhatsApp,

                "HDFC" to Hdfc,
                "ICICI" to Icici,
                "AXIS" to Axis,
                "YES" to Yes,
                "SBI" to Sbi,
                "KOTAK" to Kotak,
                "INDUSLAND" to Indusland,
                "IDFC" to Idfc,
                "HSBC" to Hsbc,
                "FEDERAL" to Federal,
            )
        }
    }
}
