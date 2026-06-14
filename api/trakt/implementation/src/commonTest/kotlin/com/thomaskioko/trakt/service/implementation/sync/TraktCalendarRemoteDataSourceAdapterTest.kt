package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarEpisode
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarShow
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktCalendarRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class TraktCalendarRemoteDataSourceAdapterTest {

    private val traktDataSource = FakeTraktCalendarRemoteDataSource()
    private val adapter = TraktCalendarRemoteDataSourceAdapter(traktDataSource)

    @Test
    fun `should report trakt as its provider`() {
        adapter.provider shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should return empty list given trakt calendar returns no entries`() = runTest {
        traktDataSource.setCalendarEntries(ApiResponse.Success(emptyList()))

        val result = adapter.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body shouldBe emptyList()
    }

    @Test
    fun `should map trakt response to remote calendar entries given valid calendar data`() = runTest {
        traktDataSource.setCalendarEntries(
            ApiResponse.Success(
                listOf(
                    traktCalendarResponse(
                        firstAired = "2026-04-19T03:00:00.000Z",
                        tmdbId = 1396L,
                        showTitle = "Breaking Bad",
                        episodeTitle = "Pilot",
                        seasonNumber = 1,
                        episodeNumber = 1,
                        runtime = 50,
                        overview = "Walt cooks meth.",
                        rating = 8.5,
                        votes = 120,
                    ),
                ),
            ),
        )

        val result = adapter.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        val entry = success.body.single()
        entry.tmdbId shouldBe 1396L
        entry.showTitle shouldBe "Breaking Bad"
        entry.episodeTitle shouldBe "Pilot"
        entry.seasonNumber shouldBe 1
        entry.episodeNumber shouldBe 1
        entry.firstAiredIso shouldBe "2026-04-19T03:00:00.000Z"
        entry.runtime shouldBe 50
        entry.overview shouldBe "Walt cooks meth."
        entry.rating shouldBe 8.5
        entry.votes shouldBe 120
    }

    @Test
    fun `should use zero as tmdb id given trakt entry has no tmdb id`() = runTest {
        traktDataSource.setCalendarEntries(
            ApiResponse.Success(
                listOf(
                    traktCalendarResponse(
                        firstAired = "2026-04-19T03:00:00.000Z",
                        tmdbId = null,
                        showTitle = "Unknown Show",
                        episodeTitle = null,
                        seasonNumber = 1,
                        episodeNumber = 1,
                        runtime = null,
                        overview = null,
                        rating = null,
                        votes = null,
                    ),
                ),
            ),
        )

        val result = adapter.getCalendarEntries(startDate = "2026-04-19", days = 7)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemoteCalendarEntry>>>()
        success.body.single().tmdbId shouldBe 0L
    }

    @Test
    fun `should propagate error given trakt calendar returns error`() = runTest {
        traktDataSource.setCalendarEntries(
            ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "Server error"),
        )

        val result = adapter.getCalendarEntries(startDate = "2026-04-19", days = 7)

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<*>>()
    }
}

private fun traktCalendarResponse(
    firstAired: String,
    tmdbId: Long?,
    showTitle: String,
    episodeTitle: String?,
    seasonNumber: Int,
    episodeNumber: Int,
    runtime: Int?,
    overview: String?,
    rating: Double?,
    votes: Int?,
): TraktCalendarResponse = TraktCalendarResponse(
    firstAired = firstAired,
    episode = TraktCalendarEpisode(
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        title = episodeTitle,
        ids = EpisodeIds(trakt = 1, tmdb = null),
        runtime = runtime,
        overview = overview,
        rating = rating,
        votes = votes,
    ),
    show = TraktCalendarShow(
        title = showTitle,
        ids = ShowIds(trakt = 1L, tmdb = tmdbId),
    ),
)
