package com.rocketpay.mandate.feature.installment.data.entities

import androidx.annotation.Keep

@Keep
internal class InstallmentResponse(
    var total: Int = 0,
    var returned: Int = 0,
    var items: List<InstallmentDto>
)
