package com.thomaskioko.tvmaniac.core.notifications.api

public data class EpisodeNotification(
    val id: Long,
    val showId: Long,
    val seasonId: Long,
    val showName: String,
    val episodeTitle: String,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val imageUrl: String?,
    val scheduledTime: Long,
    val message: String,
    val channel: NotificationChannel = NotificationChannel.EPISODES_AIRING,
) {
    val title: String get() = showName
}
