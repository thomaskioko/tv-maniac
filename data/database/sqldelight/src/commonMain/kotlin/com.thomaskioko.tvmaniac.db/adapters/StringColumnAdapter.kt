package com.thomaskioko.tvmaniac.db.adapters

import app.cash.sqldelight.ColumnAdapter

public val stringColumnAdapter: ColumnAdapter<List<String>, String> = object : ColumnAdapter<List<String>, String> {

    override fun encode(value: List<String>) = value.joinToString(separator = ",")

    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",").map { it }
        }
}
