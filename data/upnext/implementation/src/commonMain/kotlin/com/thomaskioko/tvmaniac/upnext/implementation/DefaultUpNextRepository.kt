package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val nextEpisodeDao: NextEpisodeDao,
    private val datastoreRepository: DatastoreRepository,
    private val followedShowsDao: FollowedShowsDao,
) : UpNextRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override fun observeFollowedShowsCount(): Flow<Int> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.count { it.pendingAction != PendingAction.DELETE } }

    override suspend fun saveUpNextSortOption(sortOption: String) {
        datastoreRepository.saveUpNextSortOption(sortOption)
    }

    override fun observeUpNextSortOption(): Flow<String> =
        datastoreRepository.observeUpNextSortOption()
}
