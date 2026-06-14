package com.thomaskioko.tvmaniac.domain.logout

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.logout.api.LogoutHandler
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
public class LogoutInteractor(
    private val accountManager: AccountManager,
    private val userRepository: UserRepository,
    private val datastoreRepository: DatastoreRepository,
    private val logoutHandler: LogoutHandler,
) : Interactor<Unit>() {
    override suspend fun doWork(params: Unit) {
        val currentUser = userRepository.observeCurrentUser().first()
        datastoreRepository.saveLastTraktUserId(currentUser?.slug)

        accountManager.getActiveProvider()?.let { accountManager.logout(it) }
        logoutHandler.clear()
    }
}
