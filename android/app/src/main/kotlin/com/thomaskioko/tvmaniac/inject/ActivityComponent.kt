package com.thomaskioko.tvmaniac.inject

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter
import com.thomaskioko.tvmaniac.navigation.RootScreen
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthManagerComponent
import com.thomaskioko.tvmaniac.util.scope.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@ActivityScope
@Component
abstract class ActivityComponent(
  @get:Provides val activity: ComponentActivity,
  @get:Provides val componentContext: ComponentContext = activity.defaultComponentContext(),
  @Component
  val applicationComponent: ApplicationComponent =
    ApplicationComponent.create(activity.application),
) : TraktAuthManagerComponent {
  abstract val traktAuthManager: TraktAuthManager
  abstract val presenter: RootNavigationPresenter
  abstract val rootScreen: RootScreen

  companion object
}
