package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.LIBRARY_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType.SHOWS_WATCHLISTED
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class LibraryStore(
    private val traktListDataSource: TraktListRemoteDataSource,
    private val followedShowsDao: FollowedShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityDao: TraktActivityDao,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<LibrarySortOption, List<FollowedShowEntry>> by storeBuilder(
    fetcher = apiFetcher { key: LibrarySortOption ->
        traktListDataSource.getWatchList(sortBy = key.sortBy, sortHow = key.sortHow)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: LibrarySortOption -> followedShowsDao.entriesObservable() },
        writer = { _: LibrarySortOption, response: List<TraktFollowedShowResponse> ->
            val networkEntries = response.map { it.toFollowedShowEntry() }

            transactionRunner {
                val currentEntries = followedShowsDao.entriesWithNoPendingAction()
                val currentByTraktId = currentEntries.associateBy { it.traktId }
                val networkTraktIds = networkEntries.map { it.traktId }.toSet()

                networkEntries.forEach { networkEntry ->
                    val existingEntry = currentByTraktId[networkEntry.traktId]
                    val _ = followedShowsDao.upsert(networkEntry.copy(id = existingEntry?.id ?: 0))
                }

                currentEntries.forEach { localEntry ->
                    if (localEntry.traktId !in networkTraktIds) {
                        followedShowsDao.deleteById(localEntry.id)
                    }
                }
            }

            requestManagerRepository.upsert(
                entityId = LIBRARY_SYNC.requestId,
                requestType = LIBRARY_SYNC.name,
            )

            traktActivityDao.markAsSynced(SHOWS_WATCHLISTED)
        },
        delete = { _: LibrarySortOption -> },
        deleteAll = { },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = LIBRARY_SYNC.name,
                threshold = LIBRARY_SYNC.duration,
            )
        }
    },
).build()

private fun TraktFollowedShowResponse.toFollowedShowEntry(): FollowedShowEntry = FollowedShowEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    followedAt = Instant.parse(listedAt),
    pendingAction = PendingAction.NOTHING,
)
