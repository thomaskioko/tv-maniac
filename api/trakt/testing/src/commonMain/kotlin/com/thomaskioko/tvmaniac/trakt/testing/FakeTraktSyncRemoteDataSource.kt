package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeActivities
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowActivities
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse

public class FakeTraktSyncRemoteDataSource : TraktSyncRemoteDataSource {

    private var lastActivitiesResponse: ApiResponse<TraktLastActivitiesResponse> =
        ApiResponse.Success(
            TraktLastActivitiesResponse(
                all = "",
                shows = TraktShowActivities(),
                episodes = TraktEpisodeActivities(),
            ),
        )

    private var watchedShowsResponse: ApiResponse<List<TraktWatchedShowResponse>> =
        ApiResponse.Success(emptyList())

    private var lastActivitiesInvocations: Int = 0
    private var watchedShowsInvocations: Int = 0

    public fun setLastActivities(response: ApiResponse<TraktLastActivitiesResponse>) {
        lastActivitiesResponse = response
    }

    public fun setWatchedShows(response: ApiResponse<List<TraktWatchedShowResponse>>) {
        watchedShowsResponse = response
    }

    public fun lastActivitiesInvocations(): Int = lastActivitiesInvocations

    public fun watchedShowsInvocations(): Int = watchedShowsInvocations

    public fun clearInvocations() {
        lastActivitiesInvocations = 0
        watchedShowsInvocations = 0
    }

    override suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse> {
        lastActivitiesInvocations++
        return lastActivitiesResponse
    }

    override suspend fun getWatchedShows(limit: String): ApiResponse<List<TraktWatchedShowResponse>> {
        watchedShowsInvocations++
        return watchedShowsResponse
    }
}
