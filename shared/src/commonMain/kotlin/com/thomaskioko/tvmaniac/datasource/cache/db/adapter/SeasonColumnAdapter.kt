package com.thomaskioko.tvmaniac.datasource.cache.db.adapter

import com.squareup.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.presentation.model.Season
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val seasonsListAdapter = object : ColumnAdapter<List<Season>, String> {

    override fun encode(value: List<Season>) = Json.encodeToString(value)

    override fun decode(databaseValue: String): List<Season> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            Json.decodeFromString(databaseValue)
        }

}