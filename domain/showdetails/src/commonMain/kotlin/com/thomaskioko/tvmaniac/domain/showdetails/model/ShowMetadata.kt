package com.thomaskioko.tvmaniac.domain.showdetails.model

import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class ShowMetadata(
    val providers: List<Providers> = emptyList(),
    val castsList: List<Casts> = emptyList(),
    val seasonsList: List<Season> = emptyList(),
    val similarShows: List<Show> = emptyList(),
    val trailersList: List<Trailer> = emptyList(),
    val hasWebViewInstalled: Boolean = false,
    val continueTrackingEpisodes: ImmutableList<EpisodeDetails> = persistentListOf(),
) {
    public val continueTrackingScrollIndex: Int
        get() {
            val firstUnwatched = continueTrackingEpisodes.indexOfFirst { !it.isWatched }
            if (firstUnwatched >= 0) return firstUnwatched

            val nextAfterLastWatched = continueTrackingEpisodes.indexOfLast { it.isWatched } + 1
            return if (nextAfterLastWatched < continueTrackingEpisodes.size) nextAfterLastWatched else 0
        }

    public companion object {
        public val Empty: ShowMetadata = ShowMetadata()
    }
}
