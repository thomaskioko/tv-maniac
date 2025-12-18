package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

@Inject
public class LogoutInteractor(
    private val traktAuthRepository: TraktAuthRepository,
    private val userRepository: UserRepository,
    private val datastoreRepository: DatastoreRepository,
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        val currentUser = userRepository.observeCurrentUser().first()
        datastoreRepository.saveLastTraktUserId(currentUser?.slug)

        traktAuthRepository.logout()
        userRepository.clearUserData()
    }
}
