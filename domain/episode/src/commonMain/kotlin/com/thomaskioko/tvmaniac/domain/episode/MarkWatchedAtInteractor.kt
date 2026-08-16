package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.domain.rewatch.WatchAgainInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.WatchedDate
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
public class MarkWatchedAtInteractor(
    private val episodeRepository: EpisodeRepository,
    private val watchAgainInteractor: WatchAgainInteractor,
    private val dateTimeProvider: DateTimeProvider,
) : Interactor<MarkWatchedAtParams>() {

    override suspend fun doWork(params: MarkWatchedAtParams) {
        when (params.target) {
            WatchedDateTarget.SHOW -> episodeRepository.markShowWatched(
                showId = params.showId,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
            WatchedDateTarget.SEASON -> markSeason(params)
            WatchedDateTarget.EPISODE -> markEpisode(params)
        }
    }

    private suspend fun markSeason(params: MarkWatchedAtParams) {
        if (params.markPrevious) {
            episodeRepository.markSeasonAndPreviousSeasonsWatched(
                showId = params.showId,
                seasonNumber = params.seasonNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        } else {
            episodeRepository.markSeasonWatched(
                showId = params.showId,
                seasonNumber = params.seasonNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        }
    }

    private suspend fun markEpisode(params: MarkWatchedAtParams) {
        val episode = episodeRepository.observeEpisodeById(params.episodeId).first()
        if (!params.isEdit && episode != null && episode.is_watched != 0L) {
            watchAgainInteractor.executeSync(
                WatchAgainInteractor.Param(
                    showId = params.showId,
                    episodeId = params.episodeId,
                    seasonNumber = params.seasonNumber,
                    episodeNumber = params.episodeNumber,
                    watchedAt = rewatchedAt(episode, params),
                ),
            )
        } else if (params.markPrevious) {
            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        } else {
            episodeRepository.markEpisodeAsWatched(
                showId = params.showId,
                episodeId = params.episodeId,
                seasonNumber = params.seasonNumber,
                episodeNumber = params.episodeNumber,
                watchedAt = params.watchedAt,
                useReleaseDate = params.useReleaseDate,
            )
        }
    }

    private fun rewatchedAt(episode: EpisodeById, params: MarkWatchedAtParams): Long {
        val releaseDate = if (params.useReleaseDate) {
            WatchedDate.releaseDateMillis(episode.first_aired, episode.runtime)
        } else {
            null
        }
        return releaseDate ?: params.watchedAt ?: dateTimeProvider.nowMillis()
    }
}

public data class MarkWatchedAtParams(
    val target: WatchedDateTarget,
    val showId: Long,
    val episodeId: Long = 0,
    val seasonNumber: Long = 0,
    val episodeNumber: Long = 0,
    val markPrevious: Boolean = false,
    val isEdit: Boolean = false,
    val watchedAt: Long? = null,
    val useReleaseDate: Boolean = false,
)
