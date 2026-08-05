package com.thomaskioko.tvmaniac.domain.statistics

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.watchstatus.api.ShowWatchStatusRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Inject
public class ObserveWatchStatisticsInteractor(
    private val episodeRepository: EpisodeRepository,
    private val showWatchStatusRepository: ShowWatchStatusRepository,
    private val ratingsRepository: RatingsRepository,
    private val calculator: WatchStatisticsCalculator,
) : SubjectInteractor<Unit, WatchStatistics>() {

    override fun createObservable(params: Unit): Flow<WatchStatistics> = combine(
        episodeRepository.observeWatchedEpisodeRuntimes(),
        episodeRepository.observeMostWatchedShows(MOST_WATCHED_LIMIT),
        showWatchStatusRepository.observeShowCountsByStatus(),
        ratingsRepository.observeUserRatingDistribution(),
    ) { watches, mostWatchedShows, showCountsByStatus, ratingCounts ->
        calculator.calculate(
            watches = watches,
            mostWatchedShows = mostWatchedShows,
            showCountsByStatus = showCountsByStatus,
            ratingCounts = ratingCounts,
        )
    }

    private companion object {
        const val MOST_WATCHED_LIMIT = 10L
    }
}
