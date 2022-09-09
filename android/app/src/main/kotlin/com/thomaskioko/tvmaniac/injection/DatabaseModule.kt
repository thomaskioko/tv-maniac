package com.thomaskioko.tvmaniac.injection

import android.content.Context
import com.thomaskioko.tvmaniac.core.db.DriverFactory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabaseFactory
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Dagger can't see SQLDelight classes and causes the build to fail. Once this is resolved, this
 * class will move back to the shared module.
 */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDriverFactory(@ApplicationContext context: Context): DriverFactory =
        DriverFactory(context = context)

    @Singleton
    @Provides
    fun provideTvShowDatabase(driverFactory: DriverFactory): TvManiacDatabase =
        TvManiacDatabaseFactory(driverFactory).createDatabase()
}
