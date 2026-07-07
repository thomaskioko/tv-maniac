package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.ShowMetadataSyncInfo
import dev.zacsweers.metro.Inject

@Inject
public class ShowMetadataSyncHelper(
    private val episodeRepository: EpisodeRepository,
) {

    public suspend fun shouldSync(showId: Long): Boolean {
        val info = episodeRepository.getShowMetadataSyncInfo(showId) ?: return true
        return !(info.isEnded() && info.hasCompleteEpisodeData())
    }

    public suspend fun shouldRefreshLatestSeason(showId: Long): Boolean {
        val info = episodeRepository.getShowMetadataSyncInfo(showId) ?: return false
        return !info.isEnded()
    }

    private fun ShowMetadataSyncInfo.isEnded(): Boolean = status?.lowercase() in ENDED_STATUSES

    private fun ShowMetadataSyncInfo.hasCompleteEpisodeData(): Boolean =
        metadataEpisodeCount > 0 && localEpisodeCount >= metadataEpisodeCount

    private companion object {
        private val ENDED_STATUSES = setOf("ended", "canceled", "cancelled")
    }
}
