package com.thomaskioko.tvmaniac.shared.core.firebaseconfig.implementation

import co.touchlab.kermit.Logger
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfig
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigErrorDomain
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigErrorInternalError
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigErrorThrottled
import cocoapods.FirebaseRemoteConfig.FIRRemoteConfigFetchAndActivateStatus
import com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api.FeatureFlagsRemoteConfig
import kotlinx.coroutines.CompletableDeferred
import platform.Foundation.NSError

actual class FirebaseRemoteFeatureFlagsConfig constructor(
    private val remoteConfig: FIRRemoteConfig
) : FeatureFlagsRemoteConfig {

    override suspend fun fetch() {
        val status: FIRRemoteConfigFetchAndActivateStatus = remoteConfig.awaitResult {
            fetchAndActivateWithCompletionHandler(it)
        }

        Logger.withTag("@fetch").d { "Firebase activation status $status" }

        val activateStatus = FIRRemoteConfigFetchAndActivateStatus.FIRRemoteConfigFetchAndActivateStatusSuccessFetchedFromRemote

        Logger.withTag("@fetch").d { "Firebase remote config status $activateStatus" }
    }

    override suspend fun getBoolean(flagName: String): Boolean = remoteConfig.configValueForKey(flagName).boolValue
}

private suspend inline fun <T, reified R> T.awaitResult(
    function: T.(callback: (R?, NSError?) -> Unit) -> Unit
): R {
    val job = CompletableDeferred<R?>()
    function { result, error ->
        if (error == null) {
            job.complete(result)
        } else {
            job.completeExceptionally(error.toException())
        }
    }
    return job.await() as R
}

private fun NSError.toException() = when (domain) {
    FIRRemoteConfigErrorDomain -> {
        when (code) {
            FIRRemoteConfigErrorThrottled -> Exception(
                localizedDescription
            )

            FIRRemoteConfigErrorInternalError -> Exception(
                localizedDescription
            )

            else -> Exception(localizedDescription)
        }
    }

    else -> Exception(localizedDescription)
}
