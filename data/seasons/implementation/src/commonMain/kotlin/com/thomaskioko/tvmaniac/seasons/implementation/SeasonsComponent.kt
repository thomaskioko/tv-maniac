package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.util.scope.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface SeasonsComponent {

    @ApplicationScope
    @Provides
    fun provideSeasonsRepository(bind: SeasonsRepositoryImpl): SeasonsRepository =
        bind

    @ApplicationScope
    @Provides
    fun provideSeasonsDao(bind: SeasonsDaoImpl): SeasonsDao = bind
}
