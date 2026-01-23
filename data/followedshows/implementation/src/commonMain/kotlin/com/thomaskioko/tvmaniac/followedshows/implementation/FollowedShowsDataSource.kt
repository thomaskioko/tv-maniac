package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
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
    public suspend fun addShowsToWatchlistByTraktId(traktIds: List<Long>)
    public suspend fun removeShowsFromWatchlistByTraktId(traktIds: List<Long>)
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
                    val entry = FollowedShowEntry(
                        traktId = traktShow.show.ids.trakt,
                        tmdbId = traktShow.show.ids.tmdb,
                        followedAt = Instant.parse(traktShow.listedAt),
                        pendingAction = PendingAction.NOTHING,
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

    override suspend fun addShowsToWatchlistByTraktId(traktIds: List<Long>) {
        for (traktId in traktIds) {
            when (val response = traktListDataSource.addShowToWatchListByTraktId(traktId)) {
                is ApiResponse.Success -> continue
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP ${response.code}: ${response.errorMessage}"
                        is ApiResponse.Error.SerializationError -> "Serialization error: ${response.errorMessage}"
                        is ApiResponse.Error.GenericError -> response.errorMessage ?: "Unknown error"
                    }
                    throw Exception("Failed to add show $traktId to watchlist: $errorMessage")
                }
            }
        }
    }

    override suspend fun removeShowsFromWatchlistByTraktId(traktIds: List<Long>) {
        for (traktId in traktIds) {
            when (val response = traktListDataSource.removeShowFromWatchListByTraktId(traktId)) {
                is ApiResponse.Success -> continue
                is ApiResponse.Error -> {
                    val errorMessage = when (response) {
                        is ApiResponse.Error.HttpError -> "HTTP ${response.code}: ${response.errorMessage}"
                        is ApiResponse.Error.SerializationError -> "Serialization error: ${response.errorMessage}"
                        is ApiResponse.Error.GenericError -> response.errorMessage ?: "Unknown error"
                    }
                    throw Exception("Failed to remove show $traktId from watchlist: $errorMessage")
                }
            }
        }
    }
}
