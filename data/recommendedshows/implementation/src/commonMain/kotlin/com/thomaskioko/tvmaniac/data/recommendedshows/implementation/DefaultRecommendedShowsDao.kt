package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRecommendedShowsDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : RecommendedShowsDao {
    override fun upsert(showId: Long, showTmdbId: Long, recommendedShowTraktId: Long) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        database.recommendedShowsQueries.transaction {
            database.recommendedShowsQueries.upsert(
                show_id = internalShowId,
                tmdb_id = Id(showTmdbId),
                recommended_show_trakt_id = Id(recommendedShowTraktId),
            )
        }
    }

    override fun observeRecommendedShows(showId: Long): Flow<List<RecommendedShows>> {
        return database.recommendedShowsQueries
            .recommendedShows(Id(showId))
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.recommendedShowsQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { database.recommendedShowsQueries.deleteAll() }
    }
}
