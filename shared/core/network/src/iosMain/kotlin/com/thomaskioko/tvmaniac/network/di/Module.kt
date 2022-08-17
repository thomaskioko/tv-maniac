package com.thomaskioko.tvmaniac.network.di

import com.thomaskioko.tvmaniac.network.KtorClientFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun networkPlatformModule(): Module = module {
    single(named("tmdb-url")) {
        KtorClientFactory().build(get(named("tmdb-url")))
    }
}
