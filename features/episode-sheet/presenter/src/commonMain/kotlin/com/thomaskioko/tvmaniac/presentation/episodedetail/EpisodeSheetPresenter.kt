package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveEpisodeByIdInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@NavDestination(
    route = EpisodeSheetRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class EpisodeSheetPresenter(
    @Assisted private val param: EpisodeSheetParam,
    componentContext: ComponentContext,
    observeEpisodeByIdInteractor: ObserveEpisodeByIdInteractor,
    private val navigator: Navigator,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val localizer: Localizer,
    private val logger: Logger,
    private val appScopeLauncher: AppScopeLauncher,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val actionLoadingState = ObservableLoadingCounter()
    private var currentEpisode: EpisodeById? = null

    public val state: StateFlow<EpisodeDetailSheetState> = combine(
        observeEpisodeByIdInteractor.flow,
        uiMessageManager.message,
        actionLoadingState.observable,
    ) { episode, message, isTogglingWatched ->
        currentEpisode = episode
        episode?.toState(param.source, localizer)?.copy(message = message, isTogglingWatched = isTogglingWatched)
            ?: EpisodeDetailSheetState(isLoading = true, message = message, isTogglingWatched = isTogglingWatched)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EpisodeDetailSheetState(),
    )

    public val stateValue: Value<EpisodeDetailSheetState> = state.asValue(coroutineScope)

    init {
        observeEpisodeByIdInteractor(param.episodeId)
    }

    public fun dispatch(action: EpisodeSheetAction) {
        when (action) {
            is EpisodeSheetAction.ToggleWatched -> toggleWatched()
            is EpisodeSheetAction.OpenShow -> openShow()
            is EpisodeSheetAction.OpenSeason -> openSeason()
            is EpisodeSheetAction.Unfollow -> unfollowShow()
            is EpisodeSheetAction.Dismiss -> navigator.dismissOverlay()
            is EpisodeSheetAction.MessageShown -> clearMessage(action.id)
        }
    }

    private fun toggleWatched() {
        val episode = currentEpisode ?: return
        if (state.value.isTogglingWatched) return
        appScopeLauncher.launch(TAG) {
            val markStatus = if (episode.is_watched != 0L) {
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(
                        showId = episode.show_id.id,
                        episodeId = episode.episode_id.id,
                    ),
                )
            } else {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showId = episode.show_id.id,
                        episodeId = episode.episode_id.id,
                        seasonNumber = episode.season_number,
                        episodeNumber = episode.episode_number,
                    ),
                )
            }
            markStatus.collectStatus(actionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            coroutineScope.launch { navigator.dismissOverlay() }
        }
    }

    private fun openShow() {
        val episode = currentEpisode ?: return
        navigator.dismissOverlay()
        navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(showId = episode.show_id.id)))
    }

    private fun openSeason() {
        val episode = currentEpisode ?: return
        navigator.dismissOverlay()
        navigator.navigateTo(
            SeasonDetailsRoute(
                SeasonDetailsUiParam(
                    showId = episode.show_id.id,
                    seasonId = episode.season_id.id,
                    seasonNumber = episode.season_number,
                ),
            ),
        )
    }

    private fun unfollowShow() {
        val episode = currentEpisode ?: return
        navigator.dismissOverlay()
        appScopeLauncher.launch(TAG) {
            unfollowShowInteractor.executeSync(episode.show_id.id)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: EpisodeSheetParam): EpisodeSheetPresenter
    }

    private companion object {
        private const val TAG = "EpisodeSheetPresenter"
    }
}
