package com.thomaskioko.tvmaniac.oauth.testing

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.oauth.api.AuthStore

public class FakeAuthStore : AuthStore {
    private val savedStates: MutableMap<AccountProvider, AuthState> = mutableMapOf()
    public var getCallCount: Int = 0
        private set

    override suspend fun get(provider: AccountProvider): AuthState? {
        getCallCount++
        return savedStates[provider]
    }

    override suspend fun save(provider: AccountProvider, state: AuthState) {
        savedStates[provider] = state
    }

    override suspend fun clear(provider: AccountProvider) {
        savedStates.remove(provider)
    }

    public fun reset() {
        savedStates.clear()
        getCallCount = 0
    }
}
