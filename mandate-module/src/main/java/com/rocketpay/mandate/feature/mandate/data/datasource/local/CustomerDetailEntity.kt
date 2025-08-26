package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.ColumnInfo

internal data class CustomerDetailEntity(
	@ColumnInfo(name = "name")
	val name: String,

	@ColumnInfo(name = "mobile_number")
	val mobileNumber: String
)
