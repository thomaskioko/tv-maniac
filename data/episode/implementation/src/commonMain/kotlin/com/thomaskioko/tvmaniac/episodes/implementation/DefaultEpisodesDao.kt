package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.db.GetEpisodeByShowSeasonEpisodeNumber
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.NextEpisodeForShow
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.UpcomingEpisodesFromFollowedShows
import com.thomaskioko.tvmaniac.episodes.api.EpisodesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import com.thomaskioko.tvmaniac.db.Episode as EpisodeCache

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultEpisodesDao(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : EpisodesDao {

    private val episodeQueries
        get() = database.episodesQueries

    override fun insert(entity: EpisodeCache) {
        database.transaction {
            episodeQueries.upsert(
                id = entity.id,
                season_id = entity.season_id,
                title = entity.title,
                overview = entity.overview,
                runtime = entity.runtime,
                episode_number = entity.episode_number,
                image_url = entity.image_url,
                show_trakt_id = entity.show_trakt_id,
                vote_count = entity.vote_count,
                ratings = entity.ratings,
                trakt_id = entity.trakt_id,
                first_aired = entity.first_aired,
            )
        }
    }

    override fun insert(list: List<EpisodeCache>) {
        list.forEach { insert(it) }
    }

    override fun delete(id: Long) {
        episodeQueries.delete(Id(id))
    }

    override fun deleteAll() {
        database.transaction { episodeQueries.deleteAll() }
    }

    override suspend fun getEpisodeByShowSeasonEpisodeNumber(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): GetEpisodeByShowSeasonEpisodeNumber? = withContext(dispatchers.databaseRead) {
        episodeQueries.getEpisodeByShowSeasonEpisodeNumber(
            showTraktId = Id(showTraktId),
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
        ).executeAsOneOrNull()
    }

    override suspend fun updateFirstAired(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        firstAired: Long,
    ): Unit = withContext(dispatchers.databaseWrite) {
        episodeQueries.updateFirstAired(
            showId = Id(showId),
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            firstAired = firstAired,
        )
    }

    override fun observeUpcomingEpisodesFromFollowedShows(
        fromEpoch: Long,
        toEpoch: Long,
    ): Flow<List<UpcomingEpisodesFromFollowedShows>> =
        episodeQueries.upcomingEpisodesFromFollowedShows(fromEpoch, toEpoch)
            .asFlow()
            .mapToList(dispatchers.databaseRead)

    override suspend fun getUpcomingEpisodesFromFollowedShows(
        fromEpoch: Long,
        toEpoch: Long,
    ): List<UpcomingEpisodesFromFollowedShows> = withContext(dispatchers.databaseRead) {
        episodeQueries.upcomingEpisodesFromFollowedShows(fromEpoch, toEpoch).executeAsList()
    }

    override fun observeNextEpisodeForShow(
        showTraktId: Long,
        includeSpecials: Boolean,
    ): Flow<NextEpisodeForShow?> =
        database.showsNextToWatchQueries.nextEpisodeForShow(
            showTraktId = Id<TraktId>(showTraktId),
            includeSpecials = if (includeSpecials) 1L else 0L,
        ).asFlow().mapToOneOrNull(dispatchers.databaseRead)
}
