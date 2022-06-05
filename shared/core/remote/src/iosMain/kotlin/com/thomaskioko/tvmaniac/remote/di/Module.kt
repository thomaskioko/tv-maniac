package com.thomaskioko.tvmaniac.remote.di

import com.thomaskioko.tvmaniac.remote.KtorClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun remotePlatformModule(): Module = module {
    single { KtorClientFactory().build() }
}
