package com.thomaskioko.tvmaniac.seasondetails.presenter.di

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface SeasonDetailsPresenterFactory {
    fun create(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (id: Long) -> Unit,
    ): SeasonDetailsPresenter
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SeasonDetailsPresenterFactory::class)
class DefaultSeasonDetailsPresenterFactory(
    private val observableSeasonDetailsInteractor: ObservableSeasonDetailsInteractor,
    private val seasonDetailsInteractor: SeasonDetailsInteractor,
    private val logger: Logger,
) : SeasonDetailsPresenterFactory {
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
        observableSeasonDetailsInteractor = observableSeasonDetailsInteractor,
        seasonDetailsInteractor = seasonDetailsInteractor,
        logger = logger,
    )
}
