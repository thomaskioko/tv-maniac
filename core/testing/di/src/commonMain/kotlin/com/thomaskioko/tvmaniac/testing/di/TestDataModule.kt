package com.thomaskioko.tvmaniac.testing.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.data.user.testing.FakeUserRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(TestScope::class)
public interface TestDataModule {
    @Provides
    @SingleIn(TestScope::class)
    public fun provideDatastoreRepository(): DatastoreRepository = FakeDatastoreRepository()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideTraktAuthManager(): TraktAuthManager = FakeTraktAuthManager()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideTraktAuthRepository(): TraktAuthRepository = FakeTraktAuthRepository()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideLogger(): Logger = FakeLogger()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideUserRepository(): UserRepository = FakeUserRepository()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideRequestManagerRepository(): RequestManagerRepository = FakeRequestManagerRepository()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideTraktActivityRepository(): TraktActivityRepository =
        FakeTraktActivityRepository()

    @Provides
    @SingleIn(TestScope::class)
    public fun provideAppCoroutineDispatchers(): AppCoroutineDispatchers = AppCoroutineDispatchers(
        io = Dispatchers.Default,
        computation = Dispatchers.Default,
        databaseWrite = Dispatchers.Default,
        databaseRead = Dispatchers.Default,
        main = Dispatchers.Default,
    )
}
