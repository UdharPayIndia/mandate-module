package com.rocketpay.mandate.common.syncmanager.client.data.datasource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_request")
internal class SyncRequestEntity (

        @PrimaryKey
        @ColumnInfo(name = "id")
        var id: String,

        @ColumnInfo(name = "type")
        var syncType: String,

        @ColumnInfo(name = "status")
        var syncStatus: String,

        @ColumnInfo(name = "retry_count")
        var retryCount: Int,

        @ColumnInfo(name = "back_off_time")
        var backOffTime: Long
)
