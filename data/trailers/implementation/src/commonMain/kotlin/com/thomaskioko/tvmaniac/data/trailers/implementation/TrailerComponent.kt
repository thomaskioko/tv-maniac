package com.thomaskioko.tvmaniac.data.trailers.implementation

import me.tatarka.inject.annotations.Provides

interface TrailerComponent {

    @Provides
    fun provideTrailerCache(bind: TrailerDaoImpl): TrailerDao = bind

    @Provides
    fun provideTrailerRepository(bind: TrailerRepositoryImpl): TrailerRepository = bind
}
