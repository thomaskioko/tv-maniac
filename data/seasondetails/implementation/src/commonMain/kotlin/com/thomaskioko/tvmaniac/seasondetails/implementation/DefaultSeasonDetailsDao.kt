package com.thomaskioko.tvmaniac.seasondetails.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SeasonDetails
import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonDetailsDao(
    private val database: TvManiacDatabase,
    private val dispatcher: AppCoroutineDispatchers,
    private val dateTimeProvider: DateTimeProvider,
) : SeasonDetailsDao {

    private val seasonQueries
        get() = database.seasonsQueries

    override fun fetchSeasonDetails(showTraktId: Long, seasonNumber: Long): SeasonDetailsWithEpisodes? {
        val queryResult = database.seasonsQueries
            .seasonDetails(
                showTraktId = Id(showTraktId),
                seasonNumber = seasonNumber,
            )
            .executeAsList()
        return if (queryResult.isEmpty()) null else mapSeasonDetails(queryResult)
    }

    override fun observeSeasonEpisodeDetails(
        showTraktId: Long,
        seasonNumber: Long,
    ): Flow<SeasonDetailsWithEpisodes?> =
        seasonQueries.seasonDetails(showTraktId = Id(showTraktId), seasonNumber = seasonNumber)
            .asFlow()
            .map { it.executeAsList() }
            .map { if (it.isEmpty()) null else mapSeasonDetails(it) }

    override fun delete(showTraktId: Long) {
        seasonQueries.delete(Id(showTraktId))
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

    private fun mapSeasonDetails(resultItem: List<SeasonDetails>): SeasonDetailsWithEpisodes {
        val seasonDetails = resultItem.first()
        val episodeList = mapEpisode(resultItem)

        return SeasonDetailsWithEpisodes(
            seasonId = seasonDetails.season_id.id,
            name = seasonDetails.season_title,
            seasonNumber = seasonDetails.season_number,
            seasonOverview = seasonDetails.overview ?: "",
            showTraktId = seasonDetails.show_trakt_id.id,
            showTmdbId = seasonDetails.show_tmdb_id.id,
            showTitle = seasonDetails.show_title,
            imageUrl = seasonDetails.season_image_url,
            episodes = episodeList,
            episodeCount = episodeList.size.toLong(),
        )
    }

    private fun mapEpisode(resultItem: List<SeasonDetails>): List<EpisodeDetails> {
        return resultItem.mapNotNull { seasonDetails ->
            seasonDetails.episode_id?.let { episodeId ->
                val airDate = seasonDetails.episode_air_date
                val daysUntilAir = dateTimeProvider.calculateDaysUntilAir(airDate)
                val hasAired = airDate != null && (daysUntilAir == null || daysUntilAir <= 0)
                EpisodeDetails(
                    id = episodeId.id,
                    seasonId = seasonDetails.season_id.id,
                    name = seasonDetails.episode_title ?: "",
                    seasonNumber = seasonDetails.season_number,
                    episodeNumber = seasonDetails.episode_number ?: 0,
                    overview = seasonDetails.overview ?: "",
                    voteAverage = seasonDetails.ratings ?: 0.0,
                    voteCount = seasonDetails.vote_count ?: 0,
                    stillPath = seasonDetails.episode_image_url,
                    airDate = airDate,
                    runtime = seasonDetails.runtime ?: 0,
                    isWatched = seasonDetails.is_watched == 1L,
                    daysUntilAir = daysUntilAir,
                    hasAired = hasAired,
                )
            }
        }
    }
}
