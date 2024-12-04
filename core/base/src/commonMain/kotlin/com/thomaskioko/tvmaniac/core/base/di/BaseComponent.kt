package com.thomaskioko.tvmaniac.core.base.di

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface BaseComponent {

  @Provides
  fun provideCoroutineDispatchers(): AppCoroutineDispatchers =
    AppCoroutineDispatchers(
      io = Dispatchers.Default,
      computation = Dispatchers.Default,
      main = Dispatchers.Main,
    )
}
