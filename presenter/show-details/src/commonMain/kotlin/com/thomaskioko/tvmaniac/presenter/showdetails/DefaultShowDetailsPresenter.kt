package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
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
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor.Param
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
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
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, boundType = ShowDetailsPresenter::class)
public class DefaultShowDetailsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: ShowDetailsParam,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShow: (id: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    @Assisted private val onNavigateToTrailer: (id: Long) -> Unit,
    @Assisted private val onShowFollowed: () -> Unit,
    private val followedShowsRepository: FollowedShowsRepository,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val syncTraktCalendarInteractor: SyncTraktCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val logger: Logger,
    dispatchers: AppCoroutineDispatchers,
) : ShowDetailsPresenter, ComponentContext by componentContext {

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

    override val state: StateFlow<ShowDetailsContent> = combine(
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

    override fun dispatch(action: ShowDetailsAction) {
        when (action) {
            is SeasonClicked -> {
                _state.update {
                    it.copy(selectedSeasonIndex = action.params.selectedSeasonIndex)
                }
                onNavigateToSeason(action.params)
            }

            is DetailShowClicked -> onNavigateToShow(action.id)
            is WatchTrailerClicked -> onNavigateToTrailer(action.id)
            is FollowShowClicked -> {
                coroutineScope.launch {
                    if (action.isInLibrary) {
                        followedShowsRepository.removeFollowedShow(showTraktId)
                        notificationManager.cancelNotificationsForShow(showTraktId)
                    } else {
                        followedShowsRepository.addFollowedShow(showTraktId)
                        syncShowContent(isUserInitiated = true, loadingState = episodeActionLoadingState)

                        syncTraktCalendarInteractor(SyncTraktCalendarInteractor.Params(forceRefresh = true))
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager)

                        scheduleEpisodeNotificationsInteractor(ScheduleEpisodeNotificationsInteractor.Params())
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager)

                        onShowFollowed()
                    }
                }
            }

            DetailBackClicked -> onBack()
            ReloadShowDetails -> refreshShowContent(isUserInitiated = true)
            is ShowDetailsMessageShown -> coroutineScope.launch { uiMessageManager.clearMessage(action.id) }
            DismissShowsListSheet -> coroutineScope.launch { _state.update { it.copy(showListSheet = false) } }
            ShowShowsListSheet -> coroutineScope.launch { _state.update { it.copy(showListSheet = true) } }
            CreateCustomList -> {
                // TODO:: Add implementation
            }

            is MarkEpisodeWatched -> {
                coroutineScope.launch {
                    markEpisodeWatchedInteractor(
                        MarkEpisodeWatchedParams(
                            showTraktId = action.showTraktId,
                            episodeId = action.episodeId,
                            seasonNumber = action.seasonNumber,
                            episodeNumber = action.episodeNumber,
                            markPreviousEpisodes = false,
                        ),
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager)
                }
            }

            is MarkEpisodeUnwatched -> {
                coroutineScope.launch {
                    markEpisodeUnwatchedInteractor(
                        MarkEpisodeUnwatchedParams(
                            showTraktId = action.showTraktId,
                            episodeId = action.episodeId,
                        ),
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager)
                }
            }
        }
    }

    private fun observeShowDetails(forceReload: Boolean = false, isUserInitiated: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showTraktId, forceReload))
                .collectStatus(showDetailsLoadingState, logger, uiMessageManager)

            if (traktAuthRepository.isLoggedIn()) {
                syncShowContent(
                    forceRefresh = forceReload,
                    isUserInitiated = isUserInitiated,
                    loadingState = showDetailsLoadingState,
                )
            }
        }

        coroutineScope.launch {
            similarShowsInteractor(SimilarShowsInteractor.Param(showTraktId, forceReload))
                .collectStatus(similarShowsLoadingState, logger, uiMessageManager)
        }

        coroutineScope.launch {
            watchProvidersInteractor(WatchProvidersInteractor.Param(showTraktId, forceReload))
                .collectStatus(watchProvidersLoadingState, logger, uiMessageManager)
        }
    }

    private fun refreshShowContent(isUserInitiated: Boolean) {
        observeShowDetails(forceReload = true, isUserInitiated = isUserInitiated)
    }

    private suspend fun syncShowContent(
        forceRefresh: Boolean = false,
        isUserInitiated: Boolean,
        loadingState: ObservableLoadingCounter,
    ) {
        showContentSyncInteractor(
            params = Param(
                traktId = showTraktId,
                forceRefresh = forceRefresh,
                isUserInitiated = isUserInitiated,
            ),
        ).collectStatus(loadingState, logger, uiMessageManager)
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
                .collect { refreshShowContent(isUserInitiated = false) }
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, ShowDetailsPresenter.Factory::class)
public class DefaultShowDetailsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        param: ShowDetailsParam,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
        onShowFollowed: () -> Unit,
    ) -> ShowDetailsPresenter,
) : ShowDetailsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        param: ShowDetailsParam,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
        onShowFollowed: () -> Unit,
    ): ShowDetailsPresenter = presenter(
        componentContext,
        param,
        onBack,
        onNavigateToShow,
        onNavigateToSeason,
        onNavigateToTrailer,
        onShowFollowed,
    )
}
