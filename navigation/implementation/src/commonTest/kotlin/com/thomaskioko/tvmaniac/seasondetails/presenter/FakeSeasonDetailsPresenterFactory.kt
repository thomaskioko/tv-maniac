package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.cast.testing.FakeCastRepository
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.seasondetails.presenter.di.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import kotlinx.coroutines.test.StandardTestDispatcher

/**
 * A fake implementation of [SeasonDetailsPresenterFactory] for testing.
 * This simplifies the creation of [SeasonDetailsPresenter] in tests by handling all the dependencies internally.
 */
class FakeSeasonDetailsPresenterFactory : SeasonDetailsPresenterFactory {
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val castRepository = FakeCastRepository()
    private val logger = FakeLogger()
    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    override fun create(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (id: Long) -> Unit,
    ): SeasonDetailsPresenter = SeasonDetailsPresenter(
        componentContext = componentContext,
        param = param,
        onBack = onBack,
        onEpisodeClick = onNavigateToEpisodeDetails,
        observableSeasonDetailsInteractor = ObservableSeasonDetailsInteractor(
            seasonDetailsRepository = seasonDetailsRepository,
            castRepository = castRepository,
        ),
        seasonDetailsInteractor = SeasonDetailsInteractor(
            seasonDetailsRepository = seasonDetailsRepository,
            dispatchers = coroutineDispatcher,
        ),
        logger = logger,
    )
}
