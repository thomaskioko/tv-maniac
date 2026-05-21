package com.thomaskioko.tvmaniac.trakt.testing

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPersonalListsResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse

public class FakeTraktUserRemoteDataSource : TraktUserRemoteDataSource {

    private var userResponse: ApiResponse<TraktUserResponse>? = null
    private var userStatsResponse: ApiResponse<TraktUserStatsResponse>? = null
    private var userListResponse: List<TraktPersonalListsResponse> = emptyList()
    private var hiddenProgressWatchedResponse: ApiResponse<List<TraktHiddenItemResponse>> =
        ApiResponse.Success(emptyList())

    private var hiddenProgressWatchedInvocations: Int = 0

    public fun setUser(response: ApiResponse<TraktUserResponse>) {
        userResponse = response
    }

    public fun setUserStats(response: ApiResponse<TraktUserStatsResponse>) {
        userStatsResponse = response
    }

    public fun setUserList(response: List<TraktPersonalListsResponse>) {
        userListResponse = response
    }

    public fun setHiddenProgressWatched(response: ApiResponse<List<TraktHiddenItemResponse>>) {
        hiddenProgressWatchedResponse = response
    }

    public fun hiddenProgressWatchedInvocations(): Int = hiddenProgressWatchedInvocations

    public fun clearInvocations() {
        hiddenProgressWatchedInvocations = 0
    }

    override suspend fun getUser(userId: String): ApiResponse<TraktUserResponse> =
        userResponse ?: error("FakeTraktUserRemoteDataSource.userResponse not configured")

    override suspend fun getUserStats(userId: String): ApiResponse<TraktUserStatsResponse> =
        userStatsResponse ?: error("FakeTraktUserRemoteDataSource.userStatsResponse not configured")

    override suspend fun getUserList(userId: String): List<TraktPersonalListsResponse> =
        userListResponse

    override suspend fun getHiddenProgressWatched(type: String): ApiResponse<List<TraktHiddenItemResponse>> {
        hiddenProgressWatchedInvocations++
        return hiddenProgressWatchedResponse
    }
}
