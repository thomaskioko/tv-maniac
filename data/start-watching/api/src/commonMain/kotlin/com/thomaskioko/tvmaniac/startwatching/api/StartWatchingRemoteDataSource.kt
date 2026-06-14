package com.thomaskioko.tvmaniac.startwatching.api

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderScoped
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse

public interface StartWatchingRemoteDataSource : ProviderScoped {

    public suspend fun getPlanToWatch(): ApiResponse<List<RemotePlanToWatchShow>>
}
