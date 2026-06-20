package com.thomaskioko.tvmaniac.startwatching.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.startwatching.api.RemotePlanToWatchShow
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingRemoteDataSource

public class FakeStartWatchingRemoteDataSource(
    override val provider: AccountProvider = AccountProvider.TRAKT,
    private val planToWatchResponse: ApiResponse<List<RemotePlanToWatchShow>> = ApiResponse.Success(emptyList()),
) : StartWatchingRemoteDataSource {

    override suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>> = planToWatchResponse
}
