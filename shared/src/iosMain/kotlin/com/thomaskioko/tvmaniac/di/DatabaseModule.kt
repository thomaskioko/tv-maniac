package com.thomaskioko.tvmaniac.di

import com.squareup.sqldelight.EnumColumnAdapter
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.Tv_season
import com.thomaskioko.tvmaniac.datasource.cache.db.DriverFactory
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.intAdapter
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCache
import com.thomaskioko.tvmaniac.datasource.cache.episode.EpisodesCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCache
import com.thomaskioko.tvmaniac.datasource.cache.genre.GenreCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCache
import com.thomaskioko.tvmaniac.datasource.cache.seasons.SeasonsCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCache
import com.thomaskioko.tvmaniac.datasource.cache.shows.TvShowCacheImpl
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCache
import com.thomaskioko.tvmaniac.datasource.cache.trailers.TrailerCacheImpl


class DatabaseModule {

    private val driverFactory: DriverFactory by lazy { DriverFactory() }
    private val tvManiacDatabase: TvManiacDatabase by lazy {
        TvManiacDatabase(
            driver = driverFactory.createDriver(),
            showAdapter = Show.Adapter(
                genre_idsAdapter = intAdapter,
                season_idsAdapter = intAdapter,
                show_categoryAdapter = EnumColumnAdapter(),
                time_windowAdapter = EnumColumnAdapter(),
            ),
            tv_seasonAdapter = Tv_season.Adapter(
                episode_idsAdapter = intAdapter
            )
        )
    }

    val episodesCache : EpisodesCache by lazy {
        EpisodesCacheImpl(
            database = tvManiacDatabase
        )
    }

    val genreCache : GenreCache by lazy {
        GenreCacheImpl(
            database = tvManiacDatabase
        )
    }

    val seasonsCache : SeasonsCache by lazy {
        SeasonsCacheImpl(
            database = tvManiacDatabase
        )
    }

    val trailerCache : TrailerCache by lazy {
        TrailerCacheImpl(
            database = tvManiacDatabase
        )
    }

    val tvShowCache : TvShowCache by lazy {
        TvShowCacheImpl(
            database = tvManiacDatabase
        )
    }

}
