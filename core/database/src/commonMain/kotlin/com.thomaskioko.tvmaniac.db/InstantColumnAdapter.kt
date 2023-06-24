package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

internal object InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}
