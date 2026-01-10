package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetSeasonByShowAndNumber
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonsDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonsDao {

    private val seasonQueries
        get() = database.seasonsQueries

    override fun upsert(season: Season) {
        database.transaction {
            seasonQueries.upsert(
                id = season.id,
                show_trakt_id = season.show_trakt_id,
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

    override fun observeSeasonsByShowTraktId(showTraktId: Long, includeSpecials: Boolean): Flow<List<ShowSeasons>> {
        return database.seasonsQueries.showSeasons(
            showTraktId = Id(showTraktId),
            includeSpecials = if (includeSpecials) 1L else 0L,
        ).asFlow().mapToList(dispatcher.io)
    }

    override fun fetchShowSeasons(showTraktId: Long, includeSpecials: Boolean): List<ShowSeasons> =
        database.seasonsQueries.showSeasons(
            showTraktId = Id(showTraktId),
            includeSpecials = if (includeSpecials) 1L else 0L,
        ).executeAsList()

    override suspend fun getSeasonByShowAndNumber(showTraktId: Long, seasonNumber: Long): GetSeasonByShowAndNumber? =
        withContext(dispatcher.databaseRead) {
            seasonQueries.getSeasonByShowAndNumber(
                showTraktId = Id(showTraktId),
                seasonNumber = seasonNumber,
            ).executeAsOneOrNull()
        }

    override fun updateImageUrl(seasonId: Long, imageUrl: String) {
        seasonQueries.updateImageUrl(image_url = imageUrl, id = Id(seasonId))
    }

    override fun delete(showTraktId: Long) {
        seasonQueries.delete(Id(showTraktId))
    }

    override fun deleteAll() {
        database.transaction { seasonQueries.deleteAll() }
    }
}
