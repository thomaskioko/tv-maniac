package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.DefaultRootPresenter
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import me.tatarka.inject.annotations.Provides

interface NavigatorComponent {

  @ActivityScope
  @Provides
  fun provideRootComponent(bind: DefaultRootPresenter): RootPresenter = bind
}
