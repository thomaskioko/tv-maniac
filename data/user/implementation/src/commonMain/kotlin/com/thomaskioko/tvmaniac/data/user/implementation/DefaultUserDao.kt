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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUserDao(
    private val database: TvManiacDatabase,
    private val userStatsDao: UserStatsDao,
    private val dispatchers: AppCoroutineDispatchers,
) : UserDao {

    override fun observeUserByKey(key: String): Flow<User?> =
        if (key == "me") {
            observeCurrentUserQuery()
        } else {
            observeUserBySlugQuery(key)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeUser(slug: String): Flow<UserProfile?> =
        combine(
            observeUserBySlugQuery(slug),
            observeLibraryBackdrop(),
        ) { user, libraryBackdrop ->
            user to libraryBackdrop
        }.flatMapLatest { (user, libraryBackdrop) ->
            user?.let {
                userStatsDao.observeUserProfileStats(slug).map { stats ->
                    UserProfile(
                        slug = it.slug,
                        username = it.user_name,
                        fullName = it.full_name,
                        avatarUrl = it.profile_picture,
                        backgroundUrl = it.background_url ?: libraryBackdrop,
                        stats = stats ?: UserProfileStats.Empty,
                        statsLoaded = stats != null,
                    )
                }
            } ?: flowOf(null)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCurrentUser(): Flow<UserProfile?> =
        combine(
            observeCurrentUserQuery(),
            observeLibraryBackdrop(),
        ) { user, libraryBackdrop ->
            user to libraryBackdrop
        }.flatMapLatest { (user, libraryBackdrop) ->
            user?.let {
                userStatsDao.observeUserProfileStats(it.slug).map { stats ->
                    UserProfile(
                        slug = it.slug,
                        username = it.user_name,
                        fullName = it.full_name,
                        avatarUrl = it.profile_picture,
                        backgroundUrl = it.background_url ?: libraryBackdrop,
                        stats = stats ?: UserProfileStats.Empty,
                        statsLoaded = stats != null,
                    )
                }
            } ?: flowOf(null)
        }

    override suspend fun getCurrentUser(): UserProfile? =
        withContext(dispatchers.databaseRead) {
            val user = database.userQueries.observeCurrentUser().executeAsOneOrNull()
            user?.let {
                val stats = userStatsDao.observeUserProfileStats(it.slug).first()
                UserProfile(
                    slug = it.slug,
                    username = it.user_name,
                    fullName = it.full_name,
                    avatarUrl = it.profile_picture,
                    backgroundUrl = it.background_url ?: getRandomLibraryBackdrop(),
                    stats = stats ?: UserProfileStats.Empty,
                )
            }
        }

    private fun observeCurrentUserQuery(): Flow<User?> =
        database.userQueries.observeCurrentUser()
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)

    private fun observeUserBySlugQuery(slug: String): Flow<User?> =
        database.userQueries.userBySlug(slug)
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)

    override fun observeLibraryBackdrop(): Flow<String?> =
        database.userQueries.observeLibraryBackdrop()
            .asFlow()
            .mapToOneOrNull(dispatchers.databaseRead)
            .map { it?.image_url }

    override fun getRandomLibraryBackdrop(): String? =
        database.userQueries.getRandomLibraryBackdrop().executeAsOneOrNull()?.image_url

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
