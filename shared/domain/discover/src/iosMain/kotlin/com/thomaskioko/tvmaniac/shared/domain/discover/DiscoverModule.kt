package com.thomaskioko.tvmaniac.shared.domain.discover

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun discoverDomainModule(): Module = module {
    factory { DiscoverStateMachine(get(), get()) }
}