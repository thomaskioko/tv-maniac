package com.thomaskioko.tvmaniac.datasource.cache.db.adapter

import com.squareup.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.datasource.cache.model.SeasonsEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val seasonsListAdapter = object : ColumnAdapter<List<SeasonsEntity>, String> {

    override fun encode(value: List<SeasonsEntity>) = Json.encodeToString(value)

    override fun decode(databaseValue: String): List<SeasonsEntity> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            Json.decodeFromString(databaseValue)
        }

}