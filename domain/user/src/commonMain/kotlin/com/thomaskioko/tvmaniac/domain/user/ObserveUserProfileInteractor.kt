package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.domain.user.model.UserProfile
import com.thomaskioko.tvmaniac.domain.user.model.UserStats
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject
import com.thomaskioko.tvmaniac.data.user.api.model.UserProfile as DataUserProfile

@Inject
public class ObserveUserProfileInteractor(
    private val userRepository: UserRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : SubjectInteractor<Unit, UserProfile?>() {

    override fun createObservable(params: Unit): Flow<UserProfile?> {
        return combine(
            traktAuthRepository.state,
            userRepository.observeCurrentUser(),
        ) { authState, user ->
            user?.toDomain(authState)
        }
    }
}

private fun DataUserProfile.toDomain(authState: TraktAuthState): UserProfile {
    return UserProfile(
        slug = slug,
        username = username,
        fullName = fullName,
        avatarUrl = avatarUrl,
        backgroundUrl = backgroundUrl,
        stats = UserStats(
            showsWatched = stats.showsWatched.toInt(),
            episodesWatched = stats.episodesWatched.toInt(),
            userWatchTime = stats.userWatchTime,
        ),
        authState = authState,
    )
}
