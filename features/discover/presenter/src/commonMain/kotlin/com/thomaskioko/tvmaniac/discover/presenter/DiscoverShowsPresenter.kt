package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
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
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import dev.zacsweers.metro.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Inject
public class DiscoverShowsPresenter(
    componentContext: ComponentContext,
    private val navigator: DiscoverNavigator,
    private val discoverShowsInteractor: DiscoverShowsInteractor,
    private val followedShowsRepository: FollowedShowsRepository,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    public val presenterInstance: PresenterInstance = instanceKeeper.getOrCreate { PresenterInstance() }

    public val state: StateFlow<DiscoverViewState> = presenterInstance.state

    public val stateValue: Value<DiscoverViewState> = state.asValue(coroutineScope)

    init {
        presenterInstance.init()
    }

    public fun dispatch(action: DiscoverShowAction) {
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
                    .drop(1)
                    .distinctUntilChanged()
                    .filter { it == TraktAuthState.LOGGED_IN }
                    .collect { observeShowData(forceRefresh = true) }
            }
        }

        public fun dispatch(action: DiscoverShowAction) {
            when (action) {
                is ShowClicked -> navigator.showDetails(action.traktId)
                PopularClicked -> navigator.showMoreShows(Category.POPULAR.id)
                TopRatedClicked -> navigator.showMoreShows(Category.TOP_RATED.id)
                TrendingClicked -> navigator.showMoreShows(Category.TRENDING_TODAY.id)
                UpComingClicked -> navigator.showMoreShows(Category.UPCOMING.id)
                UpNextMoreClicked -> navigator.showUpNext()
                RefreshData -> observeShowData(forceRefresh = true)
                is UpdateShowInLibrary -> {
                    coroutineScope.launch {
                        if (action.inLibrary) {
                            unfollowShowInteractor.executeSync(action.traktId)
                        } else {
                            followedShowsRepository.addFollowedShow(action.traktId)
                        }
                    }
                }
                is MessageShown -> {
                    clearMessage(action.id)
                }
                is NextEpisodeClicked -> navigator.showSeason(action.showTraktId, action.seasonId, action.seasonNumber)
                is MarkNextEpisodeWatched -> {
                    coroutineScope.launch {
                        markEpisodeWatchedInteractor(
                            MarkEpisodeWatchedParams(
                                showTraktId = action.showTraktId,
                                episodeId = action.episodeId,
                                seasonNumber = action.seasonNumber,
                                episodeNumber = action.episodeNumber,
                            ),
                        ).collectStatus(upNextActionLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
                    }
                }
                is UnfollowShowFromUpNext -> {
                    coroutineScope.launch {
                        unfollowShowInteractor.executeSync(action.showTraktId)
                    }
                }
                is OpenSeasonFromUpNext -> navigator.showSeason(action.showTraktId, action.seasonId, action.seasonNumber)
                is OpenShowFromUpNext -> navigator.showDetails(action.showTraktId)
                SearchIconClicked -> navigator.showSearch()
                is DiscoverEpisodeLongPressed -> navigator.showEpisodeSheet(action.showTraktId, action.episodeId)
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
                    .collectStatus(genreState, logger, uiMessageManager, "Genres", errorToStringMapper)
            }
            coroutineScope.launch {
                featuredShowsInteractor(forceRefresh)
                    .collectStatus(featuredLoadingState, logger, uiMessageManager, "Featured Shows", errorToStringMapper)
            }

            coroutineScope.launch {
                topRatedShowsInteractor(forceRefresh)
                    .collectStatus(topRatedLoadingState, logger, uiMessageManager, "Top Rated Shows", errorToStringMapper)
            }

            coroutineScope.launch {
                popularShowsInteractor(forceRefresh)
                    .collectStatus(popularLoadingState, logger, uiMessageManager, "Popular Shows", errorToStringMapper)
            }

            coroutineScope.launch {
                trendingShowsInteractor(forceRefresh)
                    .collectStatus(trendingLoadingState, logger, uiMessageManager, "Trending Shows", errorToStringMapper)
            }

            coroutineScope.launch {
                upcomingShowsInteractor(forceRefresh)
                    .collectStatus(upcomingLoadingState, logger, uiMessageManager, "Upcoming Shows", errorToStringMapper)
            }
        }

        override fun onDestroy() {
            coroutineScope.cancel()
        }
    }
}

private fun NextEpisodeWithShow.toUiModel(): NextEpisodeUiModel {
    return NextEpisodeUiModel(
        showTraktId = showTraktId,
        showName = showName,
        imageUrl = stillPath ?: showPoster,
        episodeId = episodeId,
        episodeTitle = episodeName ?: "",
        episodeNumberFormatted = "S${seasonNumber}E$episodeNumber",
        seasonId = seasonId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        runtime = runtime?.let { "$it min" },
        overview = overview ?: "",
        isNew = false,
        rating = rating,
        voteCount = voteCount,
    )
}
