package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, EpisodeDetailSheetPresenter::class)
public class DefaultEpisodeDetailSheetPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val episodeId: Long,
    @Assisted private val source: ScreenSource,
    @Assisted private val navigateToShowDetails: (Long) -> Unit,
    @Assisted private val navigateToSeasonDetails: (Long, Long, Long) -> Unit,
    @Assisted private val dismissSheet: () -> Unit,
    private val observeEpisodeByIdInteractor: ObserveEpisodeByIdInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : EpisodeDetailSheetPresenter, ComponentContext by componentContext {

    private val uiMessageManager = UiMessageManager()
    private val actionLoadingState = ObservableLoadingCounter()
    private var currentEpisode: EpisodeById? = null

    override val state: StateFlow<EpisodeDetailSheetState> = combine(
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

    init {
        observeEpisodeByIdInteractor(episodeId)
    }

    override fun dispatch(action: EpisodeDetailSheetAction) {
        when (action) {
            is EpisodeDetailSheetAction.ToggleWatched -> toggleWatched()
            is EpisodeDetailSheetAction.OpenShow -> openShow()
            is EpisodeDetailSheetAction.OpenSeason -> openSeason()
            is EpisodeDetailSheetAction.Unfollow -> unfollowShow()
            is EpisodeDetailSheetAction.Dismiss -> dismissSheet()
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
            dismissSheet()
        }
    }

    private fun openShow() {
        val episode = currentEpisode ?: return
        navigateToShowDetails(episode.show_trakt_id.id)
        dismissSheet()
    }

    private fun openSeason() {
        val episode = currentEpisode ?: return
        navigateToSeasonDetails(episode.show_trakt_id.id, episode.season_id.id, episode.season_number)
        dismissSheet()
    }

    private fun unfollowShow() {
        val episode = currentEpisode ?: return
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(episode.show_trakt_id.id)
            dismissSheet()
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, EpisodeDetailSheetPresenter.Factory::class)
public class DefaultEpisodeDetailSheetPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        episodeId: Long,
        source: ScreenSource,
        navigateToShowDetails: (Long) -> Unit,
        navigateToSeasonDetails: (Long, Long, Long) -> Unit,
        dismissSheet: () -> Unit,
    ) -> EpisodeDetailSheetPresenter,
) : EpisodeDetailSheetPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        episodeId: Long,
        source: ScreenSource,
        navigateToShowDetails: (showTraktId: Long) -> Unit,
        navigateToSeasonDetails: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
        dismissSheet: () -> Unit,
    ): EpisodeDetailSheetPresenter = presenter(
        componentContext,
        episodeId,
        source,
        navigateToShowDetails,
        navigateToSeasonDetails,
        dismissSheet,
    )
}
