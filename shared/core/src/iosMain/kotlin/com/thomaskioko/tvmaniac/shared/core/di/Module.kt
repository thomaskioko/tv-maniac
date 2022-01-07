package com.thomaskioko.tvmaniac.shared.core.di

import com.thomaskioko.tvmaniac.shared.core.MainDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun corePlatformModule(): Module = module {
    single { MainDispatcher() }
}
