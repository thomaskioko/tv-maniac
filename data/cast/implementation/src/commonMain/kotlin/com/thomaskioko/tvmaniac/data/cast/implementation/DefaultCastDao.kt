package com.thomaskioko.tvmaniac.data.cast.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season_cast
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class DefaultCastDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : CastDao {

    override fun upsert(entity: Season_cast) {
        database.transaction {
            database.season_castQueries.upsert(
                id = entity.id,
                season_id = entity.season_id,
                character_name = entity.character_name,
                name = entity.name,
                profile_path = entity.profile_path,
                popularity = entity.popularity,
            )
        }
    }

    override fun fetchSeasonCast(id: Long): List<Season_cast> =
        database.season_castQueries.seasonCast(Id(id))
            .executeAsList()

    override fun observeSeasonCast(id: Long): Flow<List<Season_cast>> =
        database.season_castQueries.seasonCast(Id(id))
            .asFlow()
            .mapToList(dispatcher.io)
}
