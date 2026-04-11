package com.thomaskioko.tvmaniac.core.base.di

import dev.zacsweers.metro.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
public annotation class ApplicationContext

@Qualifier
@Retention(BINARY)
public annotation class ActivityContext

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
public annotation class TmdbApi

@Qualifier
@Retention(BINARY)
public annotation class TraktApi
