package com.thomaskioko.tvmaniac.core.db

import app.cash.sqldelight.ColumnAdapter

val stringColumnAdapter = object : ColumnAdapter<List<String>, String> {

    override fun encode(value: List<String>) = value.joinToString(separator = ",")

    override fun decode(databaseValue: String): List<String> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",").map { it }
        }
}
