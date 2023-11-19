package com.thomaskioko.tvmaniac.seasondetails.implementation

import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface SeasonDetailsComponent {

    @ApplicationScope
    @Provides
    fun provideSeasonDetailsRepository(
        bind: SeasonDetailsRepositoryImpl,
    ): SeasonDetailsRepository = bind
}
