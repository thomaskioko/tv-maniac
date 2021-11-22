package com.thomaskioko.tvmaniac.datasource.cache.db.adapter

import com.squareup.sqldelight.ColumnAdapter

val intAdapter = object : ColumnAdapter<List<Int>, String> {

    override fun encode(value: List<Int>) = value.joinToString(separator = ",")

    override fun decode(databaseValue: String): List<Int> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",").map { it.toInt() }
        }
}
