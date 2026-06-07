package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.tmdb.api.model.CreditsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.EpisodesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.ImagesResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.TmdbSeasonDetailsResponse
import com.thomaskioko.tvmaniac.tmdb.api.model.VideosResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test

internal class TmdbSeasonMapperTest {

    private val mapper = TmdbSeasonMapper(
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = FakeDateTimeProvider(),
    )

    @Test
    fun `should map season keyed by tmdb id with episode count`() {
        val season = mapper.mapToSeason(seasonResponse(), Id(SHOW_ID))

        season.id.id shouldBe 5001L
        season.show_id.id shouldBe SHOW_ID
        season.season_number shouldBe 1L
        season.episode_count shouldBe 2L
        season.title shouldBe "Season 1"
        season.overview shouldBe "S1 overview"
    }

    @Test
    fun `should map episodes keyed by tmdb id with season and show ids`() {
        val episodes = mapper.mapToEpisodes(seasonResponse(), Id(SHOW_ID))

        episodes.size shouldBe 2

        val first = episodes.first()
        first.id.id shouldBe 101L
        first.season_id.id shouldBe 5001L
        first.show_id.id shouldBe SHOW_ID
        first.episode_number shouldBe 1L
        first.title shouldBe "Pilot"
        first.overview shouldBe "Pilot overview"
        first.runtime shouldBe 42L
        first.vote_count shouldBe 100L
        first.ratings shouldBe 8.5
        first.trakt_id.shouldBeNull()
        first.first_aired shouldBe LocalDate(2024, 6, 1).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    @Test
    fun `should default missing runtime and null air date and still path`() {
        val second = mapper.mapToEpisodes(seasonResponse(), Id(SHOW_ID))[1]

        second.runtime shouldBe 0L
        second.image_url.shouldBeNull()
        second.first_aired.shouldBeNull()
    }

    @Test
    fun `should map a specials season with season number zero`() {
        val response = seasonResponse().copy(seasonNumber = 0, id = 5000)

        val season = mapper.mapToSeason(response, Id(SHOW_ID))
        season.season_number shouldBe 0L
        season.id.id shouldBe 5000L
        mapper.mapToEpisodes(response, Id(SHOW_ID)).size shouldBe 2
    }

    private fun seasonResponse(): TmdbSeasonDetailsResponse = TmdbSeasonDetailsResponse(
        airDate = "2024-06-01",
        episodes = arrayListOf(
            EpisodesResponse(
                airDate = "2024-06-01",
                episodeNumber = 1,
                id = 101,
                name = "Pilot",
                overview = "Pilot overview",
                runtime = 42,
                seasonNumber = 1,
                showId = 1,
                stillPath = "/pilot.jpg",
                voteAverage = 8.5,
                voteCount = 100,
            ),
            EpisodesResponse(
                airDate = null,
                episodeNumber = 2,
                id = 102,
                name = "Episode 2",
                overview = "E2 overview",
                runtime = null,
                seasonNumber = 1,
                showId = 1,
                stillPath = null,
                voteAverage = 7.0,
                voteCount = 50,
            ),
        ),
        name = "Season 1",
        overview = "S1 overview",
        id = 5001,
        posterPath = "/season.jpg",
        seasonNumber = 1,
        voteAverage = 8.0,
        videos = VideosResponse(arrayListOf()),
        images = ImagesResponse(arrayListOf()),
        credits = CreditsResponse(),
    )

    private companion object {
        private const val SHOW_ID = 1L
    }
}
