package com.thomaskioko.tvmaniac.connectedaccount.implementation

import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedProvider
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultConnectedAccountRepository(
    private val traktAuthRepository: TraktAuthRepository,
) : ConnectedAccountRepository {

    override val activeProvider: Flow<ConnectedProvider?> =
        traktAuthRepository.state.map { it.toActiveProvider() }

    override val isConnected: Flow<Boolean> =
        activeProvider.map { it != null }

    override val connectionEvents: Flow<ConnectedProvider> =
        traktAuthRepository.loginEvents.map { ConnectedProvider.TRAKT }

    override fun getActiveProvider(): ConnectedProvider? =
        if (traktAuthRepository.isLoggedIn()) ConnectedProvider.TRAKT else null
}

private fun TraktAuthState.toActiveProvider(): ConnectedProvider? =
    if (this == TraktAuthState.LOGGED_IN) ConnectedProvider.TRAKT else null
