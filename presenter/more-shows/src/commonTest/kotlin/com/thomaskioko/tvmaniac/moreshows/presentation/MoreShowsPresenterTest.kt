package com.thomaskioko.tvmaniac.moreshows.presentation

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.test.StandardTestDispatcher

class MoreShowsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: DefaultMoreShowsPresenter
}
