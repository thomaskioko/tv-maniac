package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Instant

object InstantColumnAdapter : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}
