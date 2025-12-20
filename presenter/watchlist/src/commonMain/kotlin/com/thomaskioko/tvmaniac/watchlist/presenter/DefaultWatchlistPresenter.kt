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
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveUpNextSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.ObserveWatchlistSectionsInteractor
import com.thomaskioko.tvmaniac.domain.watchlist.WatchlistInteractor
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, WatchlistPresenter::class)
class DefaultWatchlistPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    @Assisted private val navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    private val repository: WatchlistRepository,
    private val observeWatchlistSectionsInteractor: ObserveWatchlistSectionsInteractor,
    private val observeUpNextSectionsInteractor: ObserveUpNextSectionsInteractor,
    private val refreshWatchlistInteractor: WatchlistInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val dateTimeProvider: DateTimeProvider,
    private val logger: Logger,
) : WatchlistPresenter, ComponentContext by componentContext {

    private val watchlistLoadingState = ObservableLoadingCounter()
    private val upNextActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val queryFlow = MutableStateFlow("")

    init {
        observeWatchlistSectionsInteractor(queryFlow.value)
        observeUpNextSectionsInteractor(queryFlow.value)
    }

    override val state: StateFlow<WatchlistState> = combine(
        watchlistLoadingState.observable,
        upNextActionLoadingState.observable,
        observeWatchlistSectionsInteractor.flow,
        observeUpNextSectionsInteractor.flow,
        repository.observeListStyle(),
        uiMessageManager.message,
        queryFlow,
    ) { isLoading, upNextLoading, watchlistSections, upNextSections, isGridMode, message, query ->
        val currentTime = dateTimeProvider.nowMillis()
        val sectionedItems = watchlistSections.toPresenter()
        val sectionedEpisodes = upNextSections.toPresenter(currentTime)
        WatchlistState(
            query = query,
            isSearchActive = query.isNotBlank(),
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
            is ReloadWatchlist -> refreshWatchlist()
            is WatchlistShowClicked -> navigateToShowDetails(action.id)
            is WatchlistQueryChanged -> updateQuery(action.query)
            is ClearWatchlistQuery -> clearQuery()
            ChangeListStyleClicked -> toggleListStyle()
            is MessageShown -> clearMessage(action.id)
            is UpNextEpisodeClicked -> navigateToShowDetails(action.showId)
            is ShowTitleClicked -> navigateToShowDetails(action.showId)
            is MarkUpNextEpisodeWatched -> markEpisodeWatched(action)
            is UnfollowShowFromUpNext -> unfollowShow(action.showId)
            is OpenSeasonFromUpNext -> navigateToSeason(action.showId, action.seasonId, action.seasonNumber)
        }
    }

    private fun refreshWatchlist() {
        coroutineScope.launch {
            refreshWatchlistInteractor(Unit)
                .collectStatus(watchlistLoadingState, logger, uiMessageManager)
        }
    }

    private fun markEpisodeWatched(action: MarkUpNextEpisodeWatched) {
        coroutineScope.launch {
            markEpisodeWatchedInteractor(
                MarkEpisodeWatchedParams(
                    showId = action.showId,
                    episodeId = action.episodeId,
                    seasonNumber = action.seasonNumber,
                    episodeNumber = action.episodeNumber,
                ),
            ).collectStatus(upNextActionLoadingState, logger, uiMessageManager)
        }
    }

    private fun unfollowShow(showId: Long) {
        coroutineScope.launch {
            repository.updateLibrary(id = showId, addToLibrary = false)
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

    private fun toggleListStyle() {
        coroutineScope.launch {
            val currentIsGridMode = repository.observeListStyle().first()
            val newIsGridMode = !currentIsGridMode

            repository.saveListStyle(newIsGridMode)
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, WatchlistPresenter.Factory::class)
class DefaultWatchlistPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ) -> WatchlistPresenter,
) : WatchlistPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showDetails: Long) -> Unit,
        navigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): WatchlistPresenter = presenter(componentContext, navigateToShowDetails, navigateToSeason)
}
