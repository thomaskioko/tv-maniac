package com.thomaskioko.tvmaniac.core.db.di

import android.content.Context
import com.thomaskioko.tvmaniac.core.db.DriverFactory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabaseFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDriverFactory(@ApplicationContext context: Context): DriverFactory =
        DriverFactory(context = context)

    @Singleton
    @Provides
    fun provideTvManiacDatabase(driverFactory: DriverFactory): TvManiacDatabase =
        TvManiacDatabaseFactory(driverFactory).createDatabase()
}
