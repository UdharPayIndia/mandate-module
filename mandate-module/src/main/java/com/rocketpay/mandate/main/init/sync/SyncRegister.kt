package com.rocketpay.mandate.main.init.sync

import com.rocketpay.mandate.feature.bankaccount.data.BankAccountSync
import com.rocketpay.mandate.feature.bankaccount.data.BankAccountSyncer
import com.rocketpay.mandate.feature.business.data.BusinessPropertySync
import com.rocketpay.mandate.feature.business.data.BusinessPropertySyncer
import com.rocketpay.mandate.feature.kyc.data.KycSync
import com.rocketpay.mandate.feature.kyc.data.KycSyncer
import com.rocketpay.mandate.feature.mandate.data.MandateSync
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.product.data.ProductOrderSync
import com.rocketpay.mandate.feature.product.data.ProductOrderSyncer
import com.rocketpay.mandate.feature.product.data.ProductWalletSync
import com.rocketpay.mandate.feature.product.data.ProductWalletSyncer
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSync
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.kernel.kernelcommon.register.Register

internal class SyncRegister: Register<String, Sync>() {
    init {
        register(BankAccountSyncer.TYPE, BankAccountSync())
        register(KycSyncer.TYPE, KycSync())
        register(MandateSyncer.TYPE, MandateSync())
        register(PaymentOrderSyncer.TYPE, PaymentOrderSync())
        register(ProductOrderSyncer.TYPE, ProductOrderSync())
        register(ProductWalletSyncer.TYPE, ProductWalletSync())
        register(BusinessPropertySyncer.TYPE, BusinessPropertySync())
    }
}
