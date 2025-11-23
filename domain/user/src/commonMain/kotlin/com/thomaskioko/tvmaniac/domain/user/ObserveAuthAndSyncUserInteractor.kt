package com.thomaskioko.tvmaniac.domain.user

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveAuthAndSyncUserInteractor(
    private val traktAuthRepository: TraktAuthRepository,
    private val updateUserProfileData: UpdateUserProfileData,
) : SubjectInteractor<Unit, Unit>() {

    override fun createObservable(params: Unit): Flow<Unit> =
        traktAuthRepository.state
            .distinctUntilChanged()
            .filter { it == TraktAuthState.LOGGED_IN }
            .map {
                updateUserProfileData.executeSync(
                    UpdateUserProfileData.Params(
                        username = "me",
                        forceRefresh = false,
                    ),
                )
            }
}
