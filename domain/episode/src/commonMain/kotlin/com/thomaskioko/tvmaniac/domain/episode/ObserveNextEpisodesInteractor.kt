package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveNextEpisodesInteractor(
    private val episodeRepository: EpisodeRepository,
    private val watchlistRepository: WatchlistRepository,
) : SubjectInteractor<Unit, List<NextEpisodeWithShow>>() {

    override fun createObservable(params: Unit): Flow<List<NextEpisodeWithShow>> {
        return watchlistRepository.observeWatchlist()
            .flatMapLatest { watchlist ->
                if (watchlist.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    episodeRepository.observeNextEpisodesForWatchlist()
                }
            }
            .distinctUntilChanged()
    }
}
