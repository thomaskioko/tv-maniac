package com.thomaskioko.tvmaniac.upnext.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUpNextRepository(
    private val nextEpisodeDao: NextEpisodeDao,
    private val datastoreRepository: DatastoreRepository,
) : UpNextRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> =
        datastoreRepository.observeIncludeSpecials()
            .flatMapLatest { includeSpecials ->
                nextEpisodeDao.observeNextEpisodesForWatchlist(includeSpecials)
            }

    override fun observeCompletedShows(): Flow<List<CompletedShow>> =
        nextEpisodeDao.observeCompletedShows()

    override suspend fun saveUpNextSortOption(sortOption: String) {
        datastoreRepository.saveUpNextSortOption(sortOption)
    }

    override fun observeUpNextSortOption(): Flow<String> =
        datastoreRepository.observeUpNextSortOption()
}
