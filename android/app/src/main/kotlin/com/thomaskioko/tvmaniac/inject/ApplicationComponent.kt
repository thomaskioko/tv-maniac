package com.thomaskioko.tvmaniac.inject

import android.app.Application
import com.thomaskioko.tvmaniac.initializers.AppInitializers
import com.thomaskioko.tvmaniac.shared.SharedComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(AppScope::class)
abstract class ApplicationComponent(
  @get:Provides val application: Application,
) : SharedComponent() {
  abstract val initializers: AppInitializers

  companion object
}
