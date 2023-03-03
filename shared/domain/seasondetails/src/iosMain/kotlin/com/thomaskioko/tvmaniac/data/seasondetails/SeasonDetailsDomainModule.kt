package com.thomaskioko.tvmaniac.data.seasondetails

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun seasonDetailsDomainModule(): Module = module {
    factory { SeasonDetailsStateMachine(get(), get()) }
}