package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultTrailerDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : TrailerDao {

    override fun upsert(trailer: Trailers) {
        database.trailersQueries.insertOrReplace(
            id = trailer.id,
            show_id = trailer.show_id,
            key = trailer.key,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type,
        )
    }

    override fun upsert(trailerList: List<Trailers>) {
        trailerList.forEach { upsert(it) }
    }

    override fun observeTrailersById(showId: Long): Flow<List<Trailers>> {
        return database.trailersQueries.selectByShowId(Id(showId)).asFlow().mapToList(dispatchers.io)
    }

    override fun getTrailersById(showId: Long): List<Trailers> =
        database.trailersQueries.selectByShowId(Id(showId)).executeAsList()

    override fun delete(id: Long) {
        database.transaction { database.trailersQueries.delete(Id(id)) }
    }

    override fun deleteAll() {
        database.transaction { database.trailersQueries.deleteAll() }
    }
}
