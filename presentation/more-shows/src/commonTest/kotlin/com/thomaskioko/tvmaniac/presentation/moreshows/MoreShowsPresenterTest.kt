package com.thomaskioko.tvmaniac.presentation.moreshows

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

@Suppress("TestFunctionName")
@OptIn(ExperimentalCoroutinesApi::class)
class MoreShowsPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: MoreShowsPresenter
}
