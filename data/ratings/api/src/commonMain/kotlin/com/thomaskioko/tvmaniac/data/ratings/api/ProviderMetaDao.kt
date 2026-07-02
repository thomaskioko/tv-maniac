package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.db.Provider
import kotlinx.coroutines.flow.Flow

public interface ProviderMetaDao {
    public fun upsertProviderRating(showId: Long, provider: Provider, rating: Double, voteCount: Long, lastSyncedAt: Long)
    public fun observeProviderRating(showId: Long, provider: Provider): Flow<ProviderRating?>
    public fun clearAll()
}
