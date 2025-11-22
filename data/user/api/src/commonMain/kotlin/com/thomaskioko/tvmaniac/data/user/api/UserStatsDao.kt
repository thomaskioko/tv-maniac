package com.thomaskioko.tvmaniac.data.user.api

import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.db.Stats
import kotlinx.coroutines.flow.Flow

public interface UserStatsDao {
    public fun observeUserStats(slug: String): Flow<Stats?>

    public fun observeUserProfileStats(slug: String): Flow<UserProfileStats?>

    public fun getUserStats(slug: String): Stats?

    public suspend fun upsertStats(
        slug: String,
        showsWatched: Long,
        episodesWatched: Long,
        minutesWatched: Long,
    )

    public suspend fun deleteAll()
}
