package com.thomaskioko.tvmaniac.accountmanager.api

import com.thomaskioko.tvmaniac.db.Provider
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

public enum class SyncProviderSource {
    TRAKT,
    SIMKL,
}

public val SyncProviderSource.displayName: String
    get() = when (this) {
        SyncProviderSource.TRAKT -> "Trakt"
        SyncProviderSource.SIMKL -> "Simkl"
    }

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
public fun SyncProviderSource.toDbProvider(): Provider = when (this) {
    SyncProviderSource.TRAKT -> Provider.TRAKT
    SyncProviderSource.SIMKL -> Provider.SIMKL
}
