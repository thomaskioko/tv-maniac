package com.thomaskioko.tvmaniac.data.trailers.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SelectByShowTraktId
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
            show_tmdb_id = trailer.show_tmdb_id,
            youtube_url = trailer.youtube_url,
            name = trailer.name,
            site = trailer.site,
            size = trailer.size,
            type = trailer.type,
        )
    }

    override fun getTrailersByShowTraktId(showTraktId: Long): List<SelectByShowTraktId> =
        database.trailersQueries.selectByShowTraktId(Id(showTraktId)).executeAsList()

    override fun observeTrailersByShowTraktId(showTraktId: Long): Flow<List<SelectByShowTraktId>> {
        return database.trailersQueries.selectByShowTraktId(Id(showTraktId)).asFlow().mapToList(dispatchers.io)
    }

    override fun delete(id: Long) {
        database.transaction { database.trailersQueries.delete(Id(id)) }
    }

    override fun deleteAll() {
        database.transaction { database.trailersQueries.deleteAll() }
    }
}
