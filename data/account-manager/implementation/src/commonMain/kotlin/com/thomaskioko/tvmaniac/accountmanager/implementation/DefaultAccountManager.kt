package com.thomaskioko.tvmaniac.accountmanager.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthRepository
import com.thomaskioko.tvmaniac.accountmanager.api.AccountAuthState
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthError
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.ConnectedAccount
import com.thomaskioko.tvmaniac.accountmanager.api.TokenRefreshResult
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultAccountManager(
    authRepositories: Set<AccountAuthRepository>,
) : AccountManager {

    private val repositories: List<AccountAuthRepository> = authRepositories.toList()
    private val selectedProvider = MutableStateFlow<AccountProvider?>(null)

    private val providerStates: Flow<List<Pair<AccountProvider, AccountAuthState>>> =
        if (repositories.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(repositories.map { repo -> repo.state.map { repo.provider to it } }) { it.toList() }
        }

    override val activeProvider: Flow<AccountProvider?> =
        combine(providerStates, selectedProvider) { states, selected -> resolveActive(states, selected) }
            .distinctUntilChanged()

    override val isConnected: Flow<Boolean> = activeProvider.map { it != null }

    override val connectionEvents: Flow<AccountProvider> =
        repositories.map { repo -> repo.loginEvents.map { repo.provider } }.merge()

    override val accounts: Flow<List<ConnectedAccount>> =
        combine(providerStates, selectedProvider) { states, selected ->
            val active = resolveActive(states, selected)
            states.map { (provider, state) ->
                ConnectedAccount(
                    provider = provider,
                    isConnected = state == AccountAuthState.LOGGED_IN,
                    isActive = provider == active,
                )
            }
        }.distinctUntilChanged()

    override val activeAccount: Flow<ConnectedAccount?> =
        accounts.map { list -> list.firstOrNull { it.isActive } }.distinctUntilChanged()

    override val authError: Flow<AuthError?> =
        if (repositories.isEmpty()) flowOf(null) else repositories.map { it.authError }.merge()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val activeAuthState: Flow<AuthState?> =
        if (repositories.isEmpty()) {
            flowOf(null)
        } else {
            activeProvider.flatMapLatest { provider ->
                repositories.firstOrNull { it.provider == provider }?.authState ?: flowOf(null)
            }
        }

    override fun getActiveProvider(): AccountProvider? {
        val selected = selectedProvider.value
        if (selected != null && repositories.any { it.provider == selected && it.isLoggedIn() }) {
            return selected
        }
        return repositories.firstOrNull { it.isLoggedIn() }?.provider
    }

    override suspend fun logout(provider: AccountProvider) {
        repositories.firstOrNull { it.provider == provider }?.logout()
        if (selectedProvider.value == provider) {
            selectedProvider.value = null
        }
    }

    override suspend fun setActive(provider: AccountProvider) {
        selectedProvider.value = provider
    }

    override suspend fun setAuthError(error: AuthError?) {
        repositories.forEach { it.setAuthError(error) }
    }

    override suspend fun refreshActiveTokens(): TokenRefreshResult =
        activeRepository()?.refreshTokens() ?: TokenRefreshResult.NotLoggedIn

    private fun activeRepository(): AccountAuthRepository? =
        getActiveProvider()?.let { provider -> repositories.firstOrNull { it.provider == provider } }

    private fun resolveActive(
        states: List<Pair<AccountProvider, AccountAuthState>>,
        selected: AccountProvider?,
    ): AccountProvider? {
        val loggedIn = states.filter { it.second == AccountAuthState.LOGGED_IN }.map { it.first }
        return when {
            selected != null && selected in loggedIn -> selected
            else -> loggedIn.firstOrNull()
        }
    }
}
