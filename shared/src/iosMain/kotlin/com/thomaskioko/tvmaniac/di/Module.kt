package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.core.MainDispatcher
import com.thomaskioko.tvmaniac.datasource.cache.Season
import com.thomaskioko.tvmaniac.datasource.cache.Show
import com.thomaskioko.tvmaniac.datasource.cache.TvManiacDatabase
import com.thomaskioko.tvmaniac.datasource.cache.db.DriverFactory
import com.thomaskioko.tvmaniac.datasource.cache.db.adapter.intAdapter
import com.thomaskioko.tvmaniac.datasource.network.KtorClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { MainDispatcher() }
    single { KtorClientFactory().build() }

    single {
        TvManiacDatabase(
            driver = DriverFactory().createDriver(),
            showAdapter = Show.Adapter(
                genre_idsAdapter = intAdapter,
                season_idsAdapter = intAdapter,
            ),
            seasonAdapter = Season.Adapter(
                episode_idsAdapter = intAdapter
            )
        )
    }
}
