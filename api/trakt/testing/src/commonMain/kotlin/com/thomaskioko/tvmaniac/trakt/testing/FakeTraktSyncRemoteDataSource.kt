package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktEpisodeActivities
import com.thomaskioko.tvmaniac.trakt.api.model.TraktLastActivitiesResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowActivities
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
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

    private var playbackEpisodesResponse: ApiResponse<List<TraktPlaybackEpisodeResponse>> =
        ApiResponse.Success(emptyList())

    private val showWatchedProgressResponses =
        mutableMapOf<Long, ApiResponse<TraktWatchedProgressResponse>>()

    private var upNextNitroResponse: ApiResponse<List<TraktUpNextNitroResponse>> =
        ApiResponse.Success(emptyList())

    private val watchedShowsResponsesByPage =
        mutableMapOf<Int, ApiResponse<List<TraktWatchedShowResponse>>>()
    private var defaultWatchedShowsResponse: ApiResponse<List<TraktWatchedShowResponse>> =
        ApiResponse.Success(emptyList())

    private var lastActivitiesInvocations: Int = 0
    private var playbackEpisodesInvocations: Int = 0
    private val showWatchedProgressInvocations = mutableMapOf<Long, Int>()
    private val showWatchedProgressLastActivityArgs = mutableMapOf<Long, String?>()
    private var upNextNitroInvocations: Int = 0
    private val watchedShowsInvocationsByPage = mutableMapOf<Int, Int>()
    private val watchedShowsExtendedByPage = mutableMapOf<Int, String>()

    public fun setLastActivities(response: ApiResponse<TraktLastActivitiesResponse>) {
        lastActivitiesResponse = response
    }

    public fun setPlaybackEpisodes(response: ApiResponse<List<TraktPlaybackEpisodeResponse>>) {
        playbackEpisodesResponse = response
    }

    public fun setShowWatchedProgress(
        showId: Long,
        response: ApiResponse<TraktWatchedProgressResponse>,
    ) {
        showWatchedProgressResponses[showId] = response
    }

    public fun setUpNextNitro(response: ApiResponse<List<TraktUpNextNitroResponse>>) {
        upNextNitroResponse = response
    }

    public fun setWatchedShows(response: ApiResponse<List<TraktWatchedShowResponse>>) {
        defaultWatchedShowsResponse = response
        watchedShowsResponsesByPage.clear()
    }

    public fun setWatchedShowsPage(
        page: Int,
        response: ApiResponse<List<TraktWatchedShowResponse>>,
    ) {
        watchedShowsResponsesByPage[page] = response
    }

    public fun lastActivitiesInvocations(): Int = lastActivitiesInvocations

    public fun playbackEpisodesInvocations(): Int = playbackEpisodesInvocations

    public fun showWatchedProgressInvocations(showId: Long): Int =
        showWatchedProgressInvocations[showId] ?: 0

    public fun showWatchedProgressLastActivity(showId: Long): String? =
        showWatchedProgressLastActivityArgs[showId]

    public fun upNextNitroInvocations(): Int = upNextNitroInvocations

    public fun watchedShowsInvocations(page: Int): Int =
        watchedShowsInvocationsByPage[page] ?: 0

    public fun watchedShowsExtended(page: Int): String? =
        watchedShowsExtendedByPage[page]

    public fun clearInvocations() {
        lastActivitiesInvocations = 0
        playbackEpisodesInvocations = 0
        showWatchedProgressInvocations.clear()
        showWatchedProgressLastActivityArgs.clear()
        upNextNitroInvocations = 0
        watchedShowsInvocationsByPage.clear()
        watchedShowsExtendedByPage.clear()
    }

    override suspend fun getLastActivities(): ApiResponse<TraktLastActivitiesResponse> {
        lastActivitiesInvocations++
        return lastActivitiesResponse
    }

    override suspend fun getPlaybackEpisodes(
        limit: Int,
    ): ApiResponse<List<TraktPlaybackEpisodeResponse>> {
        playbackEpisodesInvocations++
        return playbackEpisodesResponse
    }

    override suspend fun getShowWatchedProgress(
        showId: Long,
        lastActivity: String?,
        hidden: Boolean,
        specials: Boolean,
    ): ApiResponse<TraktWatchedProgressResponse> {
        showWatchedProgressInvocations[showId] =
            (showWatchedProgressInvocations[showId] ?: 0) + 1
        showWatchedProgressLastActivityArgs[showId] = lastActivity
        return showWatchedProgressResponses[showId]
            ?: error("FakeTraktSyncRemoteDataSource: no showWatchedProgress response configured for showId=$showId")
    }

    override suspend fun getUpNextNitro(
        intent: String,
        limit: Int,
        page: Int,
    ): ApiResponse<List<TraktUpNextNitroResponse>> {
        upNextNitroInvocations++
        return upNextNitroResponse
    }

    override suspend fun getWatchedShows(
        page: Int,
        limit: Int,
        extended: String,
    ): ApiResponse<List<TraktWatchedShowResponse>> {
        watchedShowsInvocationsByPage[page] = (watchedShowsInvocationsByPage[page] ?: 0) + 1
        watchedShowsExtendedByPage[page] = extended
        return watchedShowsResponsesByPage[page] ?: defaultWatchedShowsResponse
    }
}
