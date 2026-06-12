package com.thomaskioko.tvmaniac.seasons.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetSeasonByShowAndNumber
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.LatestSeasonPerFollowedShow
import com.thomaskioko.tvmaniac.db.Season
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonsDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
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

    override fun observeSeasonsByShowId(showId: Long, includeSpecials: Boolean): Flow<List<ShowSeasons>> {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return flowOf(emptyList())
        return seasonQueries.showSeasons(
            showId = internalShowId,
            includeSpecials = if (includeSpecials) 1L else 0L,
        ).asFlow().mapToList(dispatcher.io)
    }

    override fun fetchShowSeasons(showId: Long, includeSpecials: Boolean): List<ShowSeasons> {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return emptyList()
        return seasonQueries.showSeasons(
            showId = internalShowId,
            includeSpecials = if (includeSpecials) 1L else 0L,
        ).executeAsList()
    }

    override suspend fun getSeasonByShowAndNumber(showId: Long, seasonNumber: Long): GetSeasonByShowAndNumber? =
        withContext(dispatcher.databaseRead) {
            val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return@withContext null
            seasonQueries.getSeasonByShowAndNumber(
                showId = internalShowId,
                seasonNumber = seasonNumber,
            ).executeAsOneOrNull()
        }

    override suspend fun getLatestSeasonPerFollowedShow(): List<LatestSeasonPerFollowedShow> =
        withContext(dispatcher.databaseRead) {
            seasonQueries.latestSeasonPerFollowedShow().executeAsList()
        }

    override fun updateImageUrl(seasonId: Long, imageUrl: String) {
        seasonQueries.updateImageUrl(image_url = imageUrl, id = Id(seasonId))
    }

    override fun delete(showId: Long) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        seasonQueries.delete(internalShowId)
    }

    override fun deleteAll() {
        database.transaction { seasonQueries.deleteAll() }
    }
}
