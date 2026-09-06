package com.thomaskioko.tvmaniac.lists.api

import kotlinx.coroutines.flow.Flow

public interface ListDao {

    public fun observeAll(): Flow<List<UserListEntity>>

    public fun observeListsWithPosters(): Flow<List<UserListEntity>>

    public fun upsert(entity: UserListEntity)

    public fun deleteAll()
}
