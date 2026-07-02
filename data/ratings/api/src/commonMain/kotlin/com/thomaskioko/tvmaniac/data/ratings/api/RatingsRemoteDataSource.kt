package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface RatingsRemoteDataSource : ProviderScoped {
    public suspend fun addShowRating(tmdbId: Long, rating: Int): ApiResponse<Unit>
    public suspend fun removeShowRating(tmdbId: Long): ApiResponse<Unit>
    public suspend fun getShowCommunityRating(providerShowId: Long): ApiResponse<CommunityRating>
    public suspend fun addSeasonRating(seasonTmdbId: Long, rating: Int): ApiResponse<Unit>
    public suspend fun removeSeasonRating(seasonTmdbId: Long): ApiResponse<Unit>
    public suspend fun addEpisodeRating(episodeTmdbId: Long, rating: Int): ApiResponse<Unit>
    public suspend fun removeEpisodeRating(episodeTmdbId: Long): ApiResponse<Unit>
}
