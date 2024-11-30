package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import com.thomaskioko.tvmaniac.core.base.scope.NsQueueCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface BaseComponent {

  @Provides
  @SingleIn(AppScope::class)
  fun provideCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.Default,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @Provides
  @SingleIn(AppScope::class)
  fun provideAppCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    AppCoroutineScope(
      default = CoroutineScope(Job() + dispatchers.computation),
      io = CoroutineScope(Job() + dispatchers.io),
      main = NsQueueCoroutineScope(),
    )
}
