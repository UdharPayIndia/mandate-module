package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mandate_subtext",
    indices = [
        Index(value = ["mandate_id"]),
        Index(value = ["subtext_id", "mandate_id"])
    ])
internal data class MandateSubtextEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subtext_id")
    var subtextId: Int = 0,

    @ColumnInfo(name = "mandate_id")
    var mandateId: String? = null,

    @ColumnInfo(name = "mandate_status")
    var mandateStatus: String? = null,

    @ColumnInfo(name = "ui_state")
    var uiState: String? = null,

    @ColumnInfo(name = "subtext")
    var subtext: String? = null,

    @ColumnInfo(name = "priority")
    var priority: Int = 0,

    @ColumnInfo(name = "is_subtext_deleted")
    var isSubTextDeleted: Boolean = false,

    @ColumnInfo(name = "subtext_created_at")
    var subtextCreatedAt: Long = 0

)
