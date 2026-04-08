package com.thomaskioko.tvmaniac.traktlists.api

import kotlinx.coroutines.flow.Flow

public interface TraktListDao {

    public fun observeAll(): Flow<List<TraktListEntity>>

    public fun upsert(entity: TraktListEntity)

    public fun deleteAll()
}
