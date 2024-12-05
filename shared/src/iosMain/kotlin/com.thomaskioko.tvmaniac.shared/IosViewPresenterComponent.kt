package com.thomaskioko.tvmaniac.shared

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesSubcomponent(ActivityScope::class)
@SingleIn(ActivityScope::class)
interface IosViewPresenterComponent {
  val rootPresenter: RootPresenter

  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun createComponent(
      componentContext: ComponentContext,
    ): IosViewPresenterComponent
  }
}
