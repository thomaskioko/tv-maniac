package com.thomaskioko.tvmaniac.data.user.api

import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import kotlinx.coroutines.flow.Flow

public interface UserRepository {
    public fun observeUser(slug: String): Flow<UserProfile?>

    public fun observeCurrentUser(): Flow<UserProfile?>

    public suspend fun getCurrentUser(): UserProfile?

    public suspend fun fetchUserProfile(
        username: String = "me",
        forceRefresh: Boolean = false,
    )

    public suspend fun fetchUserStats(
        slug: String,
        forceRefresh: Boolean = false,
    )

    public suspend fun clearUserData()
}
