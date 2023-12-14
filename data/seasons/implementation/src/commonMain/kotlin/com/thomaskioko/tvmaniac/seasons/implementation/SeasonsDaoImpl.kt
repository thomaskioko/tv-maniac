package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.Season
import com.thomaskioko.tvmaniac.core.db.SeasonEpisodeDetailsById
import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonsDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsDao {

    private val seasonQueries get() = database.seasonQueries

    override fun upsert(season: Season) {
        database.transaction {
            seasonQueries.upsert(
                id = season.id,
                show_id = season.show_id,
                season_number = season.season_number,
                episode_count = season.episode_count,
                title = season.title,
                overview = season.overview,
            )
        }
    }

    override fun upsert(entityList: List<Season>) {
        entityList.forEach { upsert(it) }
    }

    override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> {
        return database.seasonQueries.showSeasons(Id(id))
            .asFlow()
            .mapToList(dispatcher.io)
    }

    override fun fetchSeasonDetails(id: Long): List<SeasonEpisodeDetailsById> =
        database.seasonQueries.seasonEpisodeDetailsById(id = Id(id))
            .executeAsList()

    override fun fetchShowSeasons(id: Long): List<ShowSeasons> =
        database.seasonQueries.showSeasons(id = Id(id))
            .executeAsList()

    override fun observeSeasonEpisodeDetailsById(showId: Long): Flow<List<SeasonEpisodeDetailsById>> =
        database.seasonQueries.seasonEpisodeDetailsById(id = Id(showId))
            .asFlow()
            .mapToList(dispatcher.io)

    override fun delete(id: Long) {
        seasonQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction {
            seasonQueries.deleteAll()
        }
    }
}
