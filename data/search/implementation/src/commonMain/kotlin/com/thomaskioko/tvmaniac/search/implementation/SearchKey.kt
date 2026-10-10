package com.thomaskioko.tvmaniac.search.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource

public data class SearchKey(
    val provider: SyncProviderSource,
    val query: String,
) {
    val cacheKey: String = "${provider.name.lowercase()}:${query.trim().lowercase()}"
}
