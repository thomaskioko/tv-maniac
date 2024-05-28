package com.thomaskioko.tvmaniac.inject

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.di.NavigatorComponent
import com.thomaskioko.tvmaniac.traktauth.implementation.DefaultTraktAuthManager
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
) : NavigatorComponent {
  abstract val traktAuthManager: DefaultTraktAuthManager
  abstract val navigator: Navigator

  companion object
}
