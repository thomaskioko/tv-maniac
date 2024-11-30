package com.thomaskioko.tvmaniac.initializers

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
class AppInitializers(
  private val initializers: Set<AppInitializer>,
) {
  fun init() {
    for (initializer in initializers) {
      initializer.init()
    }
  }
}
