package com.thomaskioko.tvmaniac.discover.presenter.upnext

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.nav.scope.DiscoverChildScope
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.upnext.api.model.UpNextEpisode
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ChildPresenter(scope = DiscoverChildScope::class, parentScope = DiscoverRoot::class)
@Inject
public class DiscoverUpNextPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    observeUpNextInteractor: ObserveUpNextInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    public val state: StateFlow<DiscoverUpNextState> = observeUpNextInteractor.flow
        .map { result ->
            DiscoverUpNextState(
                nextEpisodes = result.episodes.map { it.toUiModel() }.toImmutableList(),
            )
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DiscoverUpNextState(),
        )

    public val stateValue: Value<DiscoverUpNextState> = state.asValue(coroutineScope)

    public fun dispatch(action: DiscoverUpNextAction) {
        when (action) {
            is DiscoverEpisodeLongPressed -> navigator.navigateTo(
                EpisodeSheetRoute(EpisodeSheetParam(episodeId = action.episodeId, source = ScreenSource.DISCOVER)),
            )
        }
    }
}

private fun UpNextEpisode.toUiModel(): NextEpisodeUiModel {
    return NextEpisodeUiModel(
        showId = showId,
        showName = showName,
        imageUrl = stillPath ?: showPoster,
        episodeId = episodeId,
        episodeTitle = episodeName ?: "",
        episodeNumberFormatted = "S${seasonNumber}E$episodeNumber",
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime?.let { "$it min" },
        overview = overview ?: "",
        isNew = false,
        rating = rating,
        voteCount = voteCount,
    )
}
