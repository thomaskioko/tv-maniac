package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.db.GetSeasonWithShowInfo
import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.math.ceil

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultSeasonDetailsRepository(
    private val store: SeasonDetailsStore,
    private val dao: SeasonDetailsDao,
    private val dateTimeProvider: DateTimeProvider,
) : SeasonDetailsRepository {

    private companion object {
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
    override suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean,
    ) {
        when {
            forceRefresh -> store.fresh(param)
            else -> store.get(param)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSeasonDetails(
        param: SeasonDetailsParam,
    ): Flow<SeasonDetailsWithEpisodes> =
        dao.observeSeasonWithShowInfo(param.showTraktId, param.seasonNumber)
            .filterNotNull()
            .flatMapLatest { season ->
                dao.observeEpisodesBySeasonId(season.season_id.id)
                    .map { episodes -> mapToSeasonDetailsWithEpisodes(season, episodes) }
            }

    override fun observeSeasonImages(id: Long): Flow<List<SeasonImages>> = dao.observeSeasonImages(id)

    private fun mapToSeasonDetailsWithEpisodes(
        season: GetSeasonWithShowInfo,
        episodes: List<EpisodesBySeasonId>,
    ): SeasonDetailsWithEpisodes {
        val mappedEpisodes = mapEpisodes(season, episodes)

        return SeasonDetailsWithEpisodes(
            seasonId = season.season_id.id,
            name = season.season_title,
            seasonNumber = season.season_number,
            seasonOverview = season.season_overview,
            showTraktId = season.show_trakt_id.id,
            showTmdbId = season.show_tmdb_id.id,
            showTitle = season.show_title,
            imageUrl = season.season_image_url,
            episodes = mappedEpisodes,
            episodeCount = mappedEpisodes.size.toLong(),
        )
    }

    private fun mapEpisodes(
        season: GetSeasonWithShowInfo,
        episodes: List<EpisodesBySeasonId>,
    ): List<EpisodeDetails> {
        val currentTime = dateTimeProvider.nowMillis()
        return episodes.map { episode ->
            val firstAired = episode.first_aired
            val hasAired = firstAired != null && firstAired <= currentTime
            val daysUntilAir = firstAired
                ?.takeIf { it > currentTime }
                ?.let { airDate -> ceilDaysBetween(currentTime, airDate) }
            EpisodeDetails(
                id = episode.episode_id.id,
                seasonId = episode.season_id.id,
                name = episode.title,
                seasonNumber = season.season_number,
                episodeNumber = episode.episode_number,
                overview = episode.overview,
                voteAverage = episode.ratings,
                voteCount = episode.vote_count,
                stillPath = episode.image_url,
                firstAired = firstAired,
                runtime = episode.runtime ?: 0,
                isWatched = episode.is_watched == 1L,
                daysUntilAir = daysUntilAir,
                hasAired = hasAired,
            )
        }
    }

    private fun ceilDaysBetween(startMillis: Long, endMillis: Long): Int {
        val millisDiff = endMillis - startMillis
        return ceil(millisDiff.toDouble() / MILLIS_PER_DAY).toInt()
    }
}
