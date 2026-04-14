package com.thomaskioko.tvmaniac.data.cast.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCastDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : CastDao {
    override fun upsert(entity: Casts) {
        database.castQueries.upsert(
            id = entity.id,
            trakt_id = entity.trakt_id,
            show_trakt_id = entity.show_trakt_id,
            season_id = entity.season_id,
            name = entity.name,
            character_name = entity.character_name,
            profile_path = entity.profile_path,
            popularity = entity.popularity,
        )
    }

    override suspend fun getShowCast(traktId: Long): List<ShowCast> =
        withContext(dispatcher.io) {
            database.castQueries.showCast(Id(traktId)).executeAsList()
        }

    override fun observeShowCast(traktId: Long): Flow<List<ShowCast>> =
        database.castQueries.showCast(Id(traktId)).asFlow().mapToList(dispatcher.io)

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
        database.castQueries.seasonCast(Id(seasonId)).asFlow().mapToList(dispatcher.io)
}
