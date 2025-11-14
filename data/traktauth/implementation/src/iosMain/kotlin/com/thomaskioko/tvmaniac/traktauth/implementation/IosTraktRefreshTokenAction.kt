package com.thomaskioko.tvmaniac.traktauth.implementation

import com.thomaskioko.tvmaniac.traktauth.api.AuthState
import com.thomaskioko.tvmaniac.traktauth.api.TraktRefreshTokenAction
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IosTraktRefreshTokenAction : TraktRefreshTokenAction {
    override suspend fun invoke(currentState: AuthState): AuthState? {
        return null
    }
}
