package com.thomaskioko.tvmaniac.presenter.home.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.di.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository

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
