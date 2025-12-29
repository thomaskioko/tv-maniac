package com.thomaskioko.tvmaniac.traktauth.testing

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.AuthStore

public class FakeAuthStore : AuthStore {
    private var savedState: AuthState? = null
    public var getCallCount: Int = 0
        private set

    override suspend fun get(): AuthState? {
        getCallCount++
        return savedState
    }

    override suspend fun save(state: AuthState) {
        savedState = state
    }

    override suspend fun clear() {
        savedState = null
    }

    public fun reset() {
        savedState = null
        getCallCount = 0
    }
}
