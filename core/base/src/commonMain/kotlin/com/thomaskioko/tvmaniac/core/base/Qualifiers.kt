package com.thomaskioko.tvmaniac.core.base

import dev.zacsweers.metro.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
public annotation class ApplicationContext

@Qualifier
@Retention(BINARY)
public annotation class Initializers

@Qualifier
@Retention(BINARY)
public annotation class AsyncInitializers

@Qualifier
@Retention(BINARY)
public annotation class MainCoroutineScope

@Qualifier
@Retention(BINARY)
public annotation class IoCoroutineScope

@Qualifier
@Retention(BINARY)
public annotation class ComputationCoroutineScope

@Qualifier
@Retention(BINARY)
public annotation class SyncScope

@Qualifier
@Retention(BINARY)
public annotation class TmdbApi

@Qualifier
@Retention(BINARY)
public annotation class TraktApi

@Qualifier
@Retention(BINARY)
public annotation class SimklApi

@Qualifier
@Retention(BINARY)
public annotation class SimklDataApi

@Qualifier
@Retention(BINARY)
public annotation class IsDebugBuild

@Qualifier
@Retention(BINARY)
public annotation class AppPreferencesDataStore

@Qualifier
@Retention(BINARY)
public annotation class FeatureFlagLocalsDataStore

/**
 * Preferences that must not follow the user to a new device.
 *
 * Kept in a file the Android backup rules exclude, so a restored device does not inherit a record of
 * something that never happened on it.
 */
@Qualifier
@Retention(BINARY)
public annotation class DeviceLocalDataStore
