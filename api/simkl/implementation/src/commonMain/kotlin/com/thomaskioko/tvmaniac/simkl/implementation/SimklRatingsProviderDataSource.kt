package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.SimklRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingIdItem
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingItem
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveRatingsRequest
import com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class SimklRatingsProviderDataSource(
    private val remoteDataSource: SimklRatingsRemoteDataSource,
) : RatingsRemoteDataSource {

    override val provider: AccountProvider = AccountProvider.SIMKL

    override suspend fun addShowRating(tmdbId: Long, rating: Int): ApiResponse<Unit> =
        remoteDataSource.addRatings(
            SimklRatingsRequest(
                shows = listOf(
                    SimklRatingItem(rating = rating, ids = SimklShowIds(tmdb = tmdbId.toString())),
                ),
            ),
        ).map { }

    override suspend fun removeShowRating(tmdbId: Long): ApiResponse<Unit> =
        remoteDataSource.removeRatings(
            SimklRemoveRatingsRequest(
                shows = listOf(
                    SimklRatingIdItem(ids = SimklShowIds(tmdb = tmdbId.toString())),
                ),
            ),
        ).map { }

    override suspend fun getShowCommunityRating(providerShowId: Long): ApiResponse<CommunityRating> =
        when (val response = remoteDataSource.getShowSummary(providerShowId)) {
            is ApiResponse.Success -> {
                val simklRating = response.body.ratings?.simkl
                val rating = simklRating?.rating
                if (rating != null) {
                    ApiResponse.Success(CommunityRating(rating = rating, votes = (simklRating.votes ?: 0).toLong()))
                } else {
                    ApiResponse.Error.HttpError(
                        code = 404,
                        errorBody = null,
                        errorMessage = "No Simkl community rating for show $providerShowId",
                    )
                }
            }
            is ApiResponse.Unauthenticated -> response
            is ApiResponse.Error.HttpError -> ApiResponse.Error.HttpError(
                code = response.code,
                errorBody = response.errorBody,
                errorMessage = response.errorMessage,
            )
            is ApiResponse.Error.SerializationError -> response
            is ApiResponse.Error.NetworkFailure -> response
            is ApiResponse.Error.OfflineError -> response
        }

    override suspend fun addSeasonRating(seasonTmdbId: Long, rating: Int): ApiResponse<Unit> = unsupportedRating()

    override suspend fun removeSeasonRating(seasonTmdbId: Long): ApiResponse<Unit> = unsupportedRating()

    override suspend fun addEpisodeRating(episodeTmdbId: Long, rating: Int): ApiResponse<Unit> = unsupportedRating()

    override suspend fun removeEpisodeRating(episodeTmdbId: Long): ApiResponse<Unit> = unsupportedRating()

    private fun unsupportedRating(): ApiResponse<Unit> = ApiResponse.Success(Unit)
}
