package com.thomaskioko.tvmaniac.settings.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface SettingsPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        launchWebView: () -> Unit,
    ): SettingsPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SettingsPresenterFactory::class)
class DefaultSettingsPresenterFactory(
    private val datastoreRepository: DatastoreRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : SettingsPresenterFactory {
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
