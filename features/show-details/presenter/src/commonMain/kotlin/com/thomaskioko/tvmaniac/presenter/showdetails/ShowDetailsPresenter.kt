package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncTraktCalendarInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.SyncShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@NavDestination(
    route = ShowDetailsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class ShowDetailsPresenter(
    componentContext: ComponentContext,
    @Assisted private val param: ShowDetailsParam,
    private val navigator: Navigator,
    private val notificationRationale: NotificationRationale,
    private val followedShowsRepository: FollowedShowsRepository,
    private val followShowInteractor: FollowShowInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val syncShowMetadataInteractor: SyncShowMetadataInteractor,
    private val syncTraktCalendarInteractor: SyncTraktCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    dispatchers: AppCoroutineDispatchers,
) : ComponentContext by componentContext {

    private val showTraktId: Long = param.id
    private val showDetailsLoadingState = ObservableLoadingCounter()
    private val similarShowsLoadingState = ObservableLoadingCounter()
    private val watchProvidersLoadingState = ObservableLoadingCounter()
    private val episodeActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ShowDetailsContent.Empty)

    init {
        observableShowDetailsInteractor(showTraktId)
        observeShowWatchProgressInteractor(showTraktId)
        observeShowDetails(forceReload = param.forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsContent> = combine(
        showDetailsLoadingState.observable,
        similarShowsLoadingState.observable,
        watchProvidersLoadingState.observable,
        observableShowDetailsInteractor.flow,
        observeShowWatchProgressInteractor.flow,
        uiMessageManager.message,
        _state,
    ) { showDetailsUpdating, similarShowsUpdating, watchProvidersUpdating,
        showDetails, watchProgress, message, currentState,
        ->
        currentState.copy(
            showDetails = showDetails.toShowDetails(
                localizer = localizer,
                watchedEpisodesCount = watchProgress.watchedCount,
                totalEpisodesCount = watchProgress.totalCount,
                watchProgress = watchProgress.progressPercentage,
            ),
            showDetailsRefreshing = showDetailsUpdating,
            similarShowsRefreshing = similarShowsUpdating,
            watchProvidersRefreshing = watchProvidersUpdating,
            continueTrackingEpisodes = mapContinueTrackingEpisodes(showDetails.continueTrackingEpisodes, showTraktId),
            continueTrackingScrollIndex = showDetails.continueTrackingScrollIndex,
            message = message,
        )
    }
        .flowOn(dispatchers.computation)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = _state.value,
        )

    public val stateValue: Value<ShowDetailsContent> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsAction) {
        when (action) {
            is SeasonClicked -> {
                _state.update {
                    it.copy(selectedSeasonIndex = action.params.selectedSeasonIndex)
                }
                navigator.navigateTo(
                    SeasonDetailsRoute(
                        SeasonDetailsUiParam(
                            showTraktId = action.params.showTraktId,
                            seasonId = action.params.seasonId,
                            seasonNumber = action.params.seasonNumber,
                        ),
                    ),
                )
            }

            is DetailShowClicked -> navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(id = action.id)))
            is WatchTrailerClicked -> navigator.navigateTo(TrailersRoute(action.id))
            is FollowShowClicked -> {
                coroutineScope.launch {
                    if (action.isInLibrary) {
                        followedShowsRepository.removeFollowedShow(showTraktId)
                        notificationManager.cancelNotificationsForShow(showTraktId)
                    } else {
                        followShowInteractor(FollowShowInteractor.Param(traktId = showTraktId))
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        syncTraktCalendarInteractor(SyncTraktCalendarInteractor.Params(forceRefresh = true))
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        scheduleEpisodeNotificationsInteractor(ScheduleEpisodeNotificationsInteractor.Params())
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        notificationRationale.showIfNeeded()
                    }
                }
            }

            DetailBackClicked -> navigator.navigateBack()
            ReloadShowDetails -> refreshShowContent()
            is ShowDetailsMessageShown -> coroutineScope.launch { uiMessageManager.clearMessage(action.id) }
            OpenShowList -> navigator.navigateTo(ShowListRoute(ShowListParam(showId = showTraktId)))

            is MarkEpisodeWatched -> launchEpisodeMark(action.episodeId) {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showTraktId = action.showTraktId,
                        episodeId = action.episodeId,
                        seasonNumber = action.seasonNumber,
                        episodeNumber = action.episodeNumber,
                        markPreviousEpisodes = false,
                    ),
                ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            }

            is MarkEpisodeUnwatched -> launchEpisodeMark(action.episodeId) {
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(
                        showTraktId = action.showTraktId,
                        episodeId = action.episodeId,
                    ),
                ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            }
        }
    }

    private fun observeShowDetails(forceReload: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showTraktId, forceReload))
                .collectStatus(showDetailsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

            syncShowContent(
                forceRefresh = forceReload,
                loadingState = showDetailsLoadingState,
            )
        }

        coroutineScope.launch {
            similarShowsInteractor(SimilarShowsInteractor.Param(showTraktId, forceReload))
                .collectStatus(similarShowsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }

        coroutineScope.launch {
            watchProvidersInteractor(WatchProvidersInteractor.Param(showTraktId, forceReload))
                .collectStatus(watchProvidersLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun refreshShowContent() {
        observeShowDetails(forceReload = true)
    }

    private suspend fun syncShowContent(
        forceRefresh: Boolean = false,
        loadingState: ObservableLoadingCounter,
    ) {
        syncShowMetadataInteractor(
            params = SyncShowMetadataInteractor.Param(
                traktId = showTraktId,
                forceRefresh = forceRefresh,
            ),
        ).collectStatus(loadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .drop(1)
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { refreshShowContent() }
        }
    }

    private fun launchEpisodeMark(episodeId: Long, block: suspend () -> Unit) {
        if (episodeId in _state.value.updatingEpisodeIds) return
        _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds + episodeId).toPersistentSet()) }
        coroutineScope.launch {
            val marker = TimeSource.Monotonic.markNow()
            try {
                block()
            } finally {
                val elapsed = marker.elapsedNow()
                if (elapsed < INDICATOR_FLOOR) {
                    delay(INDICATOR_FLOOR - elapsed)
                }
                _state.update { it.copy(updatingEpisodeIds = (it.updatingEpisodeIds - episodeId).toPersistentSet()) }
            }
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ShowDetailsParam): ShowDetailsPresenter
    }

    private companion object {
        private val INDICATOR_FLOOR: Duration = 150.milliseconds
    }
}
