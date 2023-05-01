package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonsCache
import me.tatarka.inject.annotations.Provides

interface SeasonDetailsComponent {

    @Provides
    fun provideSeasonDetailsRepository(bind: SeasonDetailsRepositoryImpl): SeasonDetailsRepository =
        bind

    @Provides
    fun provideSeasonsCache(bind: SeasonsCacheImpl): SeasonsCache = bind
}
