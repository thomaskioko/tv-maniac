package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsAction
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsContent
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, ShowDetailsPresenter.Factory::class)
public class FakeShowDetailsPresenterFactory : ShowDetailsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        param: ShowDetailsParam,
        onBack: () -> Unit,
        onNavigateToShow: (Long) -> Unit,
        onNavigateToSeason: (ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (Long) -> Unit,
        onShowFollowed: () -> Unit,
    ): ShowDetailsPresenter = FakeShowDetailsPresenter()
}

internal class FakeShowDetailsPresenter : ShowDetailsPresenter {
    override val state: StateFlow<ShowDetailsContent> = MutableStateFlow(ShowDetailsContent.Empty)

    override fun dispatch(action: ShowDetailsAction) {
        // No-op for testing
    }
}
