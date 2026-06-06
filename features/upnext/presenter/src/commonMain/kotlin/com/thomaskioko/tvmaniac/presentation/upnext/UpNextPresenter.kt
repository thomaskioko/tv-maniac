package com.thomaskioko.tvmaniac.presentation.upnext

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.connectedaccount.api.ConnectedAccountRepository
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.continuewatching.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.SyncContinueWatchingInteractor
import com.thomaskioko.tvmaniac.domain.continuewatching.model.UpNextSortOption
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import com.thomaskioko.tvmaniac.progress.nav.scope.ProgressChildScope
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.UpNextEpisode
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

@ChildPresenter(scope = ProgressChildScope::class, parentScope = ProgressRoot::class)
@Inject
public class UpNextPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val syncContinueWatchingInteractor: SyncContinueWatchingInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val upNextRepository: UpNextRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val connectedAccountRepository: ConnectedAccountRepository,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    syncObserver: SyncObserver,
    observeUpNextInteractor: ObserveUpNextInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val uiMessageManager = UiMessageManager()
    private val loadingState = ObservableLoadingCounter()
    private val refreshingState = ObservableLoadingCounter()
    private val updatingEpisodeIdsState = MutableStateFlow(persistentSetOf<Long>())

    init {
        observeAuthState()
    }

    public val state: StateFlow<UpNextState> = combine(
        observeUpNextInteractor.flow,
        uiMessageManager.message,
        refreshingState.observable,
        loadingState.observable,
        updatingEpisodeIdsState,
        syncObserver.isSyncing,
    ) { result, message, isRefreshing, isLoading, updatingEpisodeIds, isSyncing ->
        UpNextState(
            isLoading = isLoading,
            isSyncing = isSyncing,
            isRefreshing = isRefreshing,
            sortOption = result.sortOption,
            episodes = result.episodes.map { it.toUiModel() }.toImmutableList(),
            updatingEpisodeIds = updatingEpisodeIds,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UpNextState(),
    )

    public val stateValue: Value<UpNextState> = state.asValue(coroutineScope)

    public fun dispatch(action: UpNextAction) {
        when (action) {
            is UpNextShowClicked -> navigateToSeasonFromEpisode(action.showId)
            is MarkWatched -> markEpisodeWatched(action)
            is UpNextChangeSortOption -> changeSortOption(action.sortOption)
            is RefreshUpNext -> refreshUpNext(isUserInitiated = true)
            is UpNextMessageShown -> clearMessage(action.id)
            is OpenShow -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            is OpenSeason -> navigator.navigateTo(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showId = action.showId,
                        seasonId = action.seasonId,
                        seasonNumber = action.seasonNumber,
                    ),
                ),
            )
            is UnfollowShow -> unfollowShow(action.showId)
            is UpNextEpisodeLongPressed -> navigator.navigateTo(
                EpisodeSheetRoute(EpisodeSheetParam(episodeId = action.episodeId, source = ScreenSource.UP_NEXT)),
            )
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            connectedAccountRepository.isConnected
                .distinctUntilChanged()
                .filter { it }
                .collect { refreshUpNext(isUserInitiated = false) }
        }
    }

    private fun refreshUpNext(isUserInitiated: Boolean = false) {
        val counter = if (isUserInitiated) refreshingState else loadingState
        coroutineScope.launch {
            syncContinueWatchingInteractor(SyncContinueWatchingInteractor.Param(forceRefresh = isUserInitiated))
                .collectStatus(counter, logger, uiMessageManager, "Up Next", errorToStringMapper)
        }
    }

    private fun markEpisodeWatched(action: MarkWatched) {
        if (action.episodeId in updatingEpisodeIdsState.value) return
        updatingEpisodeIdsState.update { it.add(action.episodeId) }
        coroutineScope.launch {
            val marker = TimeSource.Monotonic.markNow()
            try {
                markEpisodeWatchedInteractor(
                    MarkEpisodeWatchedParams(
                        showId = action.showId,
                        episodeId = action.episodeId,
                        seasonNumber = action.seasonNumber,
                        episodeNumber = action.episodeNumber,
                    ),
                ).collectStatus(loadingState, logger, uiMessageManager, "Mark Watched", errorToStringMapper)
            } finally {
                val elapsed = marker.elapsedNow()
                if (elapsed < INDICATOR_FLOOR) {
                    delay(INDICATOR_FLOOR - elapsed)
                }
                updatingEpisodeIdsState.update { it.remove(action.episodeId) }
            }
        }
    }

    private companion object {
        private val INDICATOR_FLOOR: Duration = 150.milliseconds
    }

    private fun changeSortOption(sortOption: UpNextSortOption) {
        coroutineScope.launch {
            upNextRepository.saveUpNextSortOption(sortOption.name)
        }
    }

    private fun navigateToSeasonFromEpisode(showId: Long) {
        val episode = state.value.episodes.firstOrNull { it.showId == showId }
        if (episode?.seasonId != null && episode.seasonNumber != null) {
            navigator.navigateTo(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showId = showId,
                        seasonId = episode.seasonId,
                        seasonNumber = episode.seasonNumber,
                    ),
                ),
            )
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
}

private fun UpNextEpisode.toUiModel(): UpNextEpisodeUiModel {
    val season = seasonNumber.toString().padStart(2, '0')
    val episode = episodeNumber.toString().padStart(2, '0')
    return UpNextEpisodeUiModel(
        showId = showId,
        showTmdbId = showTmdbId,
        showName = showName,
        imageUrl = stillPath ?: showPoster,
        showStatus = showStatus,
        showYear = showYear,
        episodeId = episodeId,
        episodeName = episodeName,
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        overview = overview,
        firstAired = firstAired,
        seasonCount = seasonCount,
        episodeCount = episodeCount,
        watchedCount = watchedCount,
        totalCount = totalCount,
        formattedEpisodeNumber = "S${season}E$episode",
        remainingEpisodes = totalCount - watchedCount,
        formattedRuntime = runtime?.let { "${it}m" },
        rating = rating,
        voteCount = voteCount,
    )
}
