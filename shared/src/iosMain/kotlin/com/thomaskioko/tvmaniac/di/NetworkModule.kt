package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.datasource.network.KtorClientFactory
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsService
import com.thomaskioko.tvmaniac.datasource.network.api.TvShowsServiceImpl

open class NetworkModule {

    val tmdbService: TvShowsService by lazy {
        TvShowsServiceImpl(
            httpClient = KtorClientFactory().build()
        )
    }
}