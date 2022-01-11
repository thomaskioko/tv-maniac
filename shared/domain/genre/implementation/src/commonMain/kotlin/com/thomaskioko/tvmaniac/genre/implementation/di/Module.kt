package com.thomaskioko.tvmaniac.genre.implementation.di

import com.thomaskioko.tvmaniac.genre.api.GenreCache
import com.thomaskioko.tvmaniac.genre.api.GenreRepository
import com.thomaskioko.tvmaniac.genre.api.GetGenresInteractor
import com.thomaskioko.tvmaniac.genre.implementation.GenreCacheImpl
import com.thomaskioko.tvmaniac.genre.implementation.GenreRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val genreModule: Module = module {
    single<GenreRepository> { GenreRepositoryImpl(get(), get(), get()) }
    single<GenreCache> { GenreCacheImpl(get()) }
    factory { GetGenresInteractor(get()) }
}
