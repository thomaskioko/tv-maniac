package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryShow
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsCount
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsNotFound
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserRatingItem
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRatingsRemoteDataSource
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TraktRatingsProviderDataSourceTest {

    private val remoteDataSource = FakeTraktRatingsRemoteDataSource()
    private val source = TraktRatingsProviderDataSource(remoteDataSource)

    @Test
    fun `should report trakt as its provider`() {
        source.provider shouldBe AccountProvider.TRAKT
    }

    @Test
    fun `should return the user rating for the matching trakt id`() = runTest {
        remoteDataSource.setUserShowRatingsResponse(
            ApiResponse.Success(
                listOf(
                    userRatingItem(traktId = 10, rating = 6),
                    userRatingItem(traktId = 20, rating = 9),
                ),
            ),
        )

        source.getShowUserRating(providerShowId = 20).getOrThrow() shouldBe 9
    }

    @Test
    fun `should return null user rating given no trakt id matches`() = runTest {
        remoteDataSource.setUserShowRatingsResponse(
            ApiResponse.Success(listOf(userRatingItem(traktId = 10, rating = 6))),
        )

        source.getShowUserRating(providerShowId = 99).getOrThrow().shouldBeNull()
    }

    private fun userRatingItem(traktId: Long, rating: Int): TraktUserRatingItem = TraktUserRatingItem(
        ratedAt = "2026-01-01T00:00:00Z",
        rating = rating,
        type = "show",
        show = TraktHistoryShow(ids = TraktHistoryShowIds(traktId = traktId)),
    )

    @Test
    fun `should map add rating response to success given trakt accepts the rating`() = runTest {
        remoteDataSource.setAddRatingsResponse(
            ApiResponse.Success(TraktAddRatingsResponse(added = TraktRatingsCount(shows = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.addShowRating(tmdbId = 10, rating = 8)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should map remove rating response to success given trakt accepts the removal`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(
            ApiResponse.Success(TraktRemoveRatingsResponse(deleted = TraktRatingsCount(shows = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.removeShowRating(tmdbId = 10)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should preserve unauthenticated given add rating has no session`() = runTest {
        remoteDataSource.setAddRatingsResponse(ApiResponse.Unauthenticated)

        val result = source.addShowRating(tmdbId = 10, rating = 8)

        result.shouldBeInstanceOf<ApiResponse.Unauthenticated>()
    }

    @Test
    fun `should preserve http error given remove rating fails`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(ApiResponse.Error.HttpError(code = 404, errorBody = null, errorMessage = "not found"))

        val result = source.removeShowRating(tmdbId = 10)

        val error = result.shouldBeInstanceOf<ApiResponse.Error.HttpError<Unit>>()
        error.code shouldBe 404
    }

    @Test
    fun `should map community rating response given trakt returns a rating`() = runTest {
        remoteDataSource.setShowCommunityRatingResponse(
            ApiResponse.Success(TraktRatingResponse(rating = 8.7, votes = 12_345, distribution = emptyMap())),
        )

        val result = source.getShowCommunityRating(providerShowId = 1)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<CommunityRating>>()
        success.body shouldBe CommunityRating(rating = 8.7, votes = 12_345)
    }

    @Test
    fun `should map add season rating response to success given trakt accepts the rating`() = runTest {
        remoteDataSource.setAddRatingsResponse(
            ApiResponse.Success(TraktAddRatingsResponse(added = TraktRatingsCount(seasons = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.addSeasonRating(seasonTmdbId = 20, rating = 7)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should map remove season rating response to success given trakt accepts the removal`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(
            ApiResponse.Success(TraktRemoveRatingsResponse(deleted = TraktRatingsCount(seasons = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.removeSeasonRating(seasonTmdbId = 20)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should map add episode rating response to success given trakt accepts the rating`() = runTest {
        remoteDataSource.setAddRatingsResponse(
            ApiResponse.Success(TraktAddRatingsResponse(added = TraktRatingsCount(episodes = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.addEpisodeRating(episodeTmdbId = 30, rating = 6)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should map remove episode rating response to success given trakt accepts the removal`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(
            ApiResponse.Success(TraktRemoveRatingsResponse(deleted = TraktRatingsCount(episodes = 1), notFound = TraktRatingsNotFound())),
        )

        val result = source.removeEpisodeRating(episodeTmdbId = 30)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }
}
