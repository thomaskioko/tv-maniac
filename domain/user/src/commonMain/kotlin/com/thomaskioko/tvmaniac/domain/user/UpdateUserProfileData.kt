package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class UpdateUserProfileData(
    private val userRepository: UserRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<UpdateUserProfileData.Params>() {

    override suspend fun doWork(params: Params) {
        withContext(dispatchers.io) {
            userRepository.fetchUserProfile(
                username = params.username,
                forceRefresh = params.forceRefresh,
            )

            val slug = userRepository.observeCurrentUser().first()?.slug ?: return@withContext

            userRepository.fetchUserStats(
                slug = slug,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    public data class Params(
        val username: String = "me",
        val forceRefresh: Boolean = false,
    )
}
