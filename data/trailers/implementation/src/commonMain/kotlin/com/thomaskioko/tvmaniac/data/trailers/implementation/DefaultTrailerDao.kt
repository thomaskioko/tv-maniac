package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowId
import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultTrailerDao(
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

    override fun observeTrailersById(showId: Long): Flow<List<SelectByShowId>> {
        return database.trailersQueries.selectByShowId(Id(showId)).asFlow().mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.transaction { database.trailersQueries.delete(Id(id)) }
    }

    override fun deleteAll() {
        database.transaction { database.trailersQueries.deleteAll() }
    }
}
