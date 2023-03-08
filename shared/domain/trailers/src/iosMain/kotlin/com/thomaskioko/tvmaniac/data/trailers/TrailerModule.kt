package com.thomaskioko.tvmaniac.data.trailers

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun trailerDomainModule(): Module = module {
    factory { TrailersStateMachine(get()) }
}