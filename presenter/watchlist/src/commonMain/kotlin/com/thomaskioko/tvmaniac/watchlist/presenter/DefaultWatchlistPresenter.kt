package com.thomaskioko.tvmaniac.watchlist.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.FollowedShowsSyncInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, WatchlistPresenter::class)
public class DefaultWatchlistPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    @Assisted private val navigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    private val repository: WatchlistRepository,
    private val followedShowsRepository: FollowedShowsRepository,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val followedShowsSyncInteractor: FollowedShowsSyncInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val logger: Logger,
) : WatchlistPresenter, ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val upNextActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")
    private val _state = MutableStateFlow(WatchlistState())

    init {
        observeWatchlistSectionsInteractor(queryFlow.value)
        observeUpNextSectionsInteractor(queryFlow.value)
        syncWatchlist()
    }

    override val state: StateFlow<WatchlistState> = combine(
        _state,
        watchlistLoadingState.observable,
        upNextActionLoadingState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        repository.observeListStyle(),
        uiMessageManager.message,
        queryFlow,
    ) { currentState, isLoading, upNextLoading, watchlistSections, upNextSections, isGridMode, message, query ->
        val sectionedItems = watchlistSections.toPresenter()
        val sectionedEpisodes = upNextSections.toPresenter()
        currentState.copy(
            query = query,
            isGridMode = isGridMode,
            isRefreshing = isLoading || upNextLoading,
            watchNextItems = sectionedItems.watchNext,
            staleItems = sectionedItems.stale,
            watchNextEpisodes = sectionedEpisodes.watchNext,
            staleEpisodes = sectionedEpisodes.stale,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = WatchlistState(),
    )

    override fun dispatch(action: WatchlistAction) {
        when (action) {
            is WatchlistShowClicked -> navigateToShowDetails(action.traktId)
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            is ToggleSearchActive -> toggleSearchActive()
            is ChangeListStyleClicked -> toggleListStyle(action.isGridMode)
            is MessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigateToShowDetails(action.showTraktId)
            is ShowTitleClicked -> navigateToShowDetails(action.showTraktId)
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showTraktId)
            is OpenSeasonFromUpNext -> navigateToSeason(action.showTraktId, action.seasonId, action.seasonNumber)
            is RefreshWatchlist -> syncWatchlist(action.forceRefresh)
        }
    }

    private fun markEpisodeWatched(action: MarkUpNextEpisodeWatched) {
        coroutineScope.launch {
            markEpisodeWatchedInteractor(
                MarkEpisodeWatchedParams(
                    showTraktId = action.showTraktId,
                    episodeId = action.episodeId,
                    seasonNumber = action.seasonNumber,
                    episodeNumber = action.episodeNumber,
                ),
            ).collectStatus(upNextActionLoadingState, logger, uiMessageManager)
        }
    }

    private fun unfollowShow(showTraktId: Long) {
        coroutineScope.launch {
            followedShowsRepository.removeFollowedShow(showTraktId)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private fun updateQuery(query: String) {
        coroutineScope.launch {
            queryFlow.emit(query)
            observeWatchlistSectionsInteractor(query)
            observeUpNextSectionsInteractor(query)
        }
    }

    private fun clearQuery() {
        coroutineScope.launch {
            queryFlow.emit("")
            observeWatchlistSectionsInteractor("")
            observeUpNextSectionsInteractor("")
        }
    }

    private fun toggleSearchActive() {
        _state.update { it.copy(isSearchActive = !it.isSearchActive) }
    }

    private fun toggleListStyle(currentIsGridMode: Boolean) {
        coroutineScope.launch {
            repository.saveListStyle(!currentIsGridMode)
        }
    }

    private fun syncWatchlist(forceRefresh: Boolean = false) {
        coroutineScope.launch {
            followedShowsSyncInteractor(FollowedShowsSyncInteractor.Param(forceRefresh))
                .collectStatus(watchlistLoadingState, logger, uiMessageManager)
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, WatchlistPresenter.Factory::class)
public class DefaultWatchlistPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ) -> WatchlistPresenter,
) : WatchlistPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showTraktId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): WatchlistPresenter = presenter(componentContext, navigateToShowDetails, navigateToSeason)
}
