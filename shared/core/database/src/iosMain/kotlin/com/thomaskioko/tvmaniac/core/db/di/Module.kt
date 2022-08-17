package com.thomaskioko.tvmaniac.core.db.di

import com.thomaskioko.tvmaniac.core.db.DriverFactory
import com.thomaskioko.tvmaniac.core.db.Show
import com.thomaskioko.tvmaniac.core.db.intAdapter
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun dbPlatformModule(): Module = module {

    single {
        TvManiacDatabase(
            driver = DriverFactory().createDriver(),
            showAdapter = Show.Adapter(
                genre_idsAdapter = intAdapter,
            ),
        )
    }
}
