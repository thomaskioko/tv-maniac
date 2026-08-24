package com.thomaskioko.tvmaniac.core.deeplink.api

import kotlinx.serialization.Serializable

@Serializable
public sealed class DeepLink {
    public data class ShowDetails(
        val showId: Long,
        val forceRefresh: Boolean = true,
    ) : DeepLink()

    public data class SeasonDetails(
        val showId: Long,
        val seasonId: Long,
        val seasonNumber: Long,
        val forceRefresh: Boolean = false,
    ) : DeepLink()

    public data class Episode(
        val showId: Long,
        val seasonNumber: Long,
        val episodeNumber: Long,
    ) : DeepLink()

    public data object DebugMenu : DeepLink()
}
