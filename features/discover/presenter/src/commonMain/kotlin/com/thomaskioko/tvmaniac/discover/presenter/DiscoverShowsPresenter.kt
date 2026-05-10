package com.thomaskioko.tvmaniac.discover.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.featuredshows.api.interactor.FeaturedShowsInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsInteractor
import com.thomaskioko.tvmaniac.data.upcomingshows.api.UpcomingShowsInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsInteractor
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.domain.discover.DiscoverShowsInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.followedshows.UnfollowShowInteractor
import com.thomaskioko.tvmaniac.domain.genre.GenreShowsInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.FollowShowInteractor
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import com.thomaskioko.tvmaniac.search.nav.SearchRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.Category
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsInteractor
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.util.api.SyncError
import com.thomaskioko.tvmaniac.util.api.SyncErrorChannel
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
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
@NavDestination(
    route = DiscoverRoot::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.TAB_ROOT,
)
public class DiscoverShowsPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val discoverShowsInteractor: DiscoverShowsInteractor,
    private val followShowInteractor: FollowShowInteractor,
    private val unfollowShowInteractor: UnfollowShowInteractor,
    private val featuredShowsInteractor: FeaturedShowsInteractor,
    private val topRatedShowsInteractor: TopRatedShowsInteractor,
    private val popularShowsInteractor: PopularShowsInteractor,
    private val trendingShowsInteractor: TrendingShowsInteractor,
    private val upcomingShowsInteractor: UpcomingShowsInteractor,
    private val genreShowsInteractor: GenreShowsInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val syncErrorChannel: SyncErrorChannel,
    private val localizer: Localizer,
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
            DiscoverViewState.Empty,
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

            val isUpdating = featuredShowsIsUpdating || topRatedShowsIsUpdating || popularShowsIsUpdating ||
                trendingShowsIsUpdating || upComingIsUpdating
            val hasData = showData.featuredShows.isNotEmpty() || showData.topRatedShows.isNotEmpty() ||
                showData.popularShows.isNotEmpty() || showData.trendingShows.isNotEmpty() ||
                showData.upcomingShows.isNotEmpty()
            val isInitial = currentState.isInitial && !isUpdating && !hasData && message == null

            currentState.copy(
                isInitial = isInitial,
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
            observeSyncErrors()
        }

        // TODO:: Move to root presenter
        private fun observeSyncErrors() {
            coroutineScope.launch {
                syncErrorChannel.errors
                    .filter { it is SyncError.MarkWatchedFailed }
                    .collect {
                        uiMessageManager.emitMessage(
                            UiMessage(message = localizer.getString(StringResourceKey.SyncFailedWillRetry)),
                        )
                    }
            }
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
                is ShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(id = action.traktId)))
                PopularClicked -> navigator.navigateTo(MoreShowsRoute(Category.POPULAR.id))
                TopRatedClicked -> navigator.navigateTo(MoreShowsRoute(Category.TOP_RATED.id))
                TrendingClicked -> navigator.navigateTo(MoreShowsRoute(Category.TRENDING_TODAY.id))
                UpComingClicked -> navigator.navigateTo(MoreShowsRoute(Category.UPCOMING.id))
                UpNextMoreClicked -> navigator.switchBackStack(ProgressRoot)
                RefreshData -> observeShowData(forceRefresh = true)
                is UpdateShowInLibrary -> {
                    coroutineScope.launch {
                        if (action.inLibrary) {
                            unfollowShowInteractor.executeSync(action.traktId)
                        } else {
                            followShowInteractor.executeSync(
                                FollowShowInteractor.Param(traktId = action.traktId),
                            )
                        }
                    }
                }
                is MessageShown -> {
                    clearMessage(action.id)
                }
                is NextEpisodeClicked -> navigator.navigateTo(
                    SeasonDetailsRoute(
                        SeasonDetailsUiParam(
                            showTraktId = action.showTraktId,
                            seasonId = action.seasonId,
                            seasonNumber = action.seasonNumber,
                        ),
                    ),
                )
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
                is OpenSeasonFromUpNext -> navigator.navigateTo(
                    SeasonDetailsRoute(
                        SeasonDetailsUiParam(
                            showTraktId = action.showTraktId,
                            seasonId = action.seasonId,
                            seasonNumber = action.seasonNumber,
                        ),
                    ),
                )
                is OpenShowFromUpNext -> navigator.navigateTo(
                    ShowDetailsRoute(ShowDetailsParam(id = action.showTraktId)),
                )
                SearchIconClicked -> navigator.navigateTo(SearchRoute)
                is DiscoverEpisodeLongPressed -> navigator.navigateTo(
                    EpisodeSheetRoute(EpisodeSheetParam(episodeId = action.episodeId, source = ScreenSource.DISCOVER)),
                )
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
