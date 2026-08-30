package com.thomaskioko.tvmaniac.domain.widget.model

import kotlinx.serialization.Serializable

@Serializable
public data class WidgetSnapshot(
    val writtenAtMillis: Long,
    val entries: List<WidgetSnapshotEntry>,
)

@Serializable
public data class WidgetSnapshotEntry(
    val tmdbId: Long,
    val showName: String,
    val episodeName: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val posterFileName: String? = null,
)
