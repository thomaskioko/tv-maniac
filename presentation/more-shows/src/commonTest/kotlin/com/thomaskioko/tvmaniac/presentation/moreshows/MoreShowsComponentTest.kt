package com.thomaskioko.tvmaniac.presentation.moreshows

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.test.StandardTestDispatcher

class MoreShowsComponentTest {

  private val lifecycle = LifecycleRegistry()
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var component: MoreShowsComponent
}
