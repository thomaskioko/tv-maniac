package com.thomaskioko.tvmaniac.navigation.di

import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter
import me.tatarka.inject.annotations.Provides

interface NavigatorComponent {

  @ActivityScope @Provides fun provideNavigator(bind: RootNavigationPresenter): Navigator = bind
}
