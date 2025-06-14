package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.settings.presenter.di.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository

/**
 * A fake implementation of [SettingsPresenterFactory] for testing.
 * This simplifies the creation of [SettingsPresenter] in tests by handling all the dependencies internally.
 */
class FakeSettingsPresenterFactory : SettingsPresenterFactory {
    private val datastoreRepository = FakeDatastoreRepository()
    private val traktAuthRepository = FakeTraktAuthRepository()

    override fun create(
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ): SettingsPresenter = SettingsPresenter(
        componentContext = componentContext,
        launchWebView = launchWebView,
        datastoreRepository = datastoreRepository,
        traktAuthRepository = traktAuthRepository,
    )
}
