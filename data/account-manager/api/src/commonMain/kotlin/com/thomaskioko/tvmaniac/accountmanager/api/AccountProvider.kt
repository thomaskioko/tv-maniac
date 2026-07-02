package com.thomaskioko.tvmaniac.accountmanager.api

import com.thomaskioko.tvmaniac.db.Provider

public enum class AccountProvider {
    TRAKT,
    SIMKL,
}

public val AccountProvider.displayName: String
    get() = when (this) {
        AccountProvider.TRAKT -> "Trakt"
        AccountProvider.SIMKL -> "Simkl"
    }

public fun AccountProvider.toDbProvider(): Provider = when (this) {
    AccountProvider.TRAKT -> Provider.TRAKT
    AccountProvider.SIMKL -> Provider.SIMKL
}
