package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Inject
class DefaultRootPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted val navigator: RootNavigator,
    private val homePresenterFactory: DefaultHomePresenter.Factory,
    private val moreShowsPresenterFactory: MoreShowsPresenter.Factory,
    private val showDetailsPresenterFactory: ShowDetailsPresenter.Factory,
    private val seasonDetailsPresenterFactory: SeasonDetailsPresenter.Factory,
    private val trailersPresenterFactory: TrailersPresenter.Factory,
    datastoreRepository: DatastoreRepository,
) : RootPresenter, ComponentContext by componentContext {

    private val coroutineScope: CoroutineScope = componentContext.coroutineScope()

    override val childStack: StateFlow<ChildStack<*, Child>> = childStack(
        source = navigator.getStackNavigation(),
        key = "RootChildStackKey",
        initialConfiguration = RootDestinationConfig.Home,
        serializer = RootDestinationConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createScreen,
    ).asStateFlow(componentContext.componentCoroutineScope())

    override val themeState: StateFlow<ThemeState> =
        datastoreRepository
            .observeTheme()
            .map { theme -> ThemeState(isFetching = false, appTheme = theme) }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeState(),
            )

    private fun createScreen(
        config: RootDestinationConfig,
        componentContext: ComponentContext,
    ): Child =
        when (config) {
            is RootDestinationConfig.Home ->
                Child.Home(
                    presenter = homePresenterFactory.create(
                        componentContext = componentContext,
                        onShowClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.ShowDetails(
                                    id,
                                ),
                            )
                        },
                        onMoreShowClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.MoreShows(
                                    id,
                                ),
                            )
                        },
                        onShowGenreClicked = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.GenreShows(
                                    id,
                                ),
                            )
                        },
                    ),
                )

            is RootDestinationConfig.ShowDetails ->
                Child.ShowDetails(
                    presenter = showDetailsPresenterFactory.create(
                        componentContext = componentContext,
                        showId = config.id,
                        onBack = navigator::pop,
                        onNavigateToShow = { id ->
                            navigator.pushToFront(
                                RootDestinationConfig.ShowDetails(
                                    id,
                                ),
                            )
                        },
                        onNavigateToSeason = { params ->
                            navigator.pushNew(
                                config = RootDestinationConfig.SeasonDetails(
                                    param = SeasonDetailsUiParam(
                                        showId = params.showId,
                                        seasonNumber = params.seasonNumber,
                                        seasonId = params.seasonId,
                                    ),
                                ),
                            )
                        },
                        onNavigateToTrailer = { id ->
                            navigator.pushNew(
                                RootDestinationConfig.Trailers(
                                    id,
                                ),
                            )
                        },
                    ),
                )

            is RootDestinationConfig.SeasonDetails ->
                Child.SeasonDetails(
                    // TODO:: Navigate to episode details
                    presenter = seasonDetailsPresenterFactory.create(
                        componentContext,
                        param = config.param,
                        onBack = navigator::pop,
                        onNavigateToEpisodeDetails = { _ ->
                            // TODO:: Navigate to episode details
                        },
                    ),
                )

            is RootDestinationConfig.Trailers ->
                Child.Trailers(
                    presenter = trailersPresenterFactory(
                        componentContext = componentContext,
                        traktShowId = config.id,
                    ),
                )

            is RootDestinationConfig.MoreShows ->
                Child.MoreShows(
                    presenter = moreShowsPresenterFactory(
                        componentContext = componentContext,
                        categoryId = config.id,
                        onBack = navigator::pop,
                        onNavigateToShowDetails = { id ->
                            navigator.pushNew(RootDestinationConfig.ShowDetails(id))
                        },
                    ),
                )

            is RootDestinationConfig.GenreShows -> Child.GenreShows
        }

    @AssistedFactory
    fun interface Factory {
        fun create(
            @Assisted componentContext: ComponentContext,
            @Assisted navigator: RootNavigator,
        ): DefaultRootPresenter
    }
}
