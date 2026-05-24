package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
public class LogoutInteractor(
    private val traktAuthRepository: TraktAuthRepository,
    private val userRepository: UserRepository,
    private val datastoreRepository: DatastoreRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val syncRepository: ActivitySyncRepository,
    private val requestManagerRepository: RequestManagerRepository,
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        val currentUser = userRepository.observeCurrentUser().first()
        datastoreRepository.saveLastTraktUserId(currentUser?.slug)

        traktAuthRepository.logout()
        userRepository.clearUserData()
        traktActivityRepository.clearAllActivities()
        syncRepository.clearAll()
        // Drop every per-feature TTL so the next account's sync isn't suppressed
        // by stale request-manager entries from the previous account.
        requestManagerRepository.deleteAll()
    }
}
