package com.thomaskioko.tvmaniac.data.showdetails

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun showDetailsDomainModule(): Module = module {
    factory { ShowDetailsStateMachine(get(), get(), get(), get()) }
}