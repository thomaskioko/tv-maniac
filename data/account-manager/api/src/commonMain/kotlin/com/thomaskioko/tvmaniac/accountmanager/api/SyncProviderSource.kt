package com.thomaskioko.tvmaniac.accountmanager.api

import com.thomaskioko.tvmaniac.db.Provider

public enum class SyncProviderSource {
    TRAKT,
    SIMKL,
}

public val SyncProviderSource.displayName: String
    get() = when (this) {
        SyncProviderSource.TRAKT -> "Trakt"
        SyncProviderSource.SIMKL -> "Simkl"
    }

public fun SyncProviderSource.toDbProvider(): Provider = when (this) {
    SyncProviderSource.TRAKT -> Provider.TRAKT
    SyncProviderSource.SIMKL -> Provider.SIMKL
}
