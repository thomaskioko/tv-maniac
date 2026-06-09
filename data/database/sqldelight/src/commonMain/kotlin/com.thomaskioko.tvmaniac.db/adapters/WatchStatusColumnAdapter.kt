package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.db.WatchStatus

public object WatchStatusColumnAdapter : ColumnAdapter<WatchStatus, String> {
    override fun decode(databaseValue: String): WatchStatus = WatchStatus.valueOf(databaseValue)

    override fun encode(value: WatchStatus): String = value.name
}
