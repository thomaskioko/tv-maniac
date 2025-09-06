package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadRequest
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultWatchlistRepository(
    private val watchlistDao: WatchlistDao,
    private val watchlistMetadataStore: WatchlistMetadataStore,
    private val datastoreRepository: DatastoreRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val seasonsRepository: SeasonsRepository,
) : WatchlistRepository {

    override suspend fun updateLibrary(id: Long, addToLibrary: Boolean) {
        when {
            addToLibrary -> {
                // Add to watchlist
                watchlistDao.upsert(id)

                // Fetch season details for all seasons to populate episode data
                seasonsRepository.observeSeasonsByShowId(id).collect { seasonList ->
                    seasonList.firstOrNull()?.let { season ->
                        seasonDetailsRepository.fetchSeasonDetails(
                            SeasonDetailsParam(
                                showId = id,
                                seasonId = season.season_id.id,
                                seasonNumber = season.season_number,
                            ),
                        )
                    }
                }
            }
            else -> {
                // Remove from watchlist
                watchlistDao.delete(id)

                // Clean up all episode tracking data for this show
                episodeRepository.clearWatchHistoryForShow(id)
            }
        }
    }

    override fun observeWatchlist(): Flow<List<Watchlists>> =
        watchlistDao.observeShowsInWatchlist().distinctUntilChanged()

    override fun observeUnSyncedItems(): Flow<Unit> {
        return watchlistDao.observeUnSyncedWatchlist().flatMapMerge { ids ->
            flow {
                ids.forEach { id ->
                    watchlistMetadataStore.stream(StoreReadRequest.fresh(id)).collect()
                    emit(Unit)
                }
            }
        }
    }

    override fun searchWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> {
        return watchlistDao.observeWatchlistByQuery(query)
    }

    override fun observeListStyle(): Flow<Boolean> {
        return datastoreRepository.observeListStyle().map { listStyle ->
            listStyle == ListStyle.GRID
        }
    }

    override suspend fun saveListStyle(isGridMode: Boolean) {
        val listStyle = if (isGridMode) ListStyle.GRID else ListStyle.LIST
        datastoreRepository.saveListStyle(listStyle)
    }
}
