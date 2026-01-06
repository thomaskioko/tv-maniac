package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.episodes.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(ActivityScope::class, DiscoverShowsPresenter::class)
public class DefaultDiscoverShowsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onNavigateToShowDetails: (Long) -> Unit,
    @Assisted private val onNavigateToMore: (Long) -> Unit,
    @Assisted private val onNavigateToEpisode: (showId: Long, episodeId: Long) -> Unit,
    @Assisted private val onNavigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    private val discoverShowsInteractor: DiscoverShowsInteractor,
    private val followedShowsRepository: FollowedShowsRepository,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : DiscoverShowsPresenter, ComponentContext by componentContext {

    override val presenterInstance: PresenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }

    override val state: StateFlow<DiscoverViewState> = presenterInstance.state

    init {
        presenterInstance.init()
    }

    override fun dispatch(action: DiscoverShowAction) {
        presenterInstance.dispatch(action)
    }

    public inner class PresenterInstance : InstanceKeeper.Instance {

        private val featuredLoadingState = ObservableLoadingCounter()
        private val topRatedLoadingState = ObservableLoadingCounter()
        private val popularLoadingState = ObservableLoadingCounter()
        private val trendingLoadingState = ObservableLoadingCounter()
        private val upcomingLoadingState = ObservableLoadingCounter()
        private val genreState = ObservableLoadingCounter()
        private val upNextActionLoadingState = ObservableLoadingCounter()
        private val uiMessageManager = UiMessageManager()

        private val _state: MutableStateFlow<DiscoverViewState> = MutableStateFlow(
            DiscoverViewState.Empty.copy(featuredRefreshing = true),
        )
        public val state: StateFlow<DiscoverViewState> = combine(
            upNextActionLoadingState.observable,
            featuredLoadingState.observable,
            topRatedLoadingState.observable,
            popularLoadingState.observable,
            trendingLoadingState.observable,
            upcomingLoadingState.observable,
            discoverShowsInteractor.flow,
            uiMessageManager.message,
            _state,
        ) {
                upNextUpdating, featuredShowsIsUpdating, topRatedShowsIsUpdating, popularShowsIsUpdating,
                trendingShowsIsUpdating, upComingIsUpdating,
                showData, message, currentState,
            ->

            currentState.copy(
                message = message,
                featuredRefreshing = featuredShowsIsUpdating,
                topRatedRefreshing = topRatedShowsIsUpdating,
                popularRefreshing = popularShowsIsUpdating,
                trendingRefreshing = trendingShowsIsUpdating,
                upcomingRefreshing = upComingIsUpdating,
                upNextRefreshing = upNextUpdating,
                featuredShows = showData.featuredShows.toShowList(),
                topRatedShows = showData.topRatedShows.toShowList(),
                popularShows = showData.popularShows.toShowList(),
                trendingToday = showData.trendingShows.toShowList(),
                upcomingShows = showData.upcomingShows.toShowList(),
                nextEpisodes = showData.nextEpisodes
                    .map { it.toUiModel() }
                    .toImmutableList(),
            )
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = _state.value,
        )

        public fun init() {
            discoverShowsInteractor(Unit)
            observeShowData()
            observeAuthState()
        }

        private fun observeAuthState() {
            coroutineScope.launch {
                traktAuthRepository.state
                    .distinctUntilChanged()
                    .filter { it == TraktAuthState.LOGGED_IN }
                    .collect { observeShowData() }
            }
        }

        public fun dispatch(action: DiscoverShowAction) {
            when (action) {
                is ShowClicked -> onNavigateToShowDetails(action.id)
                PopularClicked -> onNavigateToMore(Category.POPULAR.id)
                TopRatedClicked -> onNavigateToMore(Category.TOP_RATED.id)
                TrendingClicked -> onNavigateToMore(Category.TRENDING_TODAY.id)
                UpComingClicked -> onNavigateToMore(Category.UPCOMING.id)
                RefreshData -> observeShowData(forceRefresh = true)
                is UpdateShowInLibrary -> {
                    coroutineScope.launch {
                        if (action.inLibrary) {
                            followedShowsRepository.removeFollowedShow(action.id)
                        } else {
                            followedShowsRepository.addFollowedShow(action.id)
                        }
                    }
                }
                is MessageShown -> {
                    clearMessage(action.id)
                }
                is NextEpisodeClicked -> onNavigateToEpisode(action.showId, action.episodeId)
                is MarkNextEpisodeWatched -> {
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
                is UnfollowShowFromUpNext -> {
                    coroutineScope.launch {
                        followedShowsRepository.removeFollowedShow(action.showId)
                    }
                }
                is OpenSeasonFromUpNext -> {
                    onNavigateToSeason(action.showId, action.seasonId, action.seasonNumber)
                }
            }
        }

        internal fun clearMessage(id: Long) {
            coroutineScope.launch {
                uiMessageManager.clearMessage(id)
            }
        }

        private fun observeShowData(forceRefresh: Boolean = false) {
            coroutineScope.launch {
                genreShowsInteractor(forceRefresh)
                    .collectStatus(genreState, logger, uiMessageManager, "Genres")
            }
            coroutineScope.launch {
                featuredShowsInteractor(forceRefresh)
                    .collectStatus(featuredLoadingState, logger, uiMessageManager, "Featured Shows")
            }

            coroutineScope.launch {
                topRatedShowsInteractor(forceRefresh)
                    .collectStatus(topRatedLoadingState, logger, uiMessageManager, "Top Rated Shows")
            }

            coroutineScope.launch {
                popularShowsInteractor(forceRefresh)
                    .collectStatus(popularLoadingState, logger, uiMessageManager, "Popular Shows")
            }

            coroutineScope.launch {
                trendingShowsInteractor(forceRefresh)
                    .collectStatus(trendingLoadingState, logger, uiMessageManager, "Trending Shows")
            }

            coroutineScope.launch {
                upcomingShowsInteractor(forceRefresh)
                    .collectStatus(upcomingLoadingState, logger, uiMessageManager, "Upcoming Shows")
            }
        }

        override fun onDestroy() {
            coroutineScope.cancel()
        }
    }
}

@Inject
@ContributesBinding(ActivityScope::class, DiscoverShowsPresenter.Factory::class)
public class DefaultDiscoverPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
        onNavigateToEpisode: (showId: Long, episodeId: Long) -> Unit,
        onNavigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ) -> DiscoverShowsPresenter,
) : DiscoverShowsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToMore: (categoryId: Long) -> Unit,
        onNavigateToEpisode: (showId: Long, episodeId: Long) -> Unit,
        onNavigateToSeason: (showId: Long, seasonId: Long, seasonNumber: Long) -> Unit,
    ): DiscoverShowsPresenter = presenter(componentContext, onNavigateToShowDetails, onNavigateToMore, onNavigateToEpisode, onNavigateToSeason)
}

private fun NextEpisodeWithShow.toUiModel(): NextEpisodeUiModel {
    return NextEpisodeUiModel(
        showId = showId,
        showName = showName,
        showPoster = showPoster,
        episodeId = episodeId,
        episodeTitle = episodeName ?: "",
        episodeNumberFormatted = "S${seasonNumber}E$episodeNumber",
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime?.let { "$it min" },
        stillImage = stillPath,
        overview = overview ?: "",
        isNew = false,
    )
}
