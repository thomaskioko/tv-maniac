package com.thomaskioko.tvmaniac.search.api

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow

public interface SearchRemoteDataSource : SyncProvider {

    public suspend fun searchShows(query: String, limit: Int): ApiResponse<List<RemoteSearchShow>>
}
