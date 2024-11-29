package com.thomaskioko.tvmaniac.inject

import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.navigation.di.NavigatorComponent
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.implementation.TraktAuthAndroidComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(ActivityScope::class)
@Component
abstract class ActivityComponent(
  @get:Provides val activity: ComponentActivity,
  @get:Provides val componentContext: ComponentContext = activity.defaultComponentContext(),
  @Component
  val applicationComponent: ApplicationComponent =
    ApplicationComponent.create(activity.application),
) : NavigatorComponent, TraktAuthAndroidComponent {
  abstract val traktAuthManager: TraktAuthManager
  abstract val rootPresenter: RootPresenter

  companion object
}
