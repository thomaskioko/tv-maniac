package com.thomaskioko.tvmaniac.navigation

public sealed class DeepLinkDestination {
    public data class ShowDetails(
        val showId: Long,
        val forceRefresh: Boolean = true,
    ) : DeepLinkDestination()

    public data class SeasonDetails(
        val showId: Long,
        val seasonId: Long,
        val seasonNumber: Long,
        val forceRefresh: Boolean = false,
    ) : DeepLinkDestination()

    public data object DebugMenu : DeepLinkDestination()
}
