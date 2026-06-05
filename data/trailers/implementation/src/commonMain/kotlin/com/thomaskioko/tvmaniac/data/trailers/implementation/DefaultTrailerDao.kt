package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTrailerDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatchers: AppCoroutineDispatchers,
) : TrailerDao {

    override fun upsert(trailer: Trailers) {
        database.trailersQueries.insertOrReplace(
            id = trailer.id,
            show_id = trailer.show_id,
            youtube_url = trailer.youtube_url,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type,
        )
    }

    override fun getTrailersByShowTraktId(showTraktId: Long): List<SelectByShowTraktId> {
        val showId = showIdResolver.showIdForTraktId(showTraktId) ?: return emptyList()
        return database.trailersQueries.selectByShowTraktId(showId).executeAsList()
    }

    override fun observeTrailersByShowTraktId(showTraktId: Long): Flow<List<SelectByShowTraktId>> {
        val showId = showIdResolver.showIdForTraktId(showTraktId) ?: return flowOf(emptyList())
        return database.trailersQueries.selectByShowTraktId(showId).asFlow().mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.transaction { database.trailersQueries.delete(Id(id)) }
    }

    override fun deleteAll() {
        database.transaction { database.trailersQueries.deleteAll() }
    }
}
