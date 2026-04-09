package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsAction
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsModel
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, SeasonDetailsPresenter.Factory::class)
public class FakeSeasonDetailsPresenterFactory : SeasonDetailsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (Long) -> Unit,
    ): SeasonDetailsPresenter = FakeSeasonDetailsPresenter()
}

private val initialSeasonDetailsModel = SeasonDetailsModel(
    episodeCount = 0,
    seasonImages = persistentListOf(),
    seasonId = 0,
    seasonName = "",
    seasonOverview = "",
    imageUrl = null,
    episodeDetailsList = persistentListOf(),
    seasonCast = persistentListOf(),
)

internal class FakeSeasonDetailsPresenter : SeasonDetailsPresenter {
    override val state: StateFlow<SeasonDetailsModel> = MutableStateFlow(initialSeasonDetailsModel)
    override val stateValue: Value<SeasonDetailsModel> = MutableValue(initialSeasonDetailsModel)

    override fun dispatch(action: SeasonDetailsAction) {
        // No-op for testing
    }
}
