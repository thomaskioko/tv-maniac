package com.thomaskioko.tvmaniac.shared

import me.tatarka.inject.annotations.Component
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(AppScope::class)
abstract class ApplicationComponent : SharedComponent() {
  companion object
}
