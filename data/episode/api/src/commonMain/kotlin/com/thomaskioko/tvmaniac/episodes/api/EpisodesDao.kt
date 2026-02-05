package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.db.GetEpisodeByShowSeasonEpisodeNumber
import com.thomaskioko.tvmaniac.db.NextEpisodeForShow
import com.thomaskioko.tvmaniac.db.UpcomingEpisodesFromFollowedShows
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import com.thomaskioko.tvmaniac.db.Episode as EpisodeCache

public interface EpisodesDao {

    public fun insert(entity: EpisodeCache)

    public fun insert(list: List<EpisodeCache>)

    public fun delete(id: Long)

    public fun deleteAll()

    public suspend fun getEpisodeByShowSeasonEpisodeNumber(
        showTraktId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ): GetEpisodeByShowSeasonEpisodeNumber?

    public suspend fun updateFirstAired(
        showId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        firstAired: Long,
    )

    public fun observeUpcomingEpisodesFromFollowedShows(
        limit: Duration,
    ): Flow<List<UpcomingEpisodesFromFollowedShows>>

    public suspend fun getUpcomingEpisodesFromFollowedShows(
        limit: Duration,
    ): List<UpcomingEpisodesFromFollowedShows>

    public fun observeNextEpisodeForShow(
        showTraktId: Long,
        includeSpecials: Boolean,
    ): Flow<NextEpisodeForShow?>

    public suspend fun getNextEpisodeForShow(
        showTraktId: Long,
        includeSpecials: Boolean,
    ): NextEpisodeForShow?
}
