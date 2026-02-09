package com.thomaskioko.tvmaniac.core.notifications.implementation.model

import kotlinx.serialization.Serializable

@Serializable
internal data class StoredNotification(
    val id: Long,
    val showId: Long,
    val seasonId: Long = 0L,
    val showName: String,
    val episodeTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val imageUrl: String?,
    val scheduledTime: Long,
    val channelId: String = "episodes_airing",
)
