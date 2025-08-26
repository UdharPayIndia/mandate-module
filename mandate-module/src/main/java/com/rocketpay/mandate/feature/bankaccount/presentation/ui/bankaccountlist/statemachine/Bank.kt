package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class Bank(val ifscPrefix: String, val name: String, val upiHandles: List<String>, val icon: Int) {
    object Hdfc : Bank("HDFC", ResourceManager.getInstance().getString(R.string.rp_hdfc), listOf("@hdfcbank"), R.mipmap.rp_hdfc) // ****
    object Icici : Bank("ICIC", ResourceManager.getInstance().getString(R.string.rp_icici), listOf("@icici"), R.mipmap.rp_icici)
    object Axis : Bank("UTIB", ResourceManager.getInstance().getString(R.string.rp_axis), listOf("@axisbank"), R.mipmap.rp_axis)
    object Yes : Bank("YESB", ResourceManager.getInstance().getString(R.string.rp_yes_bank), listOf("@yesbank"), R.mipmap.rp_yes)
    object Kotak : Bank("KKBK", ResourceManager.getInstance().getString(R.string.rp_kotak), listOf("@kotak"), R.mipmap.rp_kotak)
    object Indusind : Bank("INDB", ResourceManager.getInstance().getString(R.string.rp_indusland), listOf("@indus"), R.mipmap.rp_indusind)
    object Idfc : Bank("IDFB", ResourceManager.getInstance().getString(R.string.rp_idfc), listOf("@idfcbank"), R.mipmap.rp_idfc)
    object Hsbc : Bank("HSBC", ResourceManager.getInstance().getString(R.string.rp_hsbc), listOf("@hsbc"), R.drawable.rp_ic_bank)
    object Federal : Bank("FDRL", ResourceManager.getInstance().getString(R.string.rp_federal), listOf("@federal"), R.mipmap.rp_federal)

    object Sbi : Bank("SBIN", ResourceManager.getInstance().getString(R.string.rp_sbi), listOf(), R.mipmap.rp_sbi)
    object Bob : Bank("BARB", ResourceManager.getInstance().getString(R.string.rp_bob), listOf(), R.mipmap.rp_bob)
    object Union : Bank("UBIN", ResourceManager.getInstance().getString(R.string.rp_union), listOf(), R.mipmap.rp_union)
    object Canara : Bank("CNRB", ResourceManager.getInstance().getString(R.string.rp_canara), listOf(), R.mipmap.rp_canara)
    object Dena : Bank("BKDN", ResourceManager.getInstance().getString(R.string.rp_dena), listOf(), R.mipmap.rp_dena)
    object BankOfIndia : Bank("BKID", ResourceManager.getInstance().getString(R.string.rp_bank_of_india), listOf(), R.mipmap.rp_bank_of_india)

    object Paytm : Bank("PYTM", ResourceManager.getInstance().getString(R.string.rp_paytm_payments_bank), listOf(), R.mipmap.rp_paytm)

    companion object {
        val map by lazy {
            mapOf(
                "HDFC" to Hdfc,
                "ICIC" to Icici,
                "UTIB" to Axis,
                "YESB" to Yes,
                "KKBK" to Kotak,
                "INDB" to Indusind,
                "IDFB" to Idfc,
                "HSBC" to Hsbc,
                "FDRL" to Federal,

                "SBIN" to Sbi,
                "BARB" to Bob,
                "UBIN" to Union,
                "CNRB" to Canara,
                "BKDN" to Dena,
                "BKID" to BankOfIndia,

                "PYTM" to Paytm
            )
        }

        fun getBank(ifscCode: String): Bank? {
            return map[ifscCode.take(4)]
        }
    }
}
