package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.DefaultRootComponent
import com.thomaskioko.tvmaniac.navigation.RootComponent
import me.tatarka.inject.annotations.Provides

interface NavigatorComponent {

  @ActivityScope
  @Provides
  fun provideRootComponent(bind: DefaultRootComponent): RootComponent = bind
}
