package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.core.store.storeBuilder
import com.thomaskioko.tvmaniac.core.store.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbShowDetailsResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

@Inject
@SingleIn(AppScope::class)
class WatchlistMetadataStore(
    private val watchlistDao: WatchlistDao,
    private val tmdbRemoteDataSource: TmdbShowDetailsNetworkDataSource,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Id<TmdbId>, List<Watchlists>> by storeBuilder(
    fetcher = Fetcher.of { showId: Id<TmdbId> ->
        // TODO:: Also fetch up next episode.
        when (val response = tmdbRemoteDataSource.getShowDetails(showId.id)) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> throw Throwable(response.errorMessage)
            is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable(response.errorMessage)
        }
    },
    sourceOfTruth = SourceOfTruth.of<Id<TmdbId>, TmdbShowDetailsResponse, List<Watchlists>>(
        reader = { _ -> watchlistDao.observeShowsInWatchlist() },
        writer = { showId, response ->
            transactionRunner {
                watchlistDao.upsert(
                    Show_metadata(
                        show_id = showId,
                        season_count = response.numberOfSeasons.toLong(),
                        episode_count = response.numberOfEpisodes.toLong(),
                        status = response.status,
                    ),
                )
                // Mark as synced in watchlist
                watchlistDao.updateSyncState(showId)
            }
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
)
    .build()
