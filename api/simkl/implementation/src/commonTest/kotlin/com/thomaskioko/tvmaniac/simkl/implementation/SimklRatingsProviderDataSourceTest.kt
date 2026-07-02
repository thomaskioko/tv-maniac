package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingValue
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatings
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsCountBucket
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowSummaryResponse
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklRatingsRemoteDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SimklRatingsProviderDataSourceTest {

    private val remoteDataSource = FakeSimklRatingsRemoteDataSource()
    private val source = SimklRatingsProviderDataSource(remoteDataSource)

    @Test
    fun `should report simkl as its provider`() {
        source.provider shouldBe AccountProvider.SIMKL
    }

    @Test
    fun `should map add rating response to success given simkl accepts the rating`() = runTest {
        remoteDataSource.setAddRatingsResponse(ApiResponse.Success(SimklAddRatingsResponse(added = SimklRatingsCountBucket(shows = 1))))

        val result = source.addShowRating(tmdbId = 10, rating = 8)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should map remove rating response to success given simkl accepts the removal`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(
            ApiResponse.Success(SimklRemoveRatingsResponse(deleted = SimklRatingsCountBucket(shows = 1))),
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
    fun `should map show summary to community rating given simkl returns a rating`() = runTest {
        remoteDataSource.setShowSummaryResponse(
            ApiResponse.Success(SimklShowSummaryResponse(ratings = SimklRatings(simkl = SimklRatingValue(rating = 7.9, votes = 4321)))),
        )

        val result = source.getShowCommunityRating(providerShowId = 39687)

        val success = result.shouldBeInstanceOf<ApiResponse.Success<CommunityRating>>()
        success.body shouldBe CommunityRating(rating = 7.9, votes = 4321)
    }

    @Test
    fun `should return http error given simkl show summary has no rating`() = runTest {
        remoteDataSource.setShowSummaryResponse(ApiResponse.Success(SimklShowSummaryResponse(ratings = null)))

        val result = source.getShowCommunityRating(providerShowId = 39687)

        result.shouldBeInstanceOf<ApiResponse.Error.HttpError<CommunityRating>>()
    }

    @Test
    fun `should return success without calling remote source given season rating is added`() = runTest {
        remoteDataSource.setAddRatingsResponse(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "unsupported"))

        val result = source.addSeasonRating(seasonTmdbId = 10, rating = 8)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should return success without calling remote source given season rating is removed`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "unsupported"))

        val result = source.removeSeasonRating(seasonTmdbId = 10)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should return success without calling remote source given episode rating is added`() = runTest {
        remoteDataSource.setAddRatingsResponse(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "unsupported"))

        val result = source.addEpisodeRating(episodeTmdbId = 20, rating = 9)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }

    @Test
    fun `should return success without calling remote source given episode rating is removed`() = runTest {
        remoteDataSource.setRemoveRatingsResponse(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "unsupported"))

        val result = source.removeEpisodeRating(episodeTmdbId = 20)

        result.shouldBeInstanceOf<ApiResponse.Success<Unit>>()
    }
}
