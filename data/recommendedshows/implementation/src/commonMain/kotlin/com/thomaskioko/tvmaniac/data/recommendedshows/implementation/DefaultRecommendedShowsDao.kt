package com.thomaskioko.tvmaniac.data.recommendedshows.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRecommendedShowsDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : RecommendedShowsDao {
    override fun upsert(showTraktId: Long, showTmdbId: Long, recommendedShowTraktId: Long) {
        database.recommendedShowsQueries.transaction {
            database.recommendedShowsQueries.upsert(
                trakt_id = Id(showTraktId),
                tmdb_id = Id(showTmdbId),
                recommended_show_trakt_id = Id(recommendedShowTraktId),
            )
        }
    }

    override fun observeRecommendedShows(showTraktId: Long): Flow<List<RecommendedShows>> {
        return database.recommendedShowsQueries
            .recommendedShows(Id(showTraktId))
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
