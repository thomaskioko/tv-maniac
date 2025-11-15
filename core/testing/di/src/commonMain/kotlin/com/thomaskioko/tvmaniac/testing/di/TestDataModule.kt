package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfigRepository
import com.thomaskioko.tvmaniac.buildconfig.testing.FakeBuildConfigRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(TestScope::class)
interface TestDataModule {
    @Provides
    @SingleIn(TestScope::class)
    fun provideDatastoreRepository(): DatastoreRepository = FakeDatastoreRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideTraktAuthManager(): TraktAuthManager = FakeTraktAuthManager()

    @Provides
    @SingleIn(TestScope::class)
    fun provideRequestManagerRepository(): RequestManagerRepository = FakeRequestManagerRepository()

    @Provides
    @SingleIn(TestScope::class)
    fun provideSecureConfigRepository(): BuildConfigRepository = FakeBuildConfigRepository()
}
