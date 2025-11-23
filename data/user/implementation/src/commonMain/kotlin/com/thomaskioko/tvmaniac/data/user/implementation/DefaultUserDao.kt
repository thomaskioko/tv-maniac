package com.thomaskioko.tvmaniac.data.user.implementation

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserDao
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfileStats
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUserDao(
    private val database: TvManiacDatabase,
    private val userStatsDao: UserStatsDao,
    private val dispatchers: AppCoroutineDispatchers,
) : UserDao {

    override fun observeUserByKey(key: String): Flow<User?> =
        if (key == "me") {
            database.userQueries.observeCurrentUser()
                .asFlow()
                .mapToOneOrNull(dispatchers.databaseRead)
        } else {
            database.userQueries.userBySlug(key)
                .asFlow()
                .mapToOneOrNull(dispatchers.databaseRead)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUser(slug: String): Flow<UserProfile?> =
        database.userQueries.userBySlug(slug)
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .flatMapLatest { user ->
                user?.let {
                    userStatsDao.observeUserProfileStats(slug).map { stats ->
                        UserProfile(
                            slug = it.slug,
                            username = it.user_name,
                            fullName = it.full_name,
                            avatarUrl = it.profile_picture,
                            backgroundUrl = it.background_url ?: getRandomWatchlistBackdrop(),
                            stats = stats ?: UserProfileStats.Empty,
                        )
                    }
                } ?: flowOf(null)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<UserProfile?> =
        database.userQueries.observeCurrentUser()
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .flatMapLatest { user ->
                user?.let {
                    userStatsDao.observeUserProfileStats(it.slug).map { stats ->
                        UserProfile(
                            slug = it.slug,
                            username = it.user_name,
                            fullName = it.full_name,
                            avatarUrl = it.profile_picture,
                            backgroundUrl = it.background_url ?: getRandomWatchlistBackdrop(),
                            stats = stats ?: UserProfileStats.Empty,
                        )
                    }
                } ?: flowOf(null)
            }

    override fun getRandomWatchlistBackdrop(): String? =
        database.userQueries.getRandomWatchlistBackdrop().executeAsOneOrNull()

    override suspend fun upsertUser(
        slug: String,
        userName: String,
        fullName: String?,
        profilePicture: String?,
        backgroundUrl: String?,
        isMe: Boolean,
    ) {
        database.transaction {
            database.userQueries.insertOrReplace(
                slug = slug,
                user_name = userName,
                full_name = fullName,
                profile_picture = profilePicture,
                background_url = backgroundUrl,
                is_me = isMe,
            )
        }
    }

    override suspend fun deleteAll() {
        database.userQueries.deleteAll()
    }
}
