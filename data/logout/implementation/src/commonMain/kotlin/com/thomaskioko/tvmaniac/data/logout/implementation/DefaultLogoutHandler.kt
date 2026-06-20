package com.thomaskioko.tvmaniac.data.logout.implementation

import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
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
    private val userRepository: UserRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val syncRepository: ActivitySyncRepository,
    private val requestManagerRepository: RequestManagerRepository,
    private val database: TvManiacDatabase,
    private val transactionRunner: DatabaseTransactionRunner,
) : LogoutHandler {

    override suspend fun clear() {
        userRepository.clearUserData()
        traktActivityRepository.clearAllActivities()
        syncRepository.clearAll()
        requestManagerRepository.deleteAll()

        transactionRunner {
            database.watchedEpisodesQueries.deleteAll()
            database.followedShowsQueries.deleteAll()
            database.continueWatchingQueries.deleteAll()
            database.favoritesQueries.deleteAll()
            database.traktListShowsQueries.deleteAll()
            database.traktListsQueries.deleteAll()
            database.showWatchStatusQueries.deleteAll()
            database.calendarQueries.deleteAll()
        }
    }
}
