package com.thomaskioko.tvmaniac.testing.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.testing.TestScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class)
class FakeSeasonDetailsPresenterFactory(
    private val observableSeasonDetailsInteractor: ObservableSeasonDetailsInteractor,
    private val seasonDetailsInteractor: SeasonDetailsInteractor,
    private val logger: Logger,
) : SeasonDetailsPresenter.Factory {
    override fun create(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (Long) -> Unit,
    ): SeasonDetailsPresenter = SeasonDetailsPresenter(
        componentContext = componentContext,
        param = param,
        onBack = onBack,
        onEpisodeClick = onNavigateToEpisodeDetails,
        observableSeasonDetailsInteractor = observableSeasonDetailsInteractor,
        seasonDetailsInteractor = seasonDetailsInteractor,
        logger = logger,
    )
}
