package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.ColumnInfo

internal class PaymentMethodDetailEntity(
	@ColumnInfo(name = "method")
	val method: String,

	@ColumnInfo(name = "upi_id")
	val upiId: String?
)
