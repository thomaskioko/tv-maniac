package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.map
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.TraktRatingsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeRatingItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktRemoveRatingsRequest
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSeasonRatingItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowRatingIdItem
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowRatingItem
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesIntoSet(AppScope::class)
public class TraktRatingsSyncProviderDataSource(
    private val remoteDataSource: TraktRatingsRemoteDataSource,
) : RatingsRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun addShowRating(tmdbId: Long, rating: Int): ApiResponse<Unit> =
        remoteDataSource.addRatings(
            TraktRatingsRequest(
                shows = listOf(
                    TraktShowRatingItem(rating = rating, ids = TraktShowIds(tmdbId = tmdbId)),
                ),
            ),
        ).map { }

    override suspend fun removeShowRating(tmdbId: Long): ApiResponse<Unit> =
        remoteDataSource.removeRatings(
            TraktRemoveRatingsRequest(
                shows = listOf(
                    TraktShowRatingIdItem(ids = TraktShowIds(tmdbId = tmdbId)),
                ),
            ),
        ).map { }

    override suspend fun getShowCommunityRating(providerShowId: Long): ApiResponse<CommunityRating> =
        remoteDataSource.getShowCommunityRating(providerShowId).map { CommunityRating(rating = it.rating, votes = it.votes) }

    override suspend fun getShowUserRating(providerShowId: Long): ApiResponse<Int?> =
        remoteDataSource.getUserShowRatings().map { items ->
            items.firstOrNull { it.show.ids.traktId == providerShowId }?.rating
        }

    override suspend fun addSeasonRating(seasonTmdbId: Long, rating: Int): ApiResponse<Unit> =
        remoteDataSource.addRatings(
            TraktRatingsRequest(
                seasons = listOf(
                    TraktSeasonRatingItem(rating = rating, ids = TraktSeasonIds(tmdbId = seasonTmdbId)),
                ),
            ),
        ).map { }

    override suspend fun removeSeasonRating(seasonTmdbId: Long): ApiResponse<Unit> =
        remoteDataSource.removeRatings(
            TraktRemoveRatingsRequest(
                seasons = listOf(
                    TraktSeasonRatingIdItem(ids = TraktSeasonIds(tmdbId = seasonTmdbId)),
                ),
            ),
        ).map { }

    override suspend fun addEpisodeRating(episodeTmdbId: Long, rating: Int): ApiResponse<Unit> =
        remoteDataSource.addRatings(
            TraktRatingsRequest(
                episodes = listOf(
                    TraktEpisodeRatingItem(rating = rating, ids = TraktEpisodeIds(tmdbId = episodeTmdbId)),
                ),
            ),
        ).map { }

    override suspend fun removeEpisodeRating(episodeTmdbId: Long): ApiResponse<Unit> =
        remoteDataSource.removeRatings(
            TraktRemoveRatingsRequest(
                episodes = listOf(
                    TraktEpisodeRatingIdItem(ids = TraktEpisodeIds(tmdbId = episodeTmdbId)),
                ),
            ),
        ).map { }
}
