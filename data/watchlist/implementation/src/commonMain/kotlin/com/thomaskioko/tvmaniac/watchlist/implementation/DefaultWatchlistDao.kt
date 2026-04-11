package com.thomaskioko.tvmaniac.watchlist.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchlistDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : WatchlistDao {

    override fun observeShowsInWatchlist(): Flow<List<FollowedShows>> =
        database.followedShowsQueries.followedShows()
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>> =
        database.followedShowsQueries
            .searchFollowedShows(query = query)
            .asFlow()
            .mapToList(dispatchers.io)

    override fun observeIsShowInLibrary(traktId: Long): Flow<Boolean> =
        database.followedShowsQueries.isShowFollowed(Id(traktId))
            .asFlow()
            .mapToOne(dispatchers.io)
}
