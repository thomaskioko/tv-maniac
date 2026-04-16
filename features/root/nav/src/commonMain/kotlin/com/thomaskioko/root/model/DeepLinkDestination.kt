package com.thomaskioko.root.model

import kotlinx.serialization.Serializable

@Serializable
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

    public companion object {
        public const val EXTRA_DEEP_LINK: String = "extra_deep_link"
        public const val DEEP_LINK_DEBUG_MENU: String = "debug_menu"
    }
}
