package com.thomaskioko.tvmaniac.shared.core.ui.di

import com.thomaskioko.tvmaniac.shared.core.ui.CoroutineScopeProvider
import com.thomaskioko.tvmaniac.shared.core.ui.MainDispatcher
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun coreUiPlatformModule(): Module = module {
    single { MainDispatcher() }
    single { CoroutineScopeProvider() }
}
