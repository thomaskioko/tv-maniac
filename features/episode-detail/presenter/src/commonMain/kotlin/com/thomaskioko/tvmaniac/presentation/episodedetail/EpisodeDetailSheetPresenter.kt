package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.model.ScreenSource
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
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AssistedInject
public class EpisodeDetailSheetPresenter(
    @Assisted private val episodeId: Long,
    @Assisted private val source: ScreenSource,
    componentContext: ComponentContext,
    observeEpisodeByIdInteractor: ObserveEpisodeByIdInteractor,
    private val navigator: EpisodeDetailNavigator,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val actionLoadingState = ObservableLoadingCounter()
    private var currentEpisode: EpisodeById? = null

    public val state: StateFlow<EpisodeDetailSheetState> = combine(
        observeEpisodeByIdInteractor.flow,
        uiMessageManager.message,
    ) { episode, message ->
        currentEpisode = episode
        episode?.toState(source)?.copy(message = message)
            ?: EpisodeDetailSheetState(isLoading = false, message = message)
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EpisodeDetailSheetState(),
    )

    public val stateValue: Value<EpisodeDetailSheetState> = state.asValue(coroutineScope)

    init {
        observeEpisodeByIdInteractor(episodeId)
    }

    public fun dispatch(action: EpisodeDetailSheetAction) {
        when (action) {
            is EpisodeDetailSheetAction.ToggleWatched -> toggleWatched()
            is EpisodeDetailSheetAction.OpenShow -> openShow()
            is EpisodeDetailSheetAction.OpenSeason -> openSeason()
            is EpisodeDetailSheetAction.Unfollow -> unfollowShow()
            is EpisodeDetailSheetAction.Dismiss -> navigator.dismiss()
            is EpisodeDetailSheetAction.MessageShown -> clearMessage(action.id)
        }
    }

    private fun toggleWatched() {
        val episode = currentEpisode ?: return
        coroutineScope.launch {
            if (episode.is_watched != 0L) {
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(
                        showTraktId = episode.show_trakt_id.id,
                        episodeId = episode.episode_id.id,
                    ),
                ).collectStatus(actionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            } else {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showTraktId = episode.show_trakt_id.id,
                        episodeId = episode.episode_id.id,
                        seasonNumber = episode.season_number,
                        episodeNumber = episode.episode_number,
                    ),
                ).collectStatus(actionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            }
            navigator.dismiss()
        }
    }

    private fun openShow() {
        val episode = currentEpisode ?: return
        navigator.showDetails(episode.show_trakt_id.id)
    }

    private fun openSeason() {
        val episode = currentEpisode ?: return
        navigator.showSeasonDetails(episode.show_trakt_id.id, episode.season_id.id, episode.season_number)
    }

    private fun unfollowShow() {
        val episode = currentEpisode ?: return
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(episode.show_trakt_id.id)
            navigator.dismiss()
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(episodeId: Long, source: ScreenSource): EpisodeDetailSheetPresenter
    }
}
