package com.thomaskioko.tvmaniac.presentation.upnext

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.upnext.ObserveUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.RefreshUpNextInteractor
import com.thomaskioko.tvmaniac.domain.upnext.model.UpNextSortOption
import com.thomaskioko.tvmaniac.presentation.upnext.model.UpNextEpisodeUiModel
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.upnext.api.UpNextRepository
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, UpNextPresenter::class)
public class DefaultUpNextPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (showTraktId: Long) -> Unit,
    private val refreshUpNextInteractor: RefreshUpNextInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val upNextRepository: UpNextRepository,
    private val traktAuthRepository: TraktAuthRepository,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
    observeUpNextInteractor: ObserveUpNextInteractor,
) : UpNextPresenter, ComponentContext by componentContext {

    private val uiMessageManager = UiMessageManager()
    private val loadingState = ObservableLoadingCounter()
    private val refreshingState = ObservableLoadingCounter()
    private val markWatchedLoadingState = ObservableLoadingCounter()

    init {
        observeAuthState()
        observeFollowedShows()
    }

    override val state: StateFlow<UpNextState> = combine(
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

    override fun dispatch(action: UpNextAction) {
        when (action) {
            is UpNextShowClicked -> navigateToShowDetails(action.showTraktId)
            is MarkWatched -> markEpisodeWatched(action)
            is UpNextChangeSortOption -> changeSortOption(action.sortOption)
            is RefreshUpNext -> refreshUpNext(isUserInitiated = true)
            is UpNextMessageShown -> clearMessage(action.id)
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
                .collectStatus(counter, logger, uiMessageManager, "Up Next")
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
            ).collectStatus(markWatchedLoadingState, logger, uiMessageManager, "Mark Watched")
        }
    }

    private fun changeSortOption(sortOption: UpNextSortOption) {
        coroutineScope.launch {
            upNextRepository.saveUpNextSortOption(sortOption.name)
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
        showPoster = showPoster,
        showStatus = showStatus,
        showYear = showYear,
        episodeId = episodeId,
        episodeName = episodeName,
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime,
        stillPath = stillPath,
        overview = overview,
        firstAired = firstAired,
        seasonCount = seasonCount,
        episodeCount = episodeCount,
        watchedCount = watchedCount,
        totalCount = totalCount,
        formattedEpisodeNumber = "S${season}E$episode",
        remainingEpisodes = totalCount - watchedCount,
        formattedRuntime = runtime?.let { "${it}m" },
    )
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, UpNextPresenter.Factory::class)
public class DefaultUpNextPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        navigateToShowDetails: (showTraktId: Long) -> Unit,
    ) -> UpNextPresenter,
) : UpNextPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        navigateToShowDetails: (showTraktId: Long) -> Unit,
    ): UpNextPresenter = presenter(componentContext, navigateToShowDetails)
}
