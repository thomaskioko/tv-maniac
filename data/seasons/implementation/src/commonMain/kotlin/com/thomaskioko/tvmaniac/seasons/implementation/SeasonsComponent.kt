package com.thomaskioko.tvmaniac.seasons.implementation

import com.thomaskioko.tvmaniac.seasons.api.SeasonsDao
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import me.tatarka.inject.annotations.Provides

interface SeasonsComponent {

    @Provides
    fun provideSeasonsRepository(bind: SeasonsRepositoryImpl): SeasonsRepository =
        bind

    @Provides
    fun provideSeasonsDao(bind: SeasonsDaoImpl): SeasonsDao = bind
}
