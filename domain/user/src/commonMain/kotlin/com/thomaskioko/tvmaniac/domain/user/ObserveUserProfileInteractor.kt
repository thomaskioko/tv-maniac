package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.domain.user.model.UserProfile
import com.thomaskioko.tvmaniac.domain.user.model.UserStats
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile as DataUserProfile

@Inject
public class ObserveUserProfileInteractor(
    private val userRepository: UserRepository,
    private val accountManager: AccountManager,
) : SubjectInteractor<Unit, UserProfile?>() {

    override fun createObservable(params: Unit): Flow<UserProfile?> {
        return combine(
            accountManager.isConnected,
            userRepository.observeCurrentUser(),
        ) { isConnected, user ->
            user?.toDomain(if (isConnected) AccountAuthState.LOGGED_IN else AccountAuthState.LOGGED_OUT)
        }
    }
}

private fun DataUserProfile.toDomain(authState: AccountAuthState): UserProfile {
    return UserProfile(
        slug = slug,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        stats = UserStats(
            showsWatched = stats.showsWatched.toInt(),
            episodesWatched = stats.episodesWatched.toInt(),
            showsWatchedLabel = stats.showsWatchedLabel,
            episodesWatchedLabel = stats.episodesWatchedLabel,
            userWatchTime = stats.userWatchTime,
        ),
        authState = authState,
        statsLoaded = statsLoaded,
    )
}
