package com.thomaskioko.tvmaniac.datasource.cache.db.adapter

import com.squareup.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.presentation.model.Episode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val episodeListAdapter = object : ColumnAdapter<List<Episode>, String> {

    override fun encode(value: List<Episode>) = Json.encodeToString(value)

    override fun decode(databaseValue: String): List<Episode> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            Json.decodeFromString(databaseValue)
        }

}