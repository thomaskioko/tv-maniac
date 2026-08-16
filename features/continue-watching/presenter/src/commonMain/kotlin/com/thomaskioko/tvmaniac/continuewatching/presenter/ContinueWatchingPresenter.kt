package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.core.view.launchUpdating
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.flags.ContinueWatchingNitroFlagQualifier
import com.thomaskioko.tvmaniac.myshows.nav.MyShowsRoot
import com.thomaskioko.tvmaniac.myshows.nav.scope.MyShowsChildScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.presenter.promptForEpisodeRating
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionFeature
import com.thomaskioko.tvmaniac.subscription.api.SubscriptionManager
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.watchlistprefs.api.WatchlistPrefsRepository
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ChildPresenter(scope = MyShowsChildScope::class, parentScope = MyShowsRoot::class)
@Inject
public class ContinueWatchingPresenter internal constructor(
    @ContinueWatchingNitroFlagQualifier
    nitroFlag: FeatureFlag<Boolean>,
    syncObserver: SyncObserver,
    repository: WatchlistPrefsRepository,
    subscriptionManager: SubscriptionManager,
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val shouldPromptForRatingInteractor: ShouldPromptForRatingInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val mapper: ContinueWatchingMapper,
    private val logger: Logger,
    private val accountManager: AccountManager,
) : ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val userRefreshState = ObservableLoadingCounter()
    private val episodeActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val updatingEpisodeIdsState = MutableStateFlow(persistentSetOf<Long>())

    // TODO:: This is an experiment. Move to repository
    private val nitroEnabled: StateFlow<Boolean> = nitroFlag
        .observe()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = false,
        )

    private val resolvedListStyle = kotlinx.coroutines.flow.combine(
        repository.observeListStyle(),
        subscriptionManager.observeAccess(SubscriptionFeature.ListViewTypes),
    ) { listStyle, hasAccess ->
        if (hasAccess) listStyle else listStyle.freeFallback
    }

    init {
        observeWatchlistSectionsInteractor(queryFlow.value)
        observeUpNextSectionsInteractor(queryFlow.value)
        observeAuthState()
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            accountManager.isConnected
                .distinctUntilChanged()
                .filter { it }
                .collect { syncWatchlist(forceRefresh = false) }
        }
    }

    public val state: StateFlow<ContinueWatchingState> = combine(
        updatingEpisodeIdsState,
        userRefreshState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        resolvedListStyle,
        repository.observeSortOption(),
        uiMessageManager.message,
        queryFlow,
        syncObserver.isSyncing,
        watchlistLoadingState.observable,
        episodeActionLoadingState.observable,
    ) { updatingEpisodeIds, isUserRefreshing, watchlistSections, upNextSections, listStyle, sortOption, message, query, isSyncing, isLoading, isUpdating ->
        val sectionedItems = mapper.toSectionedItems(watchlistSections, sortOption)
        val sectionedEpisodes = mapper.toSectionedEpisodes(upNextSections)
        ContinueWatchingState(
            query = query,
            listStyle = listStyle,
            isLoading = isLoading,
            isRefreshing = isUserRefreshing,
            isSyncing = isSyncing,
            labels = mapper.resolveLabels(query),
            watchNextItems = sectionedItems.watchNext,
            staleItems = sectionedItems.stale,
            watchNextEpisodes = sectionedEpisodes.watchNext,
            staleEpisodes = sectionedEpisodes.stale,
            message = message,
            updatingEpisodeIds = updatingEpisodeIds,
            isUpdating = isUpdating,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ContinueWatchingState(),
    )

    public val stateValue: Value<ContinueWatchingState> = state.asValue(coroutineScope)

    public fun dispatch(action: ContinueWatchingAction) {
        when (action) {
            is ContinueWatchingShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is ContinueWatchingMessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is ShowTitleClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showId)
            is OpenSeasonFromUpNext -> navigator.navigateTo(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showId = action.showId,
                        seasonId = action.seasonId,
                        seasonNumber = action.seasonNumber,
                    ),
                ),
            )

            is RefreshContinueWatching -> syncWatchlist(action.forceRefresh)
        }
    }

    private fun markEpisodeWatched(action: MarkUpNextEpisodeWatched) {
        coroutineScope.launchUpdating(
            id = action.episodeId,
            updatingIds = updatingEpisodeIdsState,
            counter = episodeActionLoadingState,
            logger = logger,
            uiMessageManager = uiMessageManager,
            errorToStringMapper = errorToStringMapper,
        ) {
            markEpisodeWatchedInteractor(
                MarkEpisodeWatchedParams(
                    showId = action.showId,
                    episodeId = action.episodeId,
                    seasonNumber = action.seasonNumber,
                    episodeNumber = action.episodeNumber,
                ),
            ).onCompletion {
                navigator.promptForEpisodeRating(
                    interactor = shouldPromptForRatingInteractor,
                    showId = action.showId,
                    episodeId = action.episodeId,
                )
            }
        }
    }

    private fun unfollowShow(showId: Long) {
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(showId)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    public fun onQueryChanged(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            observeWatchlistSectionsInteractor(query)
            observeUpNextSectionsInteractor(query)
        }
    }

    /**
     * Suspends until the refresh finishes so a caller can hold a progress indicator open for it.
     * The work runs in the presenter's own scope, so leaving the screen part way through cancels
     * the wait rather than the sync.
     */
    public suspend fun refresh() {
        syncWatchlist(forceRefresh = true).join()
    }

    private fun syncWatchlist(forceRefresh: Boolean = false): Job = coroutineScope.launch {
        val counter = if (forceRefresh) userRefreshState else watchlistLoadingState
        syncContinueWatchingInteractor(
            SyncContinueWatchingInteractor.Param(
                forceRefresh = forceRefresh,
                useNitro = nitroEnabled.value,
            ),
        )
            .collectStatus(
                counter = counter,
                logger = logger,
                uiMessageManager = uiMessageManager,
                errorToStringMapper = errorToStringMapper,
            )
    }
}
