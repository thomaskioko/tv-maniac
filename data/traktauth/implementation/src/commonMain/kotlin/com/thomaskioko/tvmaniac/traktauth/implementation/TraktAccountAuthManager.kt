package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.api.AuthManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.SingleIn

@SingleIn(ActivityScope::class)
@ContributesIntoSet(ActivityScope::class)
public class TraktAccountAuthManager(
    private val traktAuthManager: TraktAuthManager,
) : AuthManager {

    override val provider: AccountProvider = AccountProvider.TRAKT

    override fun launchWebView() {
        traktAuthManager.launchWebView()
    }
}
