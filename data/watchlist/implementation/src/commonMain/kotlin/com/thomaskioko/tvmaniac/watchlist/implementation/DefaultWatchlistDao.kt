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
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
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
