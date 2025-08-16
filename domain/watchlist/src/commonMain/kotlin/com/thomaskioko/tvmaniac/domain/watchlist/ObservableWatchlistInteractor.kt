package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

@Inject
class ObservableWatchlistInteractor(
    private val watchlistRepository: WatchlistRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<ObservableWatchlistInteractor.Param, WatchlistData>() {

    override fun createObservable(params: Param): Flow<WatchlistData> {
        return combine(
            watchlistRepository.observeWatchlist(),
            watchlistRepository.observeListStyle(),
        ) { watchlist, isGridMode ->
            WatchlistData(
                watchlist = watchlist,
                isGridMode = isGridMode,
                query = params.query,
            )
        }.flowOn(dispatchers.io)
    }

    data class Param(
        val query: String = "",
    )
}

data class WatchlistData(
    val watchlist: List<Watchlists>,
    val isGridMode: Boolean,
    val query: String,
)
