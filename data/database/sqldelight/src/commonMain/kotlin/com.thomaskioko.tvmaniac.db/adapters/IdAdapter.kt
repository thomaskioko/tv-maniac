package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.db.Id

public class IdAdapter<T> : ColumnAdapter<Id<T>, Long> {
    override fun decode(databaseValue: Long): Id<T> = Id(id = databaseValue)

    override fun encode(value: Id<T>): Long = value.id
}
