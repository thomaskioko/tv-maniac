package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsDao
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import me.tatarka.inject.annotations.Provides

interface SeasonDetailsComponent {

    @Provides
    fun provideSeasonDetailsRepository(
        bind: SeasonDetailsRepositoryImpl,
    ): SeasonDetailsRepository = bind

    @Provides
    fun provideSeasonsDetailsDao(bind: SeasonDetailsDaoImpl): SeasonDetailsDao = bind
}
