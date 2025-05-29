package com.thomaskioko.trakt.service.implementation

import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface TraktPlatformComponent {

    @Provides
    fun provideTraktHttpClientEngine(): TraktHttpClientEngine = Darwin.create()
}
