package com.thomaskioko.tvmaniac.nextepisode.implementation

import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.nextepisode.implementation.model.NextEpisodeKey
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultNextEpisodeRepository(
    private val nextEpisodeStore: NextEpisodeStore,
    private val nextEpisodeDao: NextEpisodeDao,
    private val watchedEpisodeDao: WatchedEpisodeDao,
) : NextEpisodeRepository {

    override suspend fun fetchNextEpisode(showId: Long) {
        val shouldFetch = shouldFetchNextEpisodeForShow(showId)
        if (shouldFetch) {
            val seasonToFetch = determineSeasonToFetch(showId)
            val key = NextEpisodeKey(showId, seasonToFetch)
            nextEpisodeStore.fresh(key)
        }
    }

    private suspend fun shouldFetchNextEpisodeForShow(showId: Long): Boolean {
        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(showId)

        val existingNextEpisode = nextEpisodeDao.getNextEpisodeForShow(showId)

        return when {
            lastWatched == null -> true

            else -> {
                val expectedNextSeasonNumber = lastWatched.season_number
                val expectedNextEpisodeNumber = lastWatched.episode_number + 1

                if (existingNextEpisode == null) return true

                val cachedEpisodeMatches = existingNextEpisode.season_number == expectedNextSeasonNumber &&
                    existingNextEpisode.episode_number == expectedNextEpisodeNumber

                !cachedEpisodeMatches
            }
        }
    }

    private suspend fun determineSeasonToFetch(showId: Long): Long {
        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(showId)

        return when {
            lastWatched == null -> 1
            else -> lastWatched.season_number
        }
    }

    override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> {
        return nextEpisodeDao.observeNextEpisodesForWatchlist()
    }

    override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> {
        return nextEpisodeDao.observeNextEpisode(showId)
    }

    override suspend fun refreshNextEpisodeData(showId: Long) {
        val seasonToFetch = determineSeasonToFetch(showId)
        val key = NextEpisodeKey(showId, seasonToFetch)
        nextEpisodeStore.fresh(key)
    }

    override suspend fun removeShowFromTracking(showId: Long) {
        // Clean up next episode cache
        nextEpisodeDao.delete(showId)

        // Clean up watched episodes history
        watchedEpisodeDao.deleteAllForShow(showId)
    }
}
