package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.navigation.di.NavigatorComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(ActivityScope::class)
abstract class IosViewPresenterComponent(
  @get:Provides val componentContext: ComponentContext,
  @Component val applicationComponent: ApplicationComponent,
) : NavigatorComponent {
  abstract val rootPresenter: RootPresenter

  companion object
}
