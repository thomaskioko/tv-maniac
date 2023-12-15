package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterPresenterFactory
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.scope.ActivityScope
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalDecomposeApi::class)
@Inject
@ActivityScope
class RootNavigationPresenter(
    componentContext: ComponentContext,
    private val discoverPresenterFactory: DiscoverShowsPresenterFactory,
    private val libraryPresenterFactory: LibraryPresenterFactory,
    private val moreShowsPresenterFactory: MoreShowsPresenterFactory,
    private val searchPresenterFactory: SearchPresenterFactory,
    private val settingsPresenterFactory: SettingsPresenterFactory,
    private val showDetailsPresenterFactory: ShowDetailsPresenterPresenterFactory,
    private val seasonDetailsPresenterFactory: SeasonDetailsPresenterFactory,
    private val trailersPresenterFactory: TrailersPresenterFactory,
    private val traktAuthManager: TraktAuthManager,
    datastoreRepository: DatastoreRepository,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val screenStack: Value<ChildStack<*, Screen>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Discover,
            serializer = Config.serializer(),
            handleBackButton = true,
            childFactory = ::createScreen,
        )

    val state: Value<ThemeState> = datastoreRepository.observeTheme()
        .map { theme -> ThemeState(isFetching = false, appTheme = theme) }
        .asValue(initialValue = ThemeState(), lifecycle = lifecycle)

    fun bringToFront(config: Config) {
        navigation.bringToFront(config)
    }

    fun shouldShowBottomNav(screen: Screen): Boolean {
        return when (screen) {
            is Screen.Discover -> true
            is Screen.Search -> true
            is Screen.Library -> true
            is Screen.Settings -> true
            else -> false
        }
    }

    private fun createScreen(config: Config, componentContext: ComponentContext): Screen =
        when (config) {
            is Config.Discover -> Screen.Discover(
                presenter = discoverPresenterFactory(
                    componentContext,
                    { id ->
                        navigation.pushNew(Config.ShowDetails(id))
                    },
                    { id ->
                        navigation.pushNew(Config.MoreShows(id))
                    },
                ),
            )

            is Config.SeasonDetails -> Screen.SeasonDetails(
                presenter = seasonDetailsPresenterFactory(
                    componentContext,
                    config.id,
                    config.title,
                    navigation::pop,
                ) { _ ->
                    // TODO:: Navigate to episode details
                },
            )

            is Config.ShowDetails -> Screen.ShowDetails(
                presenter = showDetailsPresenterFactory(
                    componentContext,
                    config.id,
                    navigation::pop,
                    { id -> navigation.pushNew(Config.ShowDetails(id)) },
                    { id, title -> navigation.pushNew(Config.SeasonDetails(id, title)) },
                    { id -> navigation.pushNew(Config.Trailers(id)) },
                ),
            )

            is Config.Trailers -> Screen.Trailers(
                presenter = trailersPresenterFactory(
                    componentContext,
                    config.id,
                ),
            )

            is Config.MoreShows -> Screen.MoreShows(
                presenter = moreShowsPresenterFactory(
                    componentContext,
                    config.id,
                    navigation::pop,
                ) { id -> navigation.pushNew(Config.ShowDetails(id)) },
            )

            Config.Library -> Screen.Library(
                presenter = libraryPresenterFactory(
                    componentContext,
                ) { id ->
                    navigation.pushNew(Config.ShowDetails(id))
                },
            )

            Config.Search -> Screen.Search(
                presenter = searchPresenterFactory(
                    componentContext,
                    navigation::pop,
                ),
            )

            Config.Settings -> Screen.Settings(
                presenter = settingsPresenterFactory(
                    componentContext,
                ) { traktAuthManager.launchWebView() },
            )
        }

    @Serializable
    sealed interface Config {
        @Serializable
        data object Discover : Config

        @Serializable
        data object Library : Config

        @Serializable
        data object Search : Config

        @Serializable
        data class SeasonDetails(val id: Long, val title: String?) : Config

        @Serializable
        data class ShowDetails(val id: Long) : Config

        @Serializable
        data class MoreShows(val id: Long) : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data class Trailers(val id: Long) : Config
    }
}
