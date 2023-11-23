package com.thomaskioko.tvmaniac.db

import app.cash.sqldelight.ColumnAdapter

internal class IdAdapter<T> : ColumnAdapter<Id<T>, Long> {
    override fun decode(databaseValue: Long): Id<T> = Id(id = databaseValue)
    override fun encode(value: Id<T>): Long = value.id
}
