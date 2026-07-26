package com.thomaskioko.tvmaniac.testing.integration

import com.thomaskioko.tvmaniac.accountmanager.api.AuthState
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.datastore.implementation.DATA_STORE_FILE_NAME
import com.thomaskioko.tvmaniac.datastore.implementation.clearDataStoreReference
import com.thomaskioko.tvmaniac.db.IosDatabaseDriverBuilder
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.runBlocking
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

public object IosTestHooks {

    @OptIn(ExperimentalForeignApi::class)
    public fun clearPersistentStateIfNeeded() {
        if (!StubHttpEngine.shouldClearPersistentState) return

        IosDatabaseDriverBuilder().deleteDatabase()

        clearDataStoreReference()
        val documents: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        documents?.path?.let { path ->
            NSFileManager.defaultManager.removeItemAtPath("$path/$DATA_STORE_FILE_NAME", null)
        }
    }

    public fun saveAuthStateIfNeeded(authStore: AuthStore) {
        val scenarioAuthState = StubHttpEngine.scenario?.authState ?: return
        val provider = SyncProviderSource.valueOf(scenarioAuthState.provider)
        runBlocking {
            authStore.save(
                provider = provider,
                state = AuthState(
                    accessToken = scenarioAuthState.accessToken,
                    refreshToken = scenarioAuthState.refreshToken,
                    isAuthorized = true,
                    expiresAt = Clock.System.now() + scenarioAuthState.expiresInSeconds.seconds,
                    tokenLifetimeSeconds = scenarioAuthState.expiresInSeconds,
                ),
            )
        }
    }
}
