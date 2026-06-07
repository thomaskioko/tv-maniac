package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
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
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowMetadataInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val syncTraktCalendarInteractor: SyncTraktCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observableShowMetadataInteractor: ObservableShowMetadataInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    private val accountManager: AccountManager,
    private val mapper: ShowDetailsMapper,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    @Suppress("UNUSED_PARAMETER") dispatchers: AppCoroutineDispatchers,
) : ComponentContext by componentContext {

    private val showId: Long = param.showId
    private val showDetailsLoadingState = ObservableLoadingCounter()
    private val similarShowsLoadingState = ObservableLoadingCounter()
    private val episodeActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ShowDetailsContent.Empty)

    public val state: StateFlow<ShowDetailsContent> = _state.asStateFlow()
    public val stateValue: Value<ShowDetailsContent> = state.asValue(coroutineScope)

    init {
        observableShowDetailsInteractor(showId)
        observableShowMetadataInteractor(showId)
        observeShowWatchProgressInteractor(showId)

        observableShowDetailsInteractor.flow
            .onEach { details ->
                _state.update { it.copy(showDetails = mapper.applyShowDetails(it.showDetails, details)) }
            }
            .launchIn(coroutineScope)

        observableShowMetadataInteractor.flow
            .onEach { metadata ->
                _state.update {
                    it.copy(
                        showDetails = mapper.applyMetadata(it.showDetails, metadata),
                        continueTrackingEpisodes = mapper.mapContinueTrackingEpisodes(metadata.continueTrackingEpisodes, showId),
                        continueTrackingScrollIndex = metadata.continueTrackingScrollIndex,
                    )
                }
            }
            .launchIn(coroutineScope)

        observeShowWatchProgressInteractor.flow
            .onEach { progress ->
                _state.update { it.copy(showDetails = mapper.applyWatchProgress(it.showDetails, progress)) }
            }
            .launchIn(coroutineScope)

        showDetailsLoadingState.observable
            .onEach { refreshing -> _state.update { it.copy(showDetailsRefreshing = refreshing) } }
            .launchIn(coroutineScope)

        similarShowsLoadingState.observable
            .onEach { refreshing -> _state.update { it.copy(similarShowsRefreshing = refreshing) } }
            .launchIn(coroutineScope)

        uiMessageManager.message
            .onEach { message -> _state.update { it.copy(message = message) } }
            .launchIn(coroutineScope)

        observeShowDetails(forceReload = param.forceRefresh)
        observeAuthState()
    }

    public fun dispatch(action: ShowDetailsAction) {
        when (action) {
            is SeasonClicked -> {
                _state.update {
                    it.copy(selectedSeasonIndex = action.params.selectedSeasonIndex)
                }
                navigator.navigateTo(
                    SeasonDetailsRoute(
                        SeasonDetailsUiParam(
                            showId = action.params.showId,
                            seasonId = action.params.seasonId,
                            seasonNumber = action.params.seasonNumber,
                        ),
                    ),
                )
            }

            is DetailShowClicked -> navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is WatchTrailerClicked -> navigator.navigateTo(TrailersRoute(action.id))
            is FollowShowClicked -> {
                coroutineScope.launch {
                    if (action.isInLibrary) {
                        followedShowsRepository.removeFollowedShow(showId)
                        notificationManager.cancelNotificationsForShow(showId)
                    } else {
                        followShowInteractor(FollowShowInteractor.Param(showId = showId))
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
            OpenShowList -> navigator.navigateTo(ShowListRoute(ShowListParam(showId = showId)))

            is MarkEpisodeWatched -> launchEpisodeMark(action.episodeId) {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showId = action.showId,
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
                        showId = action.showId,
                        episodeId = action.episodeId,
                    ),
                ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            }
        }
    }

    private fun observeShowDetails(forceReload: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showId, forceReload))
                .collectStatus(showDetailsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }

        coroutineScope.launch {
            similarShowsInteractor(SimilarShowsInteractor.Param(showId, forceReload))
                .collectStatus(similarShowsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun refreshShowContent() {
        observeShowDetails(forceReload = true)
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .drop(1)
                .distinctUntilChanged()
                .filter { it }
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
