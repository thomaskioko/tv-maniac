package com.thomaskioko.tvmaniac.presenter.showdetails

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
import com.thomaskioko.tvmaniac.domain.episode.ObserveContinueTrackingInteractor
import com.thomaskioko.tvmaniac.domain.episode.ObserveShowWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObservableShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowSeasonDetailsParam
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
@ContributesBinding(ActivityScope::class, boundType = ShowDetailsPresenter::class)
public class DefaultShowDetailsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val showId: Long,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onNavigateToShow: (id: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
    @Assisted private val onNavigateToTrailer: (id: Long) -> Unit,
    private val followedShowsRepository: FollowedShowsRepository,
    private val recommendedShowsInteractor: RecommendedShowsInteractor,
    private val showDetailsInteractor: ShowDetailsInteractor,
    private val similarShowsInteractor: SimilarShowsInteractor,
    private val watchProvidersInteractor: WatchProvidersInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    observableShowDetailsInteractor: ObservableShowDetailsInteractor,
    observeShowWatchProgressInteractor: ObserveShowWatchProgressInteractor,
    observeContinueTrackingInteractor: ObserveContinueTrackingInteractor,
    private val logger: Logger,
) : ShowDetailsPresenter, ComponentContext by componentContext {

    private val recommendedShowsLoadingState = ObservableLoadingCounter()
    private val showDetailsLoadingState = ObservableLoadingCounter()
    private val similarShowsLoadingState = ObservableLoadingCounter()
    private val watchProvidersLoadingState = ObservableLoadingCounter()
    private val episodeActionLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val coroutineScope = coroutineScope()
    private val _state = MutableStateFlow(ShowDetailsContent.Empty)

    init {
        observableShowDetailsInteractor(showId)
        observeShowWatchProgressInteractor(showId)
        observeContinueTrackingInteractor(showId)
        coroutineScope.launch { observeShowDetails() }
    }

    override val state: StateFlow<ShowDetailsContent> = combine(
        recommendedShowsLoadingState.observable,
        showDetailsLoadingState.observable,
        similarShowsLoadingState.observable,
        watchProvidersLoadingState.observable,
        observableShowDetailsInteractor.flow,
        observeShowWatchProgressInteractor.flow,
        observeContinueTrackingInteractor.flow,
        _state,
    ) { recommendedShowsUpdating, showDetailsUpdating, similarShowsUpdating, watchProvidersUpdating,
        showDetails, watchProgress, continueTrackingResult, currentState,
        ->
        currentState.copy(
            showDetails = showDetails.toShowDetails(
                watchedEpisodesCount = watchProgress.watchedCount,
                totalEpisodesCount = watchProgress.totalCount,
                watchProgress = watchProgress.progressPercentage,
            ),
            recommendedShowsRefreshing = recommendedShowsUpdating,
            showDetailsRefreshing = showDetailsUpdating,
            similarShowsRefreshing = similarShowsUpdating,
            watchProvidersRefreshing = watchProvidersUpdating,
            continueTrackingEpisodes = mapContinueTrackingEpisodes(continueTrackingResult, showId),
            continueTrackingScrollIndex = continueTrackingResult?.firstUnwatchedIndex ?: 0,
        )
    }.stateIn(
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
                    if (action.addToLibrary) {
                        followedShowsRepository.removeFollowedShow(showId)
                    } else {
                        followedShowsRepository.addFollowedShow(showId)
                    }
                }
            }

            DetailBackClicked -> onBack()
            ReloadShowDetails -> coroutineScope.launch { observeShowDetails(forceReload = true) }
            DismissErrorSnackbar -> coroutineScope.launch { _state.update { it.copy(message = null) } }
            DismissShowsListSheet -> coroutineScope.launch { _state.update { it.copy(showListSheet = false) } }
            ShowShowsListSheet -> coroutineScope.launch { _state.update { it.copy(showListSheet = true) } }
            CreateCustomList -> {
                // TODO:: Add implementation
            }

            is MarkEpisodeWatched -> {
                coroutineScope.launch {
                    markEpisodeWatchedInteractor(
                        MarkEpisodeWatchedParams(
                            showId = action.showId,
                            episodeId = action.episodeId,
                            seasonNumber = action.seasonNumber,
                            episodeNumber = action.episodeNumber,
                            markPreviousEpisodes = false,
                        ),
                    ).collectStatus(episodeActionLoadingState, logger, uiMessageManager)
                }
            }
        }
    }

    private fun observeShowDetails(forceReload: Boolean = false) {
        coroutineScope.launch {
            recommendedShowsInteractor(RecommendedShowsInteractor.Param(showId, forceReload))
                .collectStatus(recommendedShowsLoadingState, logger, uiMessageManager)
        }

        coroutineScope.launch {
            showDetailsInteractor(ShowDetailsInteractor.Param(showId, forceReload))
                .collectStatus(showDetailsLoadingState, logger, uiMessageManager)
        }

        coroutineScope.launch {
            similarShowsInteractor(SimilarShowsInteractor.Param(showId, forceReload))
                .collectStatus(similarShowsLoadingState, logger, uiMessageManager)
        }

        coroutineScope.launch {
            watchProvidersInteractor(WatchProvidersInteractor.Param(showId, forceReload))
                .collectStatus(watchProvidersLoadingState, logger, uiMessageManager)
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, ShowDetailsPresenter.Factory::class)
public class DefaultShowDetailsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
    ) -> ShowDetailsPresenter,
) : ShowDetailsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        id: Long,
        onBack: () -> Unit,
        onNavigateToShow: (id: Long) -> Unit,
        onNavigateToSeason: (param: ShowSeasonDetailsParam) -> Unit,
        onNavigateToTrailer: (id: Long) -> Unit,
    ): ShowDetailsPresenter = presenter(
        componentContext,
        id,
        onBack,
        onNavigateToShow,
        onNavigateToSeason,
        onNavigateToTrailer,
    )
}
