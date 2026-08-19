package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveRewatchSupportInteractor(
    private val accountManager: AccountManager,
    private val rewatchRepository: RewatchRepository,
) : SubjectInteractor<Unit, Boolean>() {

    override fun createObservable(params: Unit): Flow<Boolean> =
        accountManager.activeProvider.map { rewatchRepository.supportsRewatch() }
}
