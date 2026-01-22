package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.seasondetails.api.model.EpisodeDetails
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

internal object MockData {
    const val TEST_SHOW_ID = 1L
    const val TEST_SHOW_NAME = "Test Show 1"
    const val TEST_SHOW_OVERVIEW = "Test overview 1"

    const val SEASON_0_ID = 10L
    const val SEASON_1_ID = 11L
    const val SEASON_2_ID = 12L
    const val SEASON_0_NUMBER = 0L
    const val SEASON_1_NUMBER = 1L
    const val SEASON_2_NUMBER = 2L
    const val SEASON_0_EPISODE_COUNT = 2
    const val SEASON_1_EPISODE_COUNT = 7
    const val SEASON_2_EPISODE_COUNT = 13

    private fun LocalDate.toEpochMillis(): Long =
        atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

    val testShowSeasons = listOf(
        ShowSeasons(
            show_trakt_id = Id<TraktId>(TEST_SHOW_ID),
            season_id = Id(SEASON_1_ID),
            season_title = "Season 1",
            season_number = SEASON_1_NUMBER,
        ),
        ShowSeasons(
            show_trakt_id = Id<TraktId>(TEST_SHOW_ID),
            season_id = Id(SEASON_2_ID),
            season_title = "Season 2",
            season_number = SEASON_2_NUMBER,
        ),
    )

    val season1Episodes = (1..SEASON_1_EPISODE_COUNT).map { episodeNumber ->
        EpisodeDetails(
            id = 100L + episodeNumber,
            seasonId = SEASON_1_ID,
            name = "Episode $episodeNumber",
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = episodeNumber.toLong(),
            runtime = 45L,
            overview = "Episode $episodeNumber overview",
            voteAverage = 8.5,
            voteCount = 50L,
            stillPath = "/episode$episodeNumber.jpg",
            firstAired = LocalDate(2023, 1, episodeNumber).toEpochMillis(),
            isWatched = false,
            daysUntilAir = null,
            hasAired = true,
        )
    }

    val season2Episodes = (1..SEASON_2_EPISODE_COUNT).map { episodeNumber ->
        EpisodeDetails(
            id = 200L + episodeNumber,
            seasonId = SEASON_2_ID,
            name = "Episode $episodeNumber",
            seasonNumber = SEASON_2_NUMBER,
            episodeNumber = episodeNumber.toLong(),
            runtime = 45L,
            overview = "Season 2 Episode $episodeNumber overview",
            voteAverage = 9.0,
            voteCount = 75L,
            stillPath = "/s2e$episodeNumber.jpg",
            firstAired = LocalDate(2023, 2, 20).toEpochMillis(),
            isWatched = false,
            daysUntilAir = null,
            hasAired = true,
        )
    }

    val season1Details = SeasonDetailsWithEpisodes(
        seasonId = SEASON_1_ID,
        showTraktId = TEST_SHOW_ID,
        showTmdbId = TEST_SHOW_ID,
        name = "Season 1",
        showTitle = TEST_SHOW_NAME,
        seasonOverview = "First season",
        imageUrl = "/season1.jpg",
        seasonNumber = SEASON_1_NUMBER,
        episodeCount = SEASON_1_EPISODE_COUNT.toLong(),
        episodes = season1Episodes,
    )

    val season2Details = SeasonDetailsWithEpisodes(
        seasonId = SEASON_2_ID,
        showTraktId = TEST_SHOW_ID,
        showTmdbId = TEST_SHOW_ID,
        name = "Season 2",
        showTitle = TEST_SHOW_NAME,
        seasonOverview = "Second season",
        imageUrl = "/season2.jpg",
        seasonNumber = SEASON_2_NUMBER,
        episodeCount = SEASON_2_EPISODE_COUNT.toLong(),
        episodes = season2Episodes,
    )

    fun createSeason2EpisodesWithWatchedState(watchedEpisodeNumber: Long): List<EpisodeDetails> =
        (1..4).map { episodeNumber ->
            EpisodeDetails(
                id = 200L + episodeNumber,
                seasonId = SEASON_2_ID,
                name = "S2E$episodeNumber",
                seasonNumber = SEASON_2_NUMBER,
                episodeNumber = episodeNumber.toLong(),
                runtime = 45L,
                overview = "Overview",
                voteAverage = 8.0,
                voteCount = 100L,
                stillPath = null,
                firstAired = LocalDate(2023, 1, episodeNumber).toEpochMillis(),
                isWatched = episodeNumber.toLong() == watchedEpisodeNumber,
                daysUntilAir = null,
                hasAired = true,
            )
        }

    fun createSeason1EpisodesForContinueTracking(): List<EpisodeDetails> =
        listOf(
            EpisodeDetails(
                id = 101L,
                seasonId = SEASON_1_ID,
                name = "S1E1",
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
                runtime = 45L,
                overview = "Overview",
                voteAverage = 8.0,
                voteCount = 100L,
                stillPath = null,
                firstAired = LocalDate(2023, 1, 1).toEpochMillis(),
                isWatched = false,
                daysUntilAir = null,
                hasAired = true,
            ),
            EpisodeDetails(
                id = 102L,
                seasonId = SEASON_1_ID,
                name = "S1E2",
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 2L,
                runtime = 45L,
                overview = "Overview",
                voteAverage = 8.0,
                voteCount = 100L,
                stillPath = null,
                firstAired = LocalDate(2023, 1, 2).toEpochMillis(),
                isWatched = false,
                daysUntilAir = null,
                hasAired = true,
            ),
        )

    fun createSeasonDetailsForContinueTracking(
        seasonId: Long,
        seasonNumber: Long,
        episodes: List<EpisodeDetails>,
    ) = SeasonDetailsWithEpisodes(
        seasonId = seasonId,
        showTraktId = TEST_SHOW_ID,
        showTmdbId = TEST_SHOW_ID,
        name = "Season $seasonNumber",
        showTitle = "Test Show",
        seasonOverview = "Season $seasonNumber overview",
        imageUrl = null,
        seasonNumber = seasonNumber,
        episodeCount = episodes.size.toLong(),
        episodes = episodes,
    )

    fun createFutureEpisodesForSeason(
        seasonId: Long,
        seasonNumber: Long,
        episodeCount: Int = 5,
        daysUntilAir: Int = 7,
    ): List<EpisodeDetails> =
        (1..episodeCount).map { episodeNumber ->
            EpisodeDetails(
                id = (seasonNumber * 100 + episodeNumber),
                seasonId = seasonId,
                name = "S${seasonNumber}E$episodeNumber",
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber.toLong(),
                runtime = 45L,
                overview = "Future episode overview",
                voteAverage = 0.0,
                voteCount = 0L,
                stillPath = null,
                firstAired = null,
                isWatched = false,
                daysUntilAir = daysUntilAir,
                hasAired = false,
            )
        }
}
