package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
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
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowContentSyncInteractor.Param
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.CreateTraktListInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveTraktListsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.SyncTraktListsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.collections.immutable.toImmutableList
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

@AssistedInject
public class ShowDetailsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: ShowDetailsParam,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShow: (id: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    @Assisted private val onNavigateToTrailer: (id: Long) -> Unit,
    @Assisted private val onShowFollowed: () -> Unit,
    private val followedShowsRepository: FollowedShowsRepository,
    private val followShowInteractor: FollowShowInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val showContentSyncInteractor: ShowContentSyncInteractor,
    private val syncTraktCalendarInteractor: SyncTraktCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    private val createTraktListInteractor: CreateTraktListInteractor,
    private val toggleShowInListInteractor: ToggleShowInListInteractor,
    private val syncTraktListsInteractor: SyncTraktListsInteractor,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    observeTraktListsInteractor: ObserveTraktListsInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val traktAuthManager: TraktAuthManager,
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
        observeTraktListsInteractor(showTraktId)
        observeShowDetails(forceReload = param.forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsContent> = combine(
        showDetailsLoadingState.observable,
        similarShowsLoadingState.observable,
        watchProvidersLoadingState.observable,
        observableShowDetailsInteractor.flow,
        observeShowWatchProgressInteractor.flow,
        observeTraktListsInteractor.flow,
        uiMessageManager.message,
        _state,
    ) { showDetailsUpdating, similarShowsUpdating, watchProvidersUpdating,
        showDetails, watchProgress, traktLists, message, currentState,
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
            traktLists = traktLists.map { list ->
                com.thomaskioko.tvmaniac.presenter.showdetails.model.TraktListModel(
                    id = list.id,
                    slug = list.slug,
                    name = list.name,
                    description = list.description,
                    showCountText = localizer.getPlural(PluralsResourceKey.ShowCount, list.itemCount.toInt(), list.itemCount.toInt()),
                    isShowInList = list.isShowInList,
                )
            }.toImmutableList(),
            sheetTitle = localizer.getString(StringResourceKey.LabelWatchlistSaveToList),
            createListButtonText = localizer.getString(StringResourceKey.LabelWatchlistCreateCustomList),
            createListDoneText = localizer.getString(StringResourceKey.LabelWatchlistDone),
            createListPlaceholder = localizer.getString(StringResourceKey.LabelWatchlistNewListPlaceholder),
            emptyListText = localizer.getString(StringResourceKey.LabelWatchlistEmptyList),
            listsHeaderText = localizer.getString(StringResourceKey.LabelWatchlistYourLists),
            loginRequiredTitle = localizer.getString(StringResourceKey.LabelWatchlistLoginRequiredTitle),
            loginRequiredMessage = localizer.getString(StringResourceKey.LabelWatchlistLoginRequiredMessage),
            loginRequiredConfirmText = localizer.getString(StringResourceKey.LabelOk),
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
                        followShowInteractor(FollowShowInteractor.Param(traktId = showTraktId))
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        syncTraktCalendarInteractor(SyncTraktCalendarInteractor.Params(forceRefresh = true))
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        scheduleEpisodeNotificationsInteractor(ScheduleEpisodeNotificationsInteractor.Params())
                            .collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                        onShowFollowed()
                    }
                }
            }

            DetailBackClicked -> onBack()
            ReloadShowDetails -> refreshShowContent(isUserInitiated = true)
            is ShowDetailsMessageShown -> coroutineScope.launch { uiMessageManager.clearMessage(action.id) }
            DismissShowsListSheet -> coroutineScope.launch { _state.update { it.copy(showListSheet = false) } }
            ShowShowsListSheet -> {
                coroutineScope.launch {
                    if (traktAuthRepository.isLoggedIn()) {
                        _state.update { it.copy(showListSheet = true) }
                        syncTraktListsInteractor(SyncTraktListsInteractor.Params())
                            .collectStatus(showDetailsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                    } else {
                        _state.update { it.copy(showLoginPrompt = true) }
                    }
                }
            }
            DismissLoginPrompt -> coroutineScope.launch { _state.update { it.copy(showLoginPrompt = false) } }
            LoginClicked -> {
                _state.update { it.copy(showLoginPrompt = false) }
                traktAuthManager.launchWebView()
            }
            ShowCreateListField -> _state.update { it.copy(showCreateListField = true) }
            DismissCreateListField -> _state.update {
                it.copy(showCreateListField = false, createListName = "", createListError = null)
            }
            is UpdateCreateListName -> _state.update { it.copy(createListName = action.name) }
            CreateListSubmitted -> {
                val name = _state.value.createListName
                coroutineScope.launch {
                    _state.update { it.copy(isCreatingList = true) }
                    createTraktListInteractor(CreateTraktListInteractor.Params(name = name))
                        .collectStatus(
                            episodeActionLoadingState,
                            logger,
                            uiMessageManager,
                            errorToStringMapper = errorToStringMapper,
                        )
                    _state.update {
                        if (it.message == null) {
                            it.copy(isCreatingList = false, showCreateListField = false, createListName = "")
                        } else {
                            it.copy(isCreatingList = false)
                        }
                    }
                }
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
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
            }

            is MarkEpisodeUnwatched -> {
                coroutineScope.launch {
                    markEpisodeUnwatchedInteractor(
                        MarkEpisodeUnwatchedParams(
                            showTraktId = action.showTraktId,
                            episodeId = action.episodeId,
                        ),
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
            }

            is ToggleShowInList -> {
                coroutineScope.launch {
                    toggleShowInListInteractor(
                        ToggleShowInListInteractor.Params(
                            listId = action.listId,
                            traktShowId = showTraktId,
                            isCurrentlyInList = action.isCurrentlyInList,
                        ),
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                }
            }
        }
    }

    private fun observeShowDetails(forceReload: Boolean = false, isUserInitiated: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showTraktId, forceReload))
                .collectStatus(showDetailsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

            syncShowContent(
                forceRefresh = forceReload,
                isUserInitiated = isUserInitiated,
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
                .collect { refreshShowContent(isUserInitiated = false) }
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(
            componentContext: ComponentContext,
            param: ShowDetailsParam,
            onBack: () -> Unit,
            onNavigateToShow: (id: Long) -> Unit,
            onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
            onNavigateToTrailer: (id: Long) -> Unit,
            onShowFollowed: () -> Unit,
        ): ShowDetailsPresenter
    }
}
