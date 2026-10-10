package com.thomaskioko.tvmaniac.search.testing

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.search.api.SearchRemoteDataSource
import com.thomaskioko.tvmaniac.search.api.model.RemoteSearchShow

public class FakeSearchRemoteDataSource(
    override val provider: SyncProviderSource,
) : SearchRemoteDataSource {

    private val results = mutableMapOf<String, List<RemoteSearchShow>>()
    private var error: ApiResponse.Error<List<RemoteSearchShow>>? = null

    private val _searchCalls = mutableListOf<Pair<String, Int>>()
    public val searchCalls: List<Pair<String, Int>> get() = _searchCalls

    public fun setSearchResult(query: String, result: List<RemoteSearchShow>) {
        results[query] = result
    }

    public fun setSearchError(error: ApiResponse.Error<List<RemoteSearchShow>>?) {
        this.error = error
    }

    override suspend fun searchShows(query: String, limit: Int): ApiResponse<List<RemoteSearchShow>> {
        _searchCalls += query to limit
        error?.let { return it }
        return ApiResponse.Success(results[query] ?: emptyList())
    }
}
