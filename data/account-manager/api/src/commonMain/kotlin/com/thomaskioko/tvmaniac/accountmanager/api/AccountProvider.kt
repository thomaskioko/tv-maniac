package com.thomaskioko.tvmaniac.accountmanager.api

public enum class AccountProvider {
    TRAKT,
    SIMKL,
}

public val AccountProvider.displayName: String
    get() = when (this) {
        AccountProvider.TRAKT -> "Trakt"
        AccountProvider.SIMKL -> "Simkl"
    }
