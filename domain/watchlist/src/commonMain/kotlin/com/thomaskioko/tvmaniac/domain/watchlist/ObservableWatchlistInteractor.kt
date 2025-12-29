package com.thomaskioko.tvmaniac.domain.watchlist

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject

@Inject
public class ObservableWatchlistInteractor(
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

    public data class Param(
        val query: String = "",
    )
}

public data class WatchlistData(
    val watchlist: List<Watchlists>,
    val isGridMode: Boolean,
    val query: String,
)
