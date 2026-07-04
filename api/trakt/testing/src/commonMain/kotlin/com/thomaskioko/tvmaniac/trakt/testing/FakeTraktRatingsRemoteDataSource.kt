package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsCount
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsNotFound
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserRatingItem

public class FakeTraktRatingsRemoteDataSource : TraktRatingsRemoteDataSource {

    private var addRatingsResponse: ApiResponse<TraktAddRatingsResponse> =
        ApiResponse.Success(TraktAddRatingsResponse(added = TraktRatingsCount(shows = 1), notFound = TraktRatingsNotFound()))
    private var removeRatingsResponse: ApiResponse<TraktRemoveRatingsResponse> =
        ApiResponse.Success(TraktRemoveRatingsResponse(deleted = TraktRatingsCount(shows = 1), notFound = TraktRatingsNotFound()))
    private var userShowRatingsResponse: ApiResponse<List<TraktUserRatingItem>> = ApiResponse.Success(emptyList())
    private var showCommunityRatingResponse: ApiResponse<TraktRatingResponse> =
        ApiResponse.Success(TraktRatingResponse(rating = 0.0, votes = 0, distribution = emptyMap()))
    private var seasonCommunityRatingResponse: ApiResponse<TraktRatingResponse> =
        ApiResponse.Success(TraktRatingResponse(rating = 0.0, votes = 0, distribution = emptyMap()))
    private var episodeCommunityRatingResponse: ApiResponse<TraktRatingResponse> =
        ApiResponse.Success(TraktRatingResponse(rating = 0.0, votes = 0, distribution = emptyMap()))

    public fun setAddRatingsResponse(response: ApiResponse<TraktAddRatingsResponse>) {
        addRatingsResponse = response
    }

    public fun setRemoveRatingsResponse(response: ApiResponse<TraktRemoveRatingsResponse>) {
        removeRatingsResponse = response
    }

    public fun setUserShowRatingsResponse(response: ApiResponse<List<TraktUserRatingItem>>) {
        userShowRatingsResponse = response
    }

    public fun setShowCommunityRatingResponse(response: ApiResponse<TraktRatingResponse>) {
        showCommunityRatingResponse = response
    }

    override suspend fun addRatings(request: TraktRatingsRequest): ApiResponse<TraktAddRatingsResponse> = addRatingsResponse

    override suspend fun removeRatings(request: TraktRemoveRatingsRequest): ApiResponse<TraktRemoveRatingsResponse> = removeRatingsResponse

    override suspend fun getUserShowRatings(): ApiResponse<List<TraktUserRatingItem>> = userShowRatingsResponse

    override suspend fun getShowCommunityRating(traktId: Long): ApiResponse<TraktRatingResponse> = showCommunityRatingResponse

    override suspend fun getSeasonCommunityRating(traktId: Long, seasonNumber: Int): ApiResponse<TraktRatingResponse> =
        seasonCommunityRatingResponse

    override suspend fun getEpisodeCommunityRating(
        traktId: Long,
        seasonNumber: Int,
        episodeNumber: Int,
    ): ApiResponse<TraktRatingResponse> = episodeCommunityRatingResponse
}
