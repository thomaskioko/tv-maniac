package com.thomaskioko.tvmaniac.domain.widget

import kotlinx.serialization.json.Json

public object WidgetSnapshotJson {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    public fun encode(snapshot: WidgetSnapshot): String = json.encodeToString(snapshot)

    public fun decode(contents: String): WidgetSnapshot = json.decodeFromString(contents)
}
