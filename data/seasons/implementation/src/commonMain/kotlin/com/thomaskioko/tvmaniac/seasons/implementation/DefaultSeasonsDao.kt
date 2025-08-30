package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultSeasonsDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsDao {

    private val seasonQueries
        get() = database.seasonsQueries

    override fun upsert(season: Season) {
        database.transaction {
            seasonQueries.upsert(
                id = season.id,
                show_id = season.show_id,
                season_number = season.season_number,
                episode_count = season.episode_count,
                title = season.title,
                overview = season.overview,
                image_url = season.image_url,
            )
        }
    }

    override fun upsert(entityList: List<Season>) {
        entityList.forEach { upsert(it) }
    }

    override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> {
        return database.seasonsQueries.showSeasons(Id(id)).asFlow().mapToList(dispatcher.io)
    }

    override fun fetchShowSeasons(id: Long): List<ShowSeasons> =
        database.seasonsQueries.showSeasons(id = Id(id)).executeAsList()

    override fun delete(id: Long) {
        seasonQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { seasonQueries.deleteAll() }
    }
}
