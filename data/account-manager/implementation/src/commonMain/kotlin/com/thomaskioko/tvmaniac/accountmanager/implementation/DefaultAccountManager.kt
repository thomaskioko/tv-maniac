package com.thomaskioko.tvmaniac.accountmanager.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultAccountManager(
    private val traktAuthRepository: TraktAuthRepository,
) : AccountManager {

    override val activeProvider: Flow<AccountProvider?> =
        traktAuthRepository.state.map { it.toActiveProvider() }

    override val isConnected: Flow<Boolean> =
        activeProvider.map { it != null }

    override val connectionEvents: Flow<AccountProvider> =
        traktAuthRepository.loginEvents.map { AccountProvider.TRAKT }

    override fun getActiveProvider(): AccountProvider? =
        if (traktAuthRepository.isLoggedIn()) AccountProvider.TRAKT else null
}

private fun TraktAuthState.toActiveProvider(): AccountProvider? =
    if (this == TraktAuthState.LOGGED_IN) AccountProvider.TRAKT else null
