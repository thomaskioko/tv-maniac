package com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes

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
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.SeasonSortOrder
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.episode.SyncShowEpisodeWatchesInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FetchSeasonsEpisodesInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveContinueTrackingInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveSeasonsInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.toContinueTrackingModels
import com.thomaskioko.tvmaniac.presenter.showdetails.toScrollIndex
import com.thomaskioko.tvmaniac.presenter.showdetails.toSeasonModels
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.scope.ShowDetailsChildScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ChildPresenter(scope = ShowDetailsChildScope::class, parentScope = ShowDetailsRoute::class)
@AssistedInject
public class ShowDetailsSeasonsEpisodesPresenter(
    componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val forceRefresh: Boolean,
    observeSeasonsInteractor: ObserveSeasonsInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    observeContinueTrackingInteractor: ObserveContinueTrackingInteractor,
    private val fetchSeasonsEpisodesInteractor: FetchSeasonsEpisodesInteractor,
    private val syncShowEpisodeWatchesInteractor: SyncShowEpisodeWatchesInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val navigator: Navigator,
    private val accountManager: AccountManager,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    datastoreRepository: DatastoreRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val loadingState = ObservableLoadingCounter()
    private val episodeActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(ShowDetailsSeasonsEpisodesState())
    private val updatingEpisodeIdsState = MutableStateFlow(persistentSetOf<Long>())

    init {
        observeSeasonsInteractor(showId)
        observeShowWatchProgressInteractor(showId)
        observeContinueTrackingInteractor(showId)
        fetchSeasonsEpisodes(forceRefresh = forceRefresh)
        observeAuthState()
    }

    public val state: StateFlow<ShowDetailsSeasonsEpisodesState> = combine(
        observeSeasonsInteractor.flow,
        observeShowWatchProgressInteractor.flow,
        observeContinueTrackingInteractor.flow,
        uiMessageManager.message,
        _state,
        loadingState.observable,
        updatingEpisodeIdsState,
        episodeActionLoadingState.observable,
        datastoreRepository.observeSeasonSortOrder(),
    ) { seasons, progress, continueEpisodes, message, currentState, isRefreshing, updatingEpisodeIds, isUpdating, sortOrder ->
        val seasonModels = seasons.toSeasonModels().let {
            if (sortOrder == SeasonSortOrder.NEWEST_FIRST) it.reversed().toImmutableList() else it
        }
        currentState.copy(
            seasonsList = seasonModels,
            numberOfSeasons = seasons.size,
            watchedEpisodesCount = progress.watchedCount,
            totalEpisodesCount = progress.totalCount,
            watchProgress = progress.progressPercentage,
            continueTrackingEpisodes = continueEpisodes.toContinueTrackingModels(showId),
            continueTrackingScrollIndex = continueEpisodes.toScrollIndex(),
            isRefreshing = isRefreshing,
            updatingEpisodeIds = updatingEpisodeIds,
            isUpdating = isUpdating,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = _state.value,
    )

    public val stateValue: Value<ShowDetailsSeasonsEpisodesState> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsSeasonsEpisodesAction) {
        when (action) {
            is ShowDetailsSeasonClicked -> {
                _state.update { it.copy(selectedSeasonIndex = action.params.selectedSeasonIndex) }
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

            is ShowDetailsMarkEpisodeWatched -> coroutineScope.launchUpdating(
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
                        markPreviousEpisodes = false,
                    ),
                )
            }

            is ShowDetailsMarkEpisodeUnwatched -> coroutineScope.launchUpdating(
                id = action.episodeId,
                updatingIds = updatingEpisodeIdsState,
                counter = episodeActionLoadingState,
                logger = logger,
                uiMessageManager = uiMessageManager,
                errorToStringMapper = errorToStringMapper,
            ) {
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(
                        showId = action.showId,
                        episodeId = action.episodeId,
                    ),
                )
            }
        }
    }

    public fun refresh() {
        fetchSeasonsEpisodes(forceRefresh = true)
    }

    public fun clearMessage(id: Long) {
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
                .collect { syncWatchStatus(forceRefresh = true) }
        }
    }

    private fun fetchSeasonsEpisodes(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            fetchSeasonsEpisodesInteractor(FetchSeasonsEpisodesInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Seasons & Episodes", errorToStringMapper)
        }
    }

    private fun syncWatchStatus(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            syncShowEpisodeWatchesInteractor(SyncShowEpisodeWatchesInteractor.Param(showId, forceRefresh))
                .collectStatus(loadingState, logger, uiMessageManager, "Watch status", errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(showId: Long, forceRefresh: Boolean): ShowDetailsSeasonsEpisodesPresenter
    }
}
