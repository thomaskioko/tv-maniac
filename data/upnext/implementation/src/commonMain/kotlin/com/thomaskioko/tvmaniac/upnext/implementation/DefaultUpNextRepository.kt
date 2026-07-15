package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val nextEpisodeDao: NextEpisodeDao,
    private val datastoreRepository: DatastoreRepository,
    @IoCoroutineScope scope: CoroutineScope,
) : UpNextRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val nextEpisodes: Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }
            .shareIn(scope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT_MS), replay = 1)

    private val completedShows: Flow<List<CompletedShow>> =
        nextEpisodeDao.observeCompletedShows()
            .shareIn(scope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT_MS), replay = 1)

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> = nextEpisodes

    override fun observeCompletedShows(): Flow<List<CompletedShow>> = completedShows

    override suspend fun saveUpNextSortOption(sortOption: String) {
        datastoreRepository.saveUpNextSortOption(sortOption)
    }

    override fun observeUpNextSortOption(): Flow<String> =
        datastoreRepository.observeUpNextSortOption()

    private companion object {
        private const val SHARING_STOP_TIMEOUT_MS = 5_000L
    }
}
