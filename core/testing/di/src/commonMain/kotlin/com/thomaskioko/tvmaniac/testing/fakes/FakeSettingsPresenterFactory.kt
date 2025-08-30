package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.testing.TestScope
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthRepository
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
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
