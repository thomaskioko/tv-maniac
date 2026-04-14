package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
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
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        val currentUser = userRepository.observeCurrentUser().first()
        datastoreRepository.saveLastTraktUserId(currentUser?.slug)

        traktAuthRepository.logout()
        userRepository.clearUserData()
        traktActivityRepository.clearAllActivities()
    }
}
