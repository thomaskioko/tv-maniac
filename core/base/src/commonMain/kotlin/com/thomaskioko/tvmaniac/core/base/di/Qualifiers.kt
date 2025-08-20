package com.thomaskioko.tvmaniac.core.base.di

import dev.zacsweers.metro.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(BINARY)
annotation class ApplicationContext

@Qualifier
@Retention(BINARY)
annotation class ActivityContext

@Qualifier
@Retention(BINARY)
annotation class Initializers

@Qualifier
@Retention(BINARY)
annotation class AsyncInitializers

@Qualifier
@Retention(BINARY)
annotation class MainCoroutineScope

@Qualifier
@Retention(BINARY)
annotation class IoCoroutineScope

@Qualifier
@Retention(BINARY)
annotation class ComputationCoroutineScope
