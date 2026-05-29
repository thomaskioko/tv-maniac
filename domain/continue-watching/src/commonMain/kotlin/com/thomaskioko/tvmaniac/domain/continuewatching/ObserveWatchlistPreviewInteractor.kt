package com.thomaskioko.tvmaniac.domain.continuewatching

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.model.WatchlistShowInfo
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveWatchlistPreviewInteractor(
    private val upNextRepository: UpNextRepository,
) : SubjectInteractor<ObserveWatchlistPreviewInteractor.Param, List<WatchlistShowInfo>>() {

    override fun createObservable(params: Param): Flow<List<WatchlistShowInfo>> =
        upNextRepository.observeNextEpisodesForWatchlist()
            .map { episodes ->
                episodes
                    .map { it.toWatchlistShowInfo() }
                    .filterNot { it.isCompleted() }
                    .take(params.limit)
            }

    public data class Param(
        val limit: Int = DEFAULT_LIMIT,
    )

    public companion object {
        public const val DEFAULT_LIMIT: Int = 20
    }
}
