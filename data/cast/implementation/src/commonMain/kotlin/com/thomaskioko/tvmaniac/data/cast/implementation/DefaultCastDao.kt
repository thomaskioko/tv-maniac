package com.thomaskioko.tvmaniac.data.cast.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastDao
import com.thomaskioko.tvmaniac.db.Cast_appearance
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultCastDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : CastDao {
    override fun upsert(entity: Casts) {
        database.castQueries.upsert(
            id = entity.id,
            name = entity.name,
            character_name = entity.character_name,
            profile_path = entity.profile_path,
            popularity = entity.popularity,
        )
    }

    override fun upsert(entity: Cast_appearance) {
        database.castAppearanceQueries.upsert(
            cast_id = entity.cast_id,
            show_id = entity.show_id,
            season_id = entity.season_id,
        )
    }

    override fun observeShowCast(id: Long): Flow<List<ShowCast>> =
        database.castAppearanceQueries.showCast(Id(id)).asFlow().mapToList(dispatcher.io)

    override fun observeSeasonCast(id: Long): Flow<List<SeasonCast>> =
        database.castAppearanceQueries.seasonCast(Id(id)).asFlow().mapToList(dispatcher.io)
}
