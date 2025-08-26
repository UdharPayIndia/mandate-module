package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.ColumnInfo

internal class PaymentModeDetailEntity(
    @ColumnInfo(name = "mode") var mode: String?,

    @ColumnInfo(name = "merchant_collected") var merchantCollected: Boolean?,

    @ColumnInfo(name = "comments") var comments: String?,

    @ColumnInfo(name = "retry_schedule_date") var retryScheduleDate: String

)
