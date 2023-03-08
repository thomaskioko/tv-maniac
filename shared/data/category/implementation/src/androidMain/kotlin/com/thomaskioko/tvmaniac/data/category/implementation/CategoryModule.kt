package com.thomaskioko.tvmaniac.data.category.implementation

import org.koin.core.module.Module as KoinModule
import com.thomaskioko.tvmaniac.category.api.cache.CategoryCache
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.koin.dsl.module
import javax.inject.Singleton


actual fun categoryDataModule() : KoinModule = module {  }

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Singleton
    @Provides
    fun provideCategoryCache(
        database: TvManiacDatabase
    ): CategoryCache = CategoryCacheImpl(database)

}
