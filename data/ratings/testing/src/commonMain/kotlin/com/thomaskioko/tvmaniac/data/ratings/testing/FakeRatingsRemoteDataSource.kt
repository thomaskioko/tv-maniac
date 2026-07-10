package com.thomaskioko.tvmaniac.data.ratings.testing

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource

public class FakeRatingsRemoteDataSource : RatingsRemoteDataSource {

    override var provider: SyncProviderSource = SyncProviderSource.TRAKT

    private var addShowRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    private var removeShowRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    public var lastAddShowRatingTmdbId: Long? = null
        private set
    public var lastRemoveShowRatingTmdbId: Long? = null
        private set
    private var communityRatingResponse: ApiResponse<CommunityRating> = ApiResponse.Success(CommunityRating(rating = 0.0, votes = 0))
    private var userRatingResponse: ApiResponse<Int?> = ApiResponse.Success(null)
    private var addSeasonRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    private var removeSeasonRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    private var addEpisodeRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)
    private var removeEpisodeRatingResponse: ApiResponse<Unit> = ApiResponse.Success(Unit)

    public fun setAddShowRatingResponse(response: ApiResponse<Unit>) {
        addShowRatingResponse = response
    }

    public fun setRemoveShowRatingResponse(response: ApiResponse<Unit>) {
        removeShowRatingResponse = response
    }

    public fun setCommunityRatingResponse(response: ApiResponse<CommunityRating>) {
        communityRatingResponse = response
    }

    public fun setUserRatingResponse(response: ApiResponse<Int?>) {
        userRatingResponse = response
    }

    public fun setAddSeasonRatingResponse(response: ApiResponse<Unit>) {
        addSeasonRatingResponse = response
    }

    public fun setRemoveSeasonRatingResponse(response: ApiResponse<Unit>) {
        removeSeasonRatingResponse = response
    }

    public fun setAddEpisodeRatingResponse(response: ApiResponse<Unit>) {
        addEpisodeRatingResponse = response
    }

    public fun setRemoveEpisodeRatingResponse(response: ApiResponse<Unit>) {
        removeEpisodeRatingResponse = response
    }

    override suspend fun addShowRating(tmdbId: Long, rating: Int): ApiResponse<Unit> {
        lastAddShowRatingTmdbId = tmdbId
        return addShowRatingResponse
    }

    override suspend fun removeShowRating(tmdbId: Long): ApiResponse<Unit> {
        lastRemoveShowRatingTmdbId = tmdbId
        return removeShowRatingResponse
    }

    override suspend fun getShowCommunityRating(providerShowId: Long): ApiResponse<CommunityRating> = communityRatingResponse

    override suspend fun getShowUserRating(providerShowId: Long): ApiResponse<Int?> = userRatingResponse

    override suspend fun addSeasonRating(seasonTmdbId: Long, rating: Int): ApiResponse<Unit> = addSeasonRatingResponse

    override suspend fun removeSeasonRating(seasonTmdbId: Long): ApiResponse<Unit> = removeSeasonRatingResponse

    override suspend fun addEpisodeRating(episodeTmdbId: Long, rating: Int): ApiResponse<Unit> = addEpisodeRatingResponse

    override suspend fun removeEpisodeRating(episodeTmdbId: Long): ApiResponse<Unit> = removeEpisodeRatingResponse
}
