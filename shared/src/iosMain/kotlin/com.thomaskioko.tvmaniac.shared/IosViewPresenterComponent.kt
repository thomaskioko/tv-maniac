package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthManagerComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
abstract class IosViewPresenterComponent(
  @get:Provides val componentContext: ComponentContext,
  @Component val applicationComponent: ApplicationComponent,
) : TraktAuthManagerComponent {
  abstract val presenter: RootNavigationPresenter

  companion object
}
