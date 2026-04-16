package com.thomaskioko.tvmaniac.presentation.upnext

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.root.nav.EpisodeSheetNavigator
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.upnext.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
public class UpNextPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val episodeSheetNavigator: EpisodeSheetNavigator,
    private val refreshUpNextInteractor: RefreshUpNextInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val upNextRepository: UpNextRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    observeUpNextInteractor: ObserveUpNextInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val uiMessageManager = UiMessageManager()
    private val loadingState = ObservableLoadingCounter()
    private val refreshingState = ObservableLoadingCounter()
    private val markWatchedLoadingState = ObservableLoadingCounter()

    init {
        observeAuthState()
        observeFollowedShows()
    }

    public val state: StateFlow<UpNextState> = combine(
        observeUpNextInteractor.flow,
        uiMessageManager.message,
        refreshingState.observable,
        loadingState.observable,
    ) { result, message, isRefreshing, isLoading ->
        UpNextState(
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            sortOption = result.sortOption,
            episodes = result.episodes.map { it.toUiModel() }.toImmutableList(),
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
            is UpNextShowClicked -> navigateToSeasonFromEpisode(action.showTraktId)
            is MarkWatched -> markEpisodeWatched(action)
            is UpNextChangeSortOption -> changeSortOption(action.sortOption)
            is RefreshUpNext -> refreshUpNext(isUserInitiated = true)
            is UpNextMessageShown -> clearMessage(action.id)
            is OpenShow -> navigator.pushNew(ShowDetailsRoute(ShowDetailsParam(id = action.showTraktId)))
            is OpenSeason -> navigator.pushNew(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showTraktId = action.showTraktId,
                        seasonId = action.seasonId,
                        seasonNumber = action.seasonNumber,
                    ),
                ),
            )
            is UnfollowShow -> unfollowShow(action.showTraktId)
            is UpNextEpisodeLongPressed -> episodeSheetNavigator.showEpisodeSheet(action.episodeId, ScreenSource.UP_NEXT)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .collect { refreshUpNext() }
        }
    }

    private fun observeFollowedShows() {
        coroutineScope.launch {
            upNextRepository.observeFollowedShowsCount()
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    refreshUpNext()
                }
        }
    }

    private fun refreshUpNext(isUserInitiated: Boolean = false) {
        val counter = if (isUserInitiated) refreshingState else loadingState
        coroutineScope.launch {
            refreshUpNextInteractor(isUserInitiated)
                .collectStatus(counter, logger, uiMessageManager, "Up Next", errorToStringMapper)
        }
    }

    private fun markEpisodeWatched(action: MarkWatched) {
        coroutineScope.launch {
            markEpisodeWatchedInteractor(
                MarkEpisodeWatchedParams(
                    showTraktId = action.showTraktId,
                    episodeId = action.episodeId,
                    seasonNumber = action.seasonNumber,
                    episodeNumber = action.episodeNumber,
                ),
            ).collectStatus(markWatchedLoadingState, logger, uiMessageManager, "Mark Watched", errorToStringMapper)
        }
    }

    private fun changeSortOption(sortOption: UpNextSortOption) {
        coroutineScope.launch {
            upNextRepository.saveUpNextSortOption(sortOption.name)
        }
    }

    private fun navigateToSeasonFromEpisode(showTraktId: Long) {
        val episode = state.value.episodes.firstOrNull { it.showTraktId == showTraktId }
        if (episode?.seasonId != null && episode.seasonNumber != null) {
            navigator.pushNew(
                SeasonDetailsRoute(
                    SeasonDetailsUiParam(
                        showTraktId = showTraktId,
                        seasonId = episode.seasonId,
                        seasonNumber = episode.seasonNumber,
                    ),
                ),
            )
        }
    }

    private fun unfollowShow(showTraktId: Long) {
        coroutineScope.launch {
            unfollowShowInteractor.executeSync(showTraktId)
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}

private fun NextEpisodeWithShow.toUiModel(): UpNextEpisodeUiModel {
    val season = seasonNumber.toString().padStart(2, '0')
    val episode = episodeNumber.toString().padStart(2, '0')
    return UpNextEpisodeUiModel(
        showTraktId = showTraktId,
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
