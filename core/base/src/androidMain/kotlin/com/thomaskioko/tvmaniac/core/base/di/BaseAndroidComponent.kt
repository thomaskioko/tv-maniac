package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface BaseAndroidComponent {

  @Provides
  fun provideCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    AppCoroutineScope(
      default = CoroutineScope(Job() + dispatchers.computation),
      io = CoroutineScope(Job() + dispatchers.io),
      main = CoroutineScope(Job() + dispatchers.main),
    )
}
