package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.ShowDetailsStateMachine
import org.koin.core.module.Module
import org.koin.dsl.module

fun detailDomainModule(): Module = module {
    single { ShowDetailsStateMachine(get(), get(), get(), get()) }
}
