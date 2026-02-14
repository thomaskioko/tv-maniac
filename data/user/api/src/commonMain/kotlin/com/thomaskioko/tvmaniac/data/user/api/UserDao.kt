package com.thomaskioko.tvmaniac.data.user.api

import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.db.User
import kotlinx.coroutines.flow.Flow

public interface UserDao {
    public fun observeUserByKey(key: String): Flow<User?>

    public fun observeUser(slug: String): Flow<UserProfile?>

    public fun observeCurrentUser(): Flow<UserProfile?>

    public suspend fun getCurrentUser(): UserProfile?

    public fun getRandomWatchlistBackdrop(): String?

    public suspend fun upsertUser(
        slug: String,
        userName: String,
        fullName: String?,
        profilePicture: String?,
        backgroundUrl: String? = null,
        isMe: Boolean = true,
    )

    public suspend fun deleteAll()
}
