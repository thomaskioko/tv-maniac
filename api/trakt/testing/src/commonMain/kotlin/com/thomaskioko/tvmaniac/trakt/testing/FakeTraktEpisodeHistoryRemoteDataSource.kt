package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryEntry
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncResponse

public class FakeTraktEpisodeHistoryRemoteDataSource : TraktEpisodeHistoryRemoteDataSource {

    private var showEpisodeWatchesResponse: ApiResponse<List<TraktHistoryEntry>> =
        ApiResponse.Success(emptyList())
    private var addResponse: ApiResponse<TraktSyncResponse> = ApiResponse.Success(TraktSyncResponse())
    private var removeResponse: ApiResponse<TraktSyncResponse> = ApiResponse.Success(TraktSyncResponse())

    public fun setShowEpisodeWatches(response: ApiResponse<List<TraktHistoryEntry>>) {
        showEpisodeWatchesResponse = response
    }

    public fun setAddResponse(response: ApiResponse<TraktSyncResponse>) {
        addResponse = response
    }

    public fun setRemoveResponse(response: ApiResponse<TraktSyncResponse>) {
        removeResponse = response
    }

    override suspend fun getShowEpisodeWatches(showId: Long): ApiResponse<List<TraktHistoryEntry>> =
        showEpisodeWatchesResponse

    override suspend fun addEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        addResponse

    override suspend fun removeEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse> =
        removeResponse
}
