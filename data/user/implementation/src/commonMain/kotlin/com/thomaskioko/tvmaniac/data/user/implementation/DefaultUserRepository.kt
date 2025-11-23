package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserDao
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.data.user.api.UserStatsDao
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultUserRepository(
    private val userDao: UserDao,
    private val userStatsDao: UserStatsDao,
    private val userStore: UserStore,
    private val statsStore: UserStatsStore,
    private val traktAuthRepository: TraktAuthRepository,
    private val appCoroutineDispatchers: AppCoroutineDispatchers,
) : UserRepository {

    override fun observeUser(slug: String): Flow<UserProfile?> =
        userDao.observeUser(slug)
            .flowOn(appCoroutineDispatchers.databaseRead)

    override fun observeCurrentUser(): Flow<UserProfile?> =
        userDao.observeCurrentUser()
            .flowOn(appCoroutineDispatchers.databaseRead)

    override suspend fun fetchUserProfile(username: String, forceRefresh: Boolean) {
        if (!traktAuthRepository.isLoggedIn()) return

        when (forceRefresh) {
            true -> userStore.fresh(username)
            false -> userStore.get(username)
        }
    }

    override suspend fun fetchUserStats(slug: String, forceRefresh: Boolean) {
        if (!traktAuthRepository.isLoggedIn()) return

        when (forceRefresh) {
            true -> statsStore.fresh(slug)
            false -> statsStore.get(slug)
        }
    }

    override suspend fun clearUserData() {
        userStore.clear()
        statsStore.clear()
        userDao.deleteAll()
        userStatsDao.deleteAll()
    }

    private suspend fun TraktAuthRepository.isLoggedIn(): Boolean {
        return state.first() == TraktAuthState.LOGGED_IN
    }
}
