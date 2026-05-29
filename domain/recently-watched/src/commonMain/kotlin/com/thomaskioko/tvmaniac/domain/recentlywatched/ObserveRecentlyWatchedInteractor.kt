package com.thomaskioko.tvmaniac.domain.recentlywatched

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.RecentlyWatchedEpisode
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveRecentlyWatchedInteractor(
    private val repository: EpisodeRepository,
) : SubjectInteractor<ObserveRecentlyWatchedInteractor.Param, List<RecentlyWatchedEpisode>>() {

    override fun createObservable(params: Param): Flow<List<RecentlyWatchedEpisode>> =
        repository.observeRecentlyWatched(params.limit)

    public data class Param(
        val limit: Long = DEFAULT_LIMIT,
    )

    public companion object {
        public const val DEFAULT_LIMIT: Long = 20
    }
}
