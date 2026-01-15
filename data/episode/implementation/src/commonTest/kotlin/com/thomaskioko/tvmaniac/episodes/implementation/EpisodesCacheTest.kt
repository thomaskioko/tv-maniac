package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Episode
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class EpisodesCacheTest : BaseDatabaseTest() {

    private val episodeQueries
        get() = database.episodesQueries

    @Test
    fun insertEpisodes_andEpisodeByEpisodeId_returnsExpectedData() {
        insertShow()

        insertSeason()

        getEpisodeCacheList().insertEpisodeEntityQuery()
        val entity = getEpisodeCacheList().first()

        val queryResult = episodeQueries.episodeDetails(Id(2534997)).executeAsOne()

        queryResult.episode_id.id shouldBe entity.id.id
        queryResult.season_id shouldBe entity.season_id
        queryResult.title shouldBe entity.title
        queryResult.overview shouldBe entity.overview
        queryResult.ratings shouldBe entity.ratings
        queryResult.vote_count shouldBe entity.vote_count
    }

    private fun List<Episode>.insertEpisodeEntityQuery() {
        map { it.insertEpisodeEntityQuery() }
    }

    private fun insertShow() {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(123232),
            tmdb_id = Id<TmdbId>(123232),
            name = "Loki",
            overview = "After stealing the Tesseract, Loki is brought to the Time Variance Authority.",
            language = "en",
            year = "2021-06-09",
            ratings = 8.2,
            vote_count = 7000,
            genres = listOf("Drama", "Science Fiction"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            backdrop_path = "/kXkuE8WXlCD8zMX7MxzBEKmLJUZ.jpg",
        )
    }

    private fun insertSeason() {
        val _ = database.seasonsQueries.upsert(
            id = Id(114355),
            show_trakt_id = Id<TraktId>(123232),
            season_number = 1,
            episode_count = 6,
            title = "Season 1",
            overview = "Season 1 of Loki",
            image_url = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
        )
    }

    private fun Episode.insertEpisodeEntityQuery() {
        val _ = episodeQueries.upsert(
            id = id,
            season_id = season_id,
            title = title,
            overview = overview,
            ratings = ratings,
            episode_number = episode_number,
            runtime = runtime,
            show_trakt_id = show_trakt_id,
            vote_count = vote_count,
            image_url = image_url,
            air_date = air_date,
            trakt_id = trakt_id,
        )
    }

    private fun getEpisodeCacheList() = listOf(
        Episode(
            id = Id(2534997),
            season_id = Id(114355),
            show_trakt_id = Id<TraktId>(123232),
            title = "Glorious Purpose",
            overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
            vote_count = 42,
            ratings = 6.429,
            runtime = 45,
            episode_number = 1,
            image_url = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            air_date = "2021-06-09",
            trakt_id = null,
        ),
        Episode(
            id = Id(2927202),
            season_id = Id(114355),
            show_trakt_id = Id<TraktId>(123232),
            title = "The Variant",
            overview = "Mobius puts Loki to work, but not everyone at TVA is thrilled about the God of Mischief's presence.",
            vote_count = 42,
            ratings = 6.429,
            runtime = 45,
            episode_number = 1,
            image_url = "/yDWJYRAwMNKbIYT8ZB33qy84uzO.jpg",
            air_date = "2021-06-16",
            trakt_id = null,
        ),
    )
}
