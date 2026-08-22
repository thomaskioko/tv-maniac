package com.thomaskioko.tvmaniac.accountmanager.api

public class AccountSwitchFailedException(
    public val target: SyncProviderSource,
    override val cause: Throwable? = null,
) : Exception("Account switch to $target failed", cause)
