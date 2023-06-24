package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.db.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.db.Season_episodes
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsDao {

    override fun insert(entity: Season_episodes) {
        database.transaction {
            database.seasonEpisodesQueries.insertOrReplace(
                show_id = entity.show_id,
                season_id = entity.season_id,
                season_number = entity.season_number,
            )
        }
    }

    override fun observeShowEpisodes(showId: Long): Flow<List<SeasonWithEpisodes>> =
        database.seasonEpisodesQueries.seasonWithEpisodes(showId)
            .asFlow()
            .mapToList(dispatcher.io)

    override fun delete(id: Long) {
        database.seasonEpisodesQueries.delete(id)
    }

    override fun deleteAll() {
        database.transaction {
            database.seasonEpisodesQueries.deleteAll()
        }
    }
}
