package com.thomaskioko.tvmaniac.lists.api

import kotlinx.coroutines.flow.Flow

public interface ListDao {

    public fun observeAll(): Flow<List<UserListEntity>>

    public fun observeListsWithPosters(): Flow<List<UserListEntity>>

    public fun upsertByTraktId(entity: UserListEntity)

    public fun insertLocal(name: String, createdAt: String): Long

    public fun markSynced(id: Long, traktId: Long, slug: String?)

    public fun getTraktId(id: Long): Long?

    public fun selectIdsByTraktId(): Map<Long, Long>

    public fun deleteById(id: Long)

    public fun deleteAll()
}
