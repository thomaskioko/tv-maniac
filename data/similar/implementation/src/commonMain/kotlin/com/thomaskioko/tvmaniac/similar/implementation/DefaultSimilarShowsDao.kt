package com.thomaskioko.tvmaniac.similar.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.SimilarShows
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSimilarShowsDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : SimilarShowsDao {

    override fun upsert(showId: Long, tmdbId: Long, traktId: Long, pageOrder: Int) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        database.similarShowsQueries.transaction {
            database.similarShowsQueries.insertOrReplace(
                show_id = internalShowId,
                tmdb_id = Id(tmdbId),
                trakt_id = Id(traktId),
                page_order = pageOrder.toLong(),
            )
        }
    }

    override fun observeSimilarShows(showId: Long): Flow<List<SimilarShows>> {
        return database.similarShowsQueries
            .similarShows(Id(showId))
            .asFlow()
            .mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.similarShowsQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { database.similarShowsQueries.deleteAll() }
    }
}
