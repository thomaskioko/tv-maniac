package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class WatchlistMetadataStore(
  private val watchlistDao: WatchlistDao,
  private val tmdbRemoteDataSource: TmdbShowDetailsNetworkDataSource,
  private val transactionRunner: DatabaseTransactionRunner,
  private val scope: AppCoroutineScope,
) : Store<Id<TmdbId>, List<Watchlists>> by StoreBuilder.from(
  fetcher = Fetcher.of { showId: Id<TmdbId> ->
    //TODO:: Also fetch up next episode.
    when (val response = tmdbRemoteDataSource.getShowDetails(showId.id)) {
      is ApiResponse.Success -> response.body
      is ApiResponse.Error.GenericError -> throw Throwable(response.errorMessage)
      is ApiResponse.Error.HttpError -> throw Throwable("${response.code} - ${response.errorMessage}")
      is ApiResponse.Error.SerializationError -> throw Throwable(response.errorMessage)
    }
  },
  sourceOfTruth = SourceOfTruth.of(
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
  ),
)
  .scope(scope.io)
  .build()
