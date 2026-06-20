package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.db.GetSeasonWithShowInfo
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.db.ShowIdResolver
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonDetailsDao(
    private val database: TvManiacDatabase,
    private val showIdResolver: ShowIdResolver,
    private val dispatcher: AppCoroutineDispatchers,
) : SeasonDetailsDao {

    private val seasonQueries
        get() = database.seasonsQueries

    private val episodesQueries
        get() = database.episodesQueries

    override fun observeSeasonDetails(
        showId: Long,
        seasonNumber: Long,
    ): Flow<List<SeasonDetails>> {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return flowOf(emptyList())
        return seasonQueries.seasonDetails(showId = internalShowId, seasonNumber = seasonNumber)
            .asFlow()
            .mapToList(dispatcher.io)
    }

    override fun observeSeasonWithShowInfo(
        showId: Long,
        seasonNumber: Long,
    ): Flow<GetSeasonWithShowInfo?> {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return flowOf(null)
        return seasonQueries.getSeasonWithShowInfo(showId = internalShowId, seasonNumber = seasonNumber)
            .asFlow()
            .mapToOneOrNull(dispatcher.io)
    }

    override fun observeEpisodesBySeasonId(seasonId: Long): Flow<List<EpisodesBySeasonId>> =
        episodesQueries.episodesBySeasonId(seasonId = Id(seasonId))
            .asFlow()
            .mapToList(dispatcher.io)

    override fun delete(showId: Long) {
        val internalShowId = showIdResolver.showIdForTmdbId(showId) ?: return
        seasonQueries.delete(internalShowId)
    }

    override fun deleteAll() {
        database.transaction { seasonQueries.deleteAll() }
    }

    override fun upsertSeasonImage(seasonId: Long, imageUrl: String) {
        database.transaction {
            database.seasonImagesQueries.upsert(
                season_id = Id(seasonId),
                image_url = imageUrl,
            )
        }
    }

    override fun fetchSeasonImages(id: Long): List<SeasonImages> =
        database.seasonImagesQueries.seasonImages(Id(id)).executeAsList()

    override fun observeSeasonImages(id: Long): Flow<List<SeasonImages>> =
        database.seasonImagesQueries.seasonImages(Id(id)).asFlow().mapToList(dispatcher.io)
}
