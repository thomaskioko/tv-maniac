package com.thomaskioko.tvmaniac.testing.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.db.SqlDriver
import com.thomaskioko.trakt.service.implementation.TraktPlatformBindingContainer
import com.thomaskioko.tvmaniac.appconfig.IosAppConfigBindingContainer
import com.thomaskioko.tvmaniac.core.base.AppPreferencesDataStore
import com.thomaskioko.tvmaniac.core.base.IoCoroutineScope
import com.thomaskioko.tvmaniac.core.logger.IosCrashReporter
import com.thomaskioko.tvmaniac.core.logger.IosCrashReporterBindingContainer
import com.thomaskioko.tvmaniac.core.notifications.implementation.IosNotificationManager
import com.thomaskioko.tvmaniac.core.tasks.implementation.IosTaskScheduler
import com.thomaskioko.tvmaniac.datastore.implementation.DATA_STORE_FILE_NAME
import com.thomaskioko.tvmaniac.datastore.implementation.DataStorePlatformBindingContainer
import com.thomaskioko.tvmaniac.db.DatabasePlatformBindingContainer
import com.thomaskioko.tvmaniac.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.db.createNativeSqliteDriver
import com.thomaskioko.tvmaniac.oauth.api.AuthStore
import com.thomaskioko.tvmaniac.oauth.implementation.IosAuthStore
import com.thomaskioko.tvmaniac.oauth.implementation.IosOAuthLauncher
import com.thomaskioko.tvmaniac.oauth.testing.FakeAuthStore
import com.thomaskioko.tvmaniac.simkl.implementation.SimklPlatformBindingContainer
import com.thomaskioko.tvmaniac.tmdb.implementation.TmdbPlatformBindingContainer
import com.thomaskioko.tvmaniac.util.IosAppUtils
import com.thomaskioko.tvmaniac.util.IosFormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import kotlin.random.Random

@BindingContainer
@ContributesTo(
    AppScope::class,
    replaces = [
        DatabasePlatformBindingContainer::class,
        DataStorePlatformBindingContainer::class,
        IosAppConfigBindingContainer::class,
        SimklPlatformBindingContainer::class,
        TmdbPlatformBindingContainer::class,
        TraktPlatformBindingContainer::class,
        IosAuthStore::class,
        IosFormatterUtil::class,
        IosAppUtils::class,
        IosOAuthLauncher::class,
        IosNotificationManager::class,
        IosCrashReporter::class,
        IosCrashReporterBindingContainer::class,
        IosTaskScheduler::class,
    ],
)
internal object FakeIosPlatformBindingContainer {

    @Provides
    @SingleIn(AppScope::class)
    fun provideAuthStore(): AuthStore = FakeAuthStore()

    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriver(): SqlDriver = createNativeSqliteDriver(
        schema = TvManiacDatabase.Schema,
        name = "tvmaniac-test-${uniqueSuffix()}.db",
        inMemory = true,
    )

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    @SingleIn(AppScope::class)
    @AppPreferencesDataStore
    fun provideDataStore(
        @IoCoroutineScope scope: CoroutineScope,
    ): DataStore<Preferences> {
        val directory = NSTemporaryDirectory() + "tvmaniac-test-${uniqueSuffix()}"
        NSFileManager.defaultManager.createDirectoryAtPath(
            path = directory,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        return PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            scope = scope,
            produceFile = { "$directory/$DATA_STORE_FILE_NAME".toPath() },
        )
    }

    private fun uniqueSuffix(): String = Random.nextLong(from = 0, until = Long.MAX_VALUE).toString()
}
