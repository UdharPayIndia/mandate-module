package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class UpiIdError(val value: String, val displayString: String) {
    object InvalidUpiId : UpiIdError("INVALID_UPI_ID", ResourceManager.getInstance().getString(R.string.rp_error_upi_id_invalid))
    object UnsupportedUpiId : UpiIdError("UNSUPPORTED_UPI_ID", ResourceManager.getInstance().getString(R.string.rp_error_upi_id_unsupported))

    companion object {
        val map by lazy {
            mapOf(
                "INVALID_UPI_ID" to InvalidUpiId,
                "UNSUPPORTED_UPI_ID" to UnsupportedUpiId
            )
        }

        fun get(code: String): UpiIdError {
            return map[code] ?: InvalidUpiId
        }
    }
}
