package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val detailDomainModule: Module = module {
    factory { ObserveShowInteractor(get(), get(), get(), get(), get(), get()) }
    factory { UpdateFollowingInteractor(get()) }

}
