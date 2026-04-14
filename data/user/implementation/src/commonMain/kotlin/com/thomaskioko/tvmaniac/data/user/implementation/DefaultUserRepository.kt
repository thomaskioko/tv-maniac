package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.data.user.api.UserDao
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUserRepository(
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val userStore: UserStore,
    private val statsStore: UserStatsStore,
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : UserRepository {

    override fun observeUser(slug: String): Flow<UserProfile?> =
        userDao.observeUser(slug)
            .flowOn(appCoroutineDispatchers.databaseRead)

    override fun observeCurrentUser(): Flow<UserProfile?> =
        userDao.observeCurrentUser()
            .flowOn(appCoroutineDispatchers.databaseRead)

    override suspend fun getCurrentUser(): UserProfile? = userDao.getCurrentUser()

    override suspend fun fetchUserProfile(username: String, forceRefresh: Boolean) {
        when (forceRefresh) {
            true -> userStore.fresh(username)
            false -> userStore.get(username)
        }
    }

    override suspend fun fetchUserStats(slug: String, forceRefresh: Boolean) {
        when (forceRefresh) {
            true -> statsStore.fresh(slug)
            false -> statsStore.get(slug)
        }
    }

    @OptIn(ExperimentalStoreApi::class)
    override suspend fun clearUserData() {
        userStore.clear()
        statsStore.clear()
        userDao.deleteAll()
        userStatsDao.deleteAll()
    }
}
