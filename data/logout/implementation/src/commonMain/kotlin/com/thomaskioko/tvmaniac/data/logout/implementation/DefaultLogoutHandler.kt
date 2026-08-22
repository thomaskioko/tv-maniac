package com.thomaskioko.tvmaniac.data.logout.implementation

import com.thomaskioko.tvmaniac.core.base.coroutines.SyncCoroutineScope
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import com.thomaskioko.tvmaniac.data.ratings.api.ProviderMetaDao
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsDao
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSessionDao
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLogoutHandler(
    private val syncCoroutineScope: SyncCoroutineScope,
    private val userRepository: UserRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val syncRepository: ActivitySyncRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val ratingsDao: RatingsDao,
    private val providerMetaDao: ProviderMetaDao,
    private val rewatchSessionDao: RewatchSessionDao,
    private val database: TvManiacDatabase,
    private val transactionRunner: DatabaseTransactionRunner,
) : LogoutHandler {

    override suspend fun clearAccountData() {
        clearAccountState()

        transactionRunner {
            deleteAccountTables()
        }
    }

    override suspend fun clearAccountAndTrackingData() {
        clearAccountState()

        transactionRunner {
            deleteAccountTables()
            deleteTrackingTables()
        }
    }

    private suspend fun clearAccountState() {
        syncCoroutineScope.cancelActiveWork()

        userRepository.clearUserData()
        traktActivityRepository.clearAllActivities()
        syncRepository.clearAll()
        requestManagerRepository.deleteAll()
    }

    private fun deleteAccountTables() {
        database.watchedShowSyncLogQueries.deleteAll()
        database.favoritesQueries.deleteAll()
        database.traktListShowsQueries.deleteAll()
        database.traktListsQueries.deleteAll()
        database.calendarQueries.deleteAll()
        providerMetaDao.clearAll()
    }

    private fun deleteTrackingTables() {
        database.watchedEpisodesQueries.deleteAll()
        database.followedShowsQueries.deleteAll()
        database.continueWatchingQueries.deleteAll()
        database.showWatchStatusQueries.deleteAll()
        ratingsDao.clearAll()
        rewatchSessionDao.clearAll()
    }
}
