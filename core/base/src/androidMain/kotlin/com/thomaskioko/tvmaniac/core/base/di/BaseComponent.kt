package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.annotations.ApplicationScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.tatarka.inject.annotations.Provides

actual interface BaseComponent {

  @ApplicationScope
  @Provides
  fun provideCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.IO,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )

  @ApplicationScope
  @Provides
  fun provideCoroutineScope(dispatchers: AppCoroutineDispatchers): AppCoroutineScope =
    AppCoroutineScope(
      default = CoroutineScope(Job() + dispatchers.computation),
      io = CoroutineScope(Job() + dispatchers.io),
      main = CoroutineScope(Job() + dispatchers.main),
    )
}
