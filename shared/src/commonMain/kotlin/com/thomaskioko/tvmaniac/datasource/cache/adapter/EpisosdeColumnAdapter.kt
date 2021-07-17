package com.thomaskioko.tvmaniac.datasource.cache.adapter

import com.squareup.sqldelight.ColumnAdapter
import com.thomaskioko.tvmaniac.datasource.cache.model.EpisodeEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val episodeListAdapter = object : ColumnAdapter<List<EpisodeEntity>, String> {

    override fun encode(value: List<EpisodeEntity>) = Json.encodeToString(value)

    override fun decode(databaseValue: String): List<EpisodeEntity> =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            Json.decodeFromString(databaseValue)
        }

}