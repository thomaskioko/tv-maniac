package com.thomaskioko.tvmaniac.testing.di.fakes

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetAction
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetPresenter
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeDetailSheetState
import com.thomaskioko.tvmaniac.presentation.episodedetail.ScreenSource
import com.thomaskioko.tvmaniac.testing.di.TestScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(TestScope::class)
@ContributesBinding(TestScope::class, EpisodeDetailSheetPresenter.Factory::class)
public class FakeEpisodeDetailSheetPresenterFactory : EpisodeDetailSheetPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        episodeId: Long,
        source: ScreenSource,
        navigateToShowDetails: (showTraktId: Long) -> Unit,
        navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        dismissSheet: () -> Unit,
    ): EpisodeDetailSheetPresenter = FakeEpisodeDetailSheetPresenter()
}

internal class FakeEpisodeDetailSheetPresenter : EpisodeDetailSheetPresenter {
    override val state: StateFlow<EpisodeDetailSheetState> = MutableStateFlow(EpisodeDetailSheetState())

    override fun dispatch(action: EpisodeDetailSheetAction) {
    }
}
