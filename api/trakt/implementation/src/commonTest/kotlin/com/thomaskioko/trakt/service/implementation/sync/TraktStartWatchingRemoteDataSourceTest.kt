package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.trakt.api.model.IdsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.ShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktListRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class TraktStartWatchingRemoteDataSourceTest {

    private val remoteDataSource = FakeTraktListRemoteDataSource()
    private val source = TraktStartWatchingRemoteDataSource(remoteDataSource)

    @Test
    fun `should report trakt as its provider`() {
        source.provider shouldBe SyncProviderSource.TRAKT
    }

    @Test
    fun `should map watchlist shows to plan-to-watch given successful response`() = runTest {
        remoteDataSource.setWatchList(
            ApiResponse.Success(
                listOf(
                    traktFollowedShow(
                        traktId = 1,
                        tmdbId = 10,
                        title = "Severance",
                        year = 2022,
                        listedAt = "2025-01-01T00:00:00Z",
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body shouldBe listOf(
            RemotePlanToWatchShow(
                tmdbId = 10,
                imdbId = null,
                providerShowId = "1",
                provider = SyncProviderSource.TRAKT,
                title = "Severance",
                year = 2022,
                followedAt = Instant.parse("2025-01-01T00:00:00Z"),
            ),
        )
    }

    @Test
    fun `should return empty list given no watchlist items`() = runTest {
        remoteDataSource.setWatchList(ApiResponse.Success(emptyList()))

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body shouldBe emptyList()
    }

    @Test
    fun `should preserve unauthenticated given remote source returns unauthenticated`() = runTest {
        remoteDataSource.setWatchList(ApiResponse.Unauthenticated)

        val result = source.getPlanToWatch()

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should map imdb id given show has imdb id`() = runTest {
        remoteDataSource.setWatchList(
            ApiResponse.Success(
                listOf(
                    traktFollowedShow(
                        traktId = 2,
                        tmdbId = 20,
                        title = "The Bear",
                        year = 2023,
                        listedAt = "2025-06-01T00:00:00Z",
                        imdbId = "tt14452776",
                    ),
                ),
            ),
        )

        val result = source.getPlanToWatch()

        val success = result.shouldBeInstanceOf<ApiResponse.Success<List<RemotePlanToWatchShow>>>()
        success.body.first().imdbId shouldBe "tt14452776"
    }
}

private fun traktFollowedShow(
    traktId: Long,
    tmdbId: Long,
    title: String,
    year: Int?,
    listedAt: String,
    imdbId: String? = null,
): TraktFollowedShowResponse = TraktFollowedShowResponse(
    rank = 1,
    id = traktId.toInt(),
    listedAt = listedAt,
    type = "show",
    show = ShowResponse(
        title = title,
        year = year,
        ids = IdsResponse(
            slug = "slug-$traktId",
            trakt = traktId,
            tmdb = tmdbId,
            imdb = imdbId,
        ),
    ),
)
