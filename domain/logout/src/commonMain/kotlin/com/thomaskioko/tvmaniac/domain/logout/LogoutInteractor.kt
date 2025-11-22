package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class LogoutInteractor(
    private val traktAuthRepository: TraktAuthRepository,
    private val userRepository: UserRepository,
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        traktAuthRepository.logout()
        userRepository.clearUserData()
    }
}
