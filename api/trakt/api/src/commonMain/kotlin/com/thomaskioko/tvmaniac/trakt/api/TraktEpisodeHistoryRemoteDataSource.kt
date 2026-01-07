package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.networkutil.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHistoryEntry
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncItems
import com.thomaskioko.tvmaniac.trakt.api.model.TraktSyncResponse

public interface TraktEpisodeHistoryRemoteDataSource {
    public suspend fun getShowEpisodeWatches(showTraktId: Long): ApiResponse<List<TraktHistoryEntry>>
    public suspend fun addEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse>
    public suspend fun removeEpisodeWatches(items: TraktSyncItems): ApiResponse<TraktSyncResponse>
}
