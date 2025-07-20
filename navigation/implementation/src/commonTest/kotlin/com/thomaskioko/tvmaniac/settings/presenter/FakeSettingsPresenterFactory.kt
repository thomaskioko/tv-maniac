package com.thomaskioko.tvmaniac.settings.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository

class FakeSettingsPresenterFactory : SettingsPresenter.Factory {

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
