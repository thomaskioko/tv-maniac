package com.thomaskioko.tvmaniac.presenter.showdetails.header

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.nav.NotificationRationale
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.notifications.api.NotificationManager
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.episode.MarkWatchedAtInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkWatchedAtParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveCommunityRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RefreshCommunityRatingInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.ObserveRewatchStatusInteractor
import com.thomaskioko.tvmaniac.domain.rewatch.StartRewatchSessionInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.traktlists.ObserveTraktListsInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.toHeaderState
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsChildScope
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionParam
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ChildPresenter(scope = ShowDetailsChildScope::class, parentScope = ShowDetailsRoute::class)
@AssistedInject
public class ShowDetailsHeaderPresenter internal constructor(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    private val navigator: Navigator,
    private val notificationRationale: NotificationRationale,
    private val followedShowsRepository: FollowedShowsRepository,
    private val followShowInteractor: FollowShowInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val refreshCommunityRatingInteractor: RefreshCommunityRatingInteractor,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observeRatingInteractor: ObserveRatingInteractor,
    observeCommunityRatingInteractor: ObserveCommunityRatingInteractor,
    observeTraktListsInteractor: ObserveTraktListsInteractor,
    observeRewatchStatusInteractor: ObserveRewatchStatusInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    private val startRewatchSessionInteractor: StartRewatchSessionInteractor,
    private val markWatchedAtInteractor: MarkWatchedAtInteractor,
    private val datastoreRepository: DatastoreRepository,
    private val syncCalendarInteractor: SyncCalendarInteractor,
    private val scheduleEpisodeNotificationsInteractor: ScheduleEpisodeNotificationsInteractor,
    private val notificationManager: NotificationManager,
    private val accountManager: AccountManager,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val followLoadingState = ObservableLoadingCounter()
    private val communityRatingLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(ShowDetailsHeaderState())

    init {
        observableShowDetailsInteractor(showId)
        observeRatingInteractor(ObserveRatingInteractor.Param(RatingEntityType.SHOW, showId))
        observeCommunityRatingInteractor(showId)
        observeTraktListsInteractor(showId)
        observeRewatchStatusInteractor(showId)
        observeShowWatchProgressInteractor(showId)

        fetchShowDetails(forceRefresh = forceRefresh)
        refreshCommunityRating(forceRefresh = forceRefresh)
        observeAuthState()
        updateListAvailability()
    }

    public val state: StateFlow<ShowDetailsHeaderState> = combine(
        observableShowDetailsInteractor.flow,
        observeRatingInteractor.flow,
        observeCommunityRatingInteractor.flow,
        observeTraktListsInteractor.flow,
        observeRewatchStatusInteractor.flow,
        observeShowWatchProgressInteractor.flow,
        datastoreRepository.observeMultiplePlaysEnabled(),
        loadingState.observable,
        uiMessageManager.message,
        _state,
    ) { details, userRating, communityRating, traktLists, rewatchStatus, watchProgress,
        multiplePlaysEnabled, isRefreshing, message, current,
        ->
        val isInList = traktLists.any { it.isShowInList }
        val unwatchedEpisodeCount = (watchProgress.totalCount - watchProgress.watchedCount).coerceAtLeast(0)
        details.toHeaderState(localizer).copy(
            communityRating = communityRating?.rating,
            communityVotes = communityRating?.votes,
            userRating = userRating,
            isRefreshing = isRefreshing,
            message = message,
            canAddToList = current.canAddToList,
            isInList = isInList,
            listActionLabel = localizer.getString(
                if (isInList) StringResourceKey.BtnInList else StringResourceKey.BtnAddToList,
            ),
            rewatchCount = rewatchStatus.finishedCount,
            canWatchAgain = multiplePlaysEnabled && details.isInLibrary && rewatchStatus.openSession == null,
            canMarkShowWatched = details.isInLibrary && rewatchStatus.openSession == null && unwatchedEpisodeCount > 0,
            unwatchedEpisodeCount = unwatchedEpisodeCount,
            markShowWatchedLabel = localizer.getString(StringResourceKey.LabelMarkShowWatched),
            markShowWatchedTitle = localizer.getString(StringResourceKey.LabelMarkShowWatchedConfirmTitle),
            markShowWatchedMessage = localizer.getPlural(
                PluralsResourceKey.DialogMessageMarkShowWatched,
                unwatchedEpisodeCount,
                unwatchedEpisodeCount,
            ),
            showMoreSheet = current.showMoreSheet,
            showWatchAgainConfirmation = current.showWatchAgainConfirmation,
            showMarkShowWatchedConfirmation = current.showMarkShowWatchedConfirmation,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ShowDetailsHeaderState(),
    )

    public val stateValue: Value<ShowDetailsHeaderState> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsHeaderAction) {
        when (action) {
            is ShowDetailsFollowClicked -> onFollowClicked(action.isInLibrary)
            ShowDetailsOpenShowList -> if (_state.value.canAddToList) {
                navigator.navigateTo(ShowListRoute(ShowListParam(showId = showId)))
            }
            ShowRatingClicked -> navigator.navigateTo(
                RatingSheetRoute(RatingSheetParam(ratingType = RatingEntityType.SHOW, id = showId)),
            )
            ShowDetailsMoreClicked -> _state.update { it.copy(showMoreSheet = true) }
            ShowDetailsMoreDismissed -> _state.update { it.copy(showMoreSheet = false) }
            WatchAgainClicked -> _state.update {
                it.copy(showMoreSheet = false, showWatchAgainConfirmation = true)
            }
            WatchAgainDismissed -> _state.update { it.copy(showWatchAgainConfirmation = false) }
            WatchAgainConfirmed -> onWatchAgainConfirmed()
            MarkShowWatchedClicked -> _state.update {
                it.copy(showMoreSheet = false, showMarkShowWatchedConfirmation = true)
            }
            MarkShowWatchedDismissed -> _state.update { it.copy(showMarkShowWatchedConfirmation = false) }
            MarkShowWatchedConfirmed -> onMarkShowWatchedConfirmed()
        }
    }

    public fun refresh() {
        fetchShowDetails(forceRefresh = true)
        refreshCommunityRating(forceRefresh = true)
    }

    public fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun onFollowClicked(isInLibrary: Boolean) {
        coroutineScope.launch {
            if (isInLibrary) {
                followedShowsRepository.removeFollowedShow(showId)
                notificationManager.cancelNotificationsForShow(showId)
            } else {
                followShowInteractor(FollowShowInteractor.Param(showId = showId))
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                syncCalendarInteractor(SyncCalendarInteractor.Params(forceRefresh = true))
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                scheduleEpisodeNotificationsInteractor(ScheduleEpisodeNotificationsInteractor.Params())
                    .collectStatus(followLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

                notificationRationale.showIfNeeded()
            }
        }
    }

    private fun refreshCommunityRating(forceRefresh: Boolean) {
        coroutineScope.launch {
            refreshCommunityRatingInteractor(
                RefreshCommunityRatingInteractor.Param(showId = showId, forceRefresh = forceRefresh),
            ).collectStatus(communityRatingLoadingState, logger, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun fetchShowDetails(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Show Details", errorToStringMapper)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .drop(1)
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    updateListAvailability()
                    fetchShowDetails(forceRefresh = true)
                }
        }
    }

    private fun onWatchAgainConfirmed() {
        _state.update { it.copy(showWatchAgainConfirmation = false) }
        coroutineScope.launch {
            startRewatchSessionInteractor(StartRewatchSessionInteractor.Param(showId = showId))
                .collectStatus(loadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun onMarkShowWatchedConfirmed() {
        _state.update { it.copy(showMarkShowWatchedConfirmation = false) }
        coroutineScope.launch {
            if (datastoreRepository.observeCustomWatchDateEnabled().first()) {
                navigator.navigateTo(
                    WatchDateSelectionRoute(
                        WatchDateSelectionParam(
                            target = WatchedDateTarget.SHOW,
                            showId = showId,
                        ),
                    ),
                )
            } else {
                markWatchedAtInteractor(MarkWatchedAtParams(target = WatchedDateTarget.SHOW, showId = showId))
                    .collectStatus(loadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
            }
        }
    }

    private fun updateListAvailability() {
        val canAddToList = accountManager.getActiveProvider() == null || activeProviderFeatures().supportsLists
        _state.update { it.copy(canAddToList = canAddToList) }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsHeaderPresenter
    }
}
