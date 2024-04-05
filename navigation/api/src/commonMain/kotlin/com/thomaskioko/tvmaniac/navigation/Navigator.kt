package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import kotlinx.coroutines.flow.StateFlow

interface Navigator {
  val screenStackFlow: StateFlow<ChildStack<*, Screen>>
  val themeState: StateFlow<ThemeState>

  fun bringToFront(config: Config)

  fun shouldShowBottomNav(screen: Screen): Boolean
}
