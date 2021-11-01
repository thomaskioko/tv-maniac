package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepository
import com.thomaskioko.tvmaniac.datasource.repository.genre.GenreRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepository
import com.thomaskioko.tvmaniac.datasource.repository.seasons.SeasonsRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepository
import com.thomaskioko.tvmaniac.datasource.repository.trailers.TrailerRepositoryImpl
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepository
import com.thomaskioko.tvmaniac.datasource.repository.tvshow.TvShowsRepositoryImpl
import kotlinx.coroutines.MainScope

class RepositoryModule(
    private val networkModule: NetworkModule,
    private val databaseModule: DatabaseModule,
) {

    val episodeRepository: EpisodeRepository by lazy {
        EpisodeRepositoryImpl(
            apiService = networkModule.tmdbService,
            episodesCache = databaseModule.episodesCache,
            seasonCache = databaseModule.seasonsCache
        )
    }

    val genreRepository: GenreRepository by lazy {
        GenreRepositoryImpl(
            apiService = networkModule.tmdbService,
            genreCache = databaseModule.genreCache
        )
    }

    val seasonsRepository: SeasonsRepository by lazy {
        SeasonsRepositoryImpl(
            apiService = networkModule.tmdbService,
            tvShowCache = databaseModule.tvShowCache,
            seasonCache = databaseModule.seasonsCache
        )
    }

    val tvShowsRepository: TvShowsRepository by lazy {
        TvShowsRepositoryImpl(
            apiService = networkModule.tmdbService,
            cache = databaseModule.tvShowCache,
            coroutineScope = MainScope()
        )
    }

    val trailerRepository: TrailerRepository by lazy {
        TrailerRepositoryImpl(
            apiService = networkModule.tmdbService,
            cache = databaseModule.trailerCache
        )
    }
}
