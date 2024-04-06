package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterPresenterFactory
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

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
) : Navigator, ComponentContext by componentContext {

  private val navigation = StackNavigation<Config>()
  private val coroutineScope = coroutineScope()

  private val screenStack: Value<ChildStack<*, Screen>> =
    childStack(
      source = navigation,
      initialConfiguration = Config.Discover,
      serializer = Config.serializer(),
      handleBackButton = true,
      childFactory = ::createScreen,
    )

  private val _state: MutableStateFlow<ChildStack<*, Screen>> = MutableStateFlow(screenStack.value)

  init {
    screenStack.observe { coroutineScope.launch { _state.emit(it) } }
  }

  override val screenStackFlow: StateFlow<ChildStack<*, Screen>> = _state.asStateFlow()

  override val themeState: StateFlow<ThemeState> =
    datastoreRepository
      .observeTheme()
      .map { theme -> ThemeState(isFetching = false, appTheme = theme) }
      .stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ThemeState(),
      )

  override fun bringToFront(config: Config) {
    navigation.bringToFront(config)
  }

  override fun shouldShowBottomNav(screen: Screen): Boolean {
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
      is Config.Discover ->
        Screen.Discover(
          presenter =
            discoverPresenterFactory(
              componentContext,
              { id -> navigation.pushNew(Config.ShowDetails(id)) },
              { id -> navigation.pushNew(Config.MoreShows(id)) },
            ),
        )
      is Config.SeasonDetails ->
        Screen.SeasonDetails(
          presenter =
            seasonDetailsPresenterFactory(
              componentContext,
              config.param,
              navigation::pop,
            ) { _ ->
              // TODO:: Navigate to episode details
            },
        )
      is Config.ShowDetails ->
        Screen.ShowDetails(
          presenter =
            showDetailsPresenterFactory(
              componentContext,
              config.id,
              navigation::pop,
              { id ->
                /**
                 * Fix crash when user navigates to the same screen with different arguments. This
                 * will push the screen to the on top instead of having deep nested stacks.
                 */
                navigation.navigate {
                  (it + Config.ShowDetails(id)).asReversed().distinct().asReversed()
                }
              },
              { params ->
                navigation.pushNew(
                  Config.SeasonDetails(
                    SeasonDetailsUiParam(
                      showId = params.showId,
                      seasonNumber = params.seasonNumber,
                      seasonId = params.seasonId,
                    ),
                  ),
                )
              },
              { id -> navigation.pushNew(Config.Trailers(id)) },
            ),
        )
      is Config.Trailers ->
        Screen.Trailers(
          presenter =
            trailersPresenterFactory(
              componentContext,
              config.id,
            ),
        )
      is Config.MoreShows ->
        Screen.MoreShows(
          presenter =
            moreShowsPresenterFactory(
              componentContext,
              config.id,
              navigation::pop,
            ) { id ->
              navigation.pushNew(Config.ShowDetails(id))
            },
        )
      Config.Library ->
        Screen.Library(
          presenter =
            libraryPresenterFactory(
              componentContext,
            ) { id ->
              navigation.pushNew(Config.ShowDetails(id))
            },
        )
      Config.Search ->
        Screen.Search(
          presenter =
            searchPresenterFactory(
              componentContext,
              navigation::pop,
            ),
        )
      Config.Settings ->
        Screen.Settings(
          presenter =
            settingsPresenterFactory(
              componentContext,
            ) {
              traktAuthManager.launchWebView()
            },
        )
    }
}
