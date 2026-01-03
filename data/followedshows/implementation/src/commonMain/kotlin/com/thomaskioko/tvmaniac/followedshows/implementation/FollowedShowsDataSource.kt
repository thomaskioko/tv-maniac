package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

public interface FollowedShowsDataSource {
    public suspend fun getFollowedShows(): List<Pair<FollowedShowEntry, TraktFollowedShowResponse>>
    public suspend fun addShowsToWatchlist(tmdbIds: List<Long>)
    public suspend fun removeShowsFromWatchlist(tmdbIds: List<Long>)
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, FollowedShowsDataSource::class)
public class TraktFollowedShowsDataSource(
    private val traktListDataSource: TraktListRemoteDataSource,
) : FollowedShowsDataSource {

    override suspend fun getFollowedShows(): List<Pair<FollowedShowEntry, TraktFollowedShowResponse>> {
        return when (val response = traktListDataSource.getWatchList()) {
            is ApiResponse.Success -> {
                response.body.map { traktShow ->
                    val tmdbId = traktShow.show.ids.tmdb.toLong()
                    val entry = FollowedShowEntry(
                        tmdbId = tmdbId,
                        followedAt = Instant.parse(traktShow.listedAt),
                        pendingAction = PendingAction.NOTHING,
                        traktId = traktShow.show.ids.trakt.toLong(),
                    )
                    entry to traktShow
                }
            }
            is ApiResponse.Error -> {
                val errorMessage = when (response) {
                    is ApiResponse.Error.HttpError -> "HTTP ${response.code}: ${response.errorMessage}"
                    is ApiResponse.Error.SerializationError -> "Serialization error: ${response.errorMessage}"
                    is ApiResponse.Error.GenericError -> response.errorMessage ?: "Unknown error"
                }
                throw Exception("Failed to fetch followed shows: $errorMessage")
            }
        }
    }

    override suspend fun addShowsToWatchlist(tmdbIds: List<Long>) {
        for (showId in tmdbIds) {
            when (val response = traktListDataSource.addShowToWatchListByTmdbId(showId)) {
                is ApiResponse.Success -> continue
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP ${response.code}: ${response.errorMessage}"
                        is ApiResponse.Error.SerializationError -> "Serialization error: ${response.errorMessage}"
                        is ApiResponse.Error.GenericError -> response.errorMessage ?: "Unknown error"
                    }
                    throw Exception("Failed to add show $showId to watchlist: $errorMessage")
                }
            }
        }
    }

    override suspend fun removeShowsFromWatchlist(tmdbIds: List<Long>) {
        for (showId in tmdbIds) {
            when (val response = traktListDataSource.removeShowFromWatchListByTmdbId(showId)) {
                is ApiResponse.Success -> continue
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP ${response.code}: ${response.errorMessage}"
                        is ApiResponse.Error.SerializationError -> "Serialization error: ${response.errorMessage}"
                        is ApiResponse.Error.GenericError -> response.errorMessage ?: "Unknown error"
                    }
                    throw Exception("Failed to remove show $showId from watchlist: $errorMessage")
                }
            }
        }
    }
}
