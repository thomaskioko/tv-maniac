package com.thomaskioko.tvmaniac.presentation.episodedetail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveEpisodeByIdInteractor
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.ObserveEpisodeRewatchesInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.WatchAgainInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@NavDestination(
    route = EpisodeSheetRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class EpisodeSheetPresenter internal constructor(
    @Assisted private val param: EpisodeSheetParam,
    componentContext: ComponentContext,
    observeEpisodeByIdInteractor: ObserveEpisodeByIdInteractor,
    observeRatingInteractor: ObserveRatingInteractor,
    private val observeEpisodeRewatchesInteractor: ObserveEpisodeRewatchesInteractor,
    private val navigator: Navigator,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val shouldPromptForRatingInteractor: ShouldPromptForRatingInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val watchAgainInteractor: WatchAgainInteractor,
    private val datastoreRepository: DatastoreRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val localizer: Localizer,
    private val logger: Logger,
    private val appScopeLauncher: AppScopeLauncher,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val actionLoadingState = ObservableLoadingCounter()
    private val showRemoveWatchConfirmation = MutableStateFlow(false)
    private var currentEpisode: EpisodeById? = null

    public val state: StateFlow<EpisodeDetailSheetState> = combine(
        observeEpisodeByIdInteractor.flow,
        uiMessageManager.message,
        actionLoadingState.observable,
        observeRatingInteractor.flow,
        observeEpisodeRewatchesInteractor.flow,
        datastoreRepository.observeMultiplePlaysEnabled(),
        showRemoveWatchConfirmation,
    ) { episode, message, isTogglingWatched, userRating, rewatches, multiplePlaysEnabled, confirmRemoval ->
        currentEpisode = episode
        val current = episode?.toState(
            source = param.source,
            localizer = localizer,
            multiplePlaysEnabled = multiplePlaysEnabled,
            rewatches = rewatches,
        ) ?: EpisodeDetailSheetState(isLoading = true)
        current.copy(
            message = message,
            isTogglingWatched = isTogglingWatched,
            userRating = userRating,
            removeWatchConfirmation = if (confirmRemoval) {
                removeWatchConfirmation(current.episodeTitle, localizer)
            } else {
                null
            },
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EpisodeDetailSheetState(),
    )

    public val stateValue: Value<EpisodeDetailSheetState> = state.asValue(coroutineScope)

    init {
        observeEpisodeByIdInteractor(param.episodeId)
        observeRatingInteractor(ObserveRatingInteractor.Param(RatingEntityType.EPISODE, param.episodeId))
        observeEpisodeRewatchesInteractor(param.episodeId)
    }

    public fun dispatch(action: EpisodeSheetAction) {
        when (action) {
            is EpisodeSheetAction.MarkWatched -> markWatched()
            is EpisodeSheetAction.MarkUnwatched -> showRemoveWatchConfirmation.value = true
            is EpisodeSheetAction.RemoveWatchDismissed -> showRemoveWatchConfirmation.value = false
            is EpisodeSheetAction.RemoveWatchConfirmed -> {
                showRemoveWatchConfirmation.value = false
                markUnwatched()
            }
            is EpisodeSheetAction.OpenShow -> openShow()
            is EpisodeSheetAction.OpenSeason -> openSeason()
            is EpisodeSheetAction.Unfollow -> unfollowShow()
            is EpisodeSheetAction.Dismiss -> navigator.dismissOverlay()
            is EpisodeSheetAction.MessageShown -> clearMessage(action.id)
            is EpisodeSheetAction.RatingClicked -> navigator.navigateTo(
                RatingSheetRoute(
                    RatingSheetParam(
                        ratingType = RatingEntityType.EPISODE,
                        id = param.episodeId,
                    ),
                ),
            )
        }
    }

    private fun markWatched() {
        val episode = currentEpisode ?: return
        if (state.value.isTogglingWatched) return
        val wasWatched = episode.is_watched != 0L
        appScopeLauncher.launch(TAG) {
            val markStatus = if (wasWatched) {
                watchAgainInteractor(
                    WatchAgainInteractor.Param(
                        showId = episode.show_id.id,
                        episodeId = episode.episode_id.id,
                        seasonNumber = episode.season_number,
                        episodeNumber = episode.episode_number,
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
            val shouldRate = !wasWatched && shouldPromptForRatingInteractor(
                ShouldPromptForRatingInteractor.Param(
                    showId = episode.show_id.id,
                    episodeId = episode.episode_id.id,
                ),
            )
            coroutineScope.launch {
                navigator.dismissOverlay()
                if (shouldRate) {
                    navigator.navigateTo(
                        RatingSheetRoute(
                            RatingSheetParam(
                                ratingType = RatingEntityType.EPISODE,
                                id = episode.episode_id.id,
                            ),
                        ),
                    )
                }
            }
        }
    }

    private fun markUnwatched() {
        val episode = currentEpisode ?: return
        if (state.value.isTogglingWatched) return
        appScopeLauncher.launch(TAG) {
            markEpisodeUnwatchedInteractor(
                MarkEpisodeUnwatchedParams(
                    showId = episode.show_id.id,
                    episodeId = episode.episode_id.id,
                ),
            ).collectStatus(actionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
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
