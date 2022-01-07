package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.datasource.network.KtorClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { KtorClientFactory().build() }
}
