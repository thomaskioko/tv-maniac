package com.thomaskioko.tvmaniac.data.user.testing

import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

public class FakeUserRepository(
    userProfile: UserProfile? = createTestProfile(),
) : UserRepository {

    private val _userProfile = MutableStateFlow(userProfile)

    public fun setUserProfile(profile: UserProfile?) {
        _userProfile.value = profile
    }

    override fun observeUser(slug: String): Flow<UserProfile?> = _userProfile

    override fun observeCurrentUser(): Flow<UserProfile?> = _userProfile

    override suspend fun fetchUserProfile(username: String, forceRefresh: Boolean) {
    }

    override suspend fun fetchUserStats(slug: String, forceRefresh: Boolean) {
    }

    override suspend fun clearUserData() {
        _userProfile.value = null
    }
}

public fun createTestProfile(
    slug: String = "test-user",
    username: String = "testuser",
    fullName: String? = "Test User",
    avatarUrl: String? = "https://example.com/avatar.jpg",
    backgroundUrl: String? = "https://example.com/background.jpg",
    stats: UserProfileStats = UserProfileStats.Empty,
): UserProfile {
    return UserProfile(
        slug = slug,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        stats = stats,
    )
}
