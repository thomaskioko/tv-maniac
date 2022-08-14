package com.thomaskioko.tvmaniac.details.implementation.di

import com.thomaskioko.tvmaniac.details.api.cache.ShowCategoryCache
import com.thomaskioko.tvmaniac.details.api.interactor.ObserveShowInteractor
import com.thomaskioko.tvmaniac.details.api.interactor.UpdateFollowingInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TvShowsRepository
import com.thomaskioko.tvmaniac.details.implementation.cache.ShowCategoryCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.cache.TvShowCacheImpl
import com.thomaskioko.tvmaniac.details.implementation.repository.TvShowsRepositoryImpl
import com.thomaskioko.tvmaniac.showcommon.api.cache.TvShowCache
import org.koin.core.module.Module
import org.koin.dsl.module

val detailDomainModule: Module = module {
    single<TvShowsRepository> {
        TvShowsRepositoryImpl(get(), get(), get(), get(), get(), get())
    }

    factory { ObserveShowInteractor(get(), get(), get(), get(), get(), get()) }
    factory { UpdateFollowingInteractor(get()) }

    single<TvShowCache> { TvShowCacheImpl(get()) }
    single<ShowCategoryCache> { ShowCategoryCacheImpl(get()) }
}
