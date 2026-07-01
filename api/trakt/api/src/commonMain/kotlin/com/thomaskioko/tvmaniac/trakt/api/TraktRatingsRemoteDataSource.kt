package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktAddRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserRatingItem

public interface TraktRatingsRemoteDataSource {

    public suspend fun addRatings(request: TraktRatingsRequest): ApiResponse<TraktAddRatingsResponse>

    public suspend fun removeRatings(request: TraktRemoveRatingsRequest): ApiResponse<TraktRemoveRatingsResponse>

    public suspend fun getUserShowRatings(): ApiResponse<List<TraktUserRatingItem>>

    public suspend fun getShowCommunityRating(traktId: Long): ApiResponse<TraktRatingResponse>

    public suspend fun getSeasonCommunityRating(traktId: Long, seasonNumber: Int): ApiResponse<TraktRatingResponse>

    public suspend fun getEpisodeCommunityRating(
        traktId: Long,
        seasonNumber: Int,
        episodeNumber: Int,
    ): ApiResponse<TraktRatingResponse>
}
