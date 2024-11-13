package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class HomePresenterFactory(
  val create: (
    componentContext: ComponentContext,
    onShowClicked: (id: Long) -> Unit,
    onMoreShowClicked: (id: Long) -> Unit,
  ) -> HomePresenter,
)

@Inject
class HomePresenter(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onShowClicked: (id: Long) -> Unit,
  @Assisted private val onMoreShowClicked: (id: Long) -> Unit,
  private val discoverPresenterFactory: DiscoverPresenterFactory,
  private val libraryPresenterFactory: LibraryPresenterFactory,
  private val searchPresenterFactory: SearchPresenterFactory,
  private val settingsPresenterFactory: SettingsPresenterFactory,
  private val traktAuthManager: TraktAuthManager,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
) : ComponentContext by componentContext {
  private val navigation = StackNavigation<Config>()

  private val screenStack: Value<ChildStack<*, Child>> =
    childStack(
      source = navigation,
      key = "HomeChildStackKey",
      initialConfiguration = Config.Discover,
      serializer = Config.serializer(),
      handleBackButton = true,
      childFactory = ::child,
    )

  private val _state: MutableStateFlow<ChildStack<*, Child>> = MutableStateFlow(screenStack.value)

  init {
    screenStack.subscribe { coroutineScope.launch { _state.emit(it) } }
  }

  val stack: StateFlow<ChildStack<*, Child>> = _state.asStateFlow()

  fun onDiscoverClicked() {
    navigation.bringToFront(Config.Discover)
  }

  fun onLibraryClicked() {
    navigation.bringToFront(Config.Library)
  }

  fun onSearchClicked() {
    navigation.bringToFront(Config.Search)
  }

  fun onSettingsClicked() {
    navigation.bringToFront(Config.Settings)
  }

  private fun child(config: Config, componentContext: ComponentContext): Child =
    when (config) {
      is Config.Discover -> {
        Child.Discover(
          presenter =
            discoverPresenterFactory.create(
              componentContext,
              { id -> onShowClicked(id) },
              { id -> onMoreShowClicked(id) },
            ),
        )
      }
      Config.Library -> {
        Child.Library(
          presenter =
            libraryPresenterFactory.create(
              componentContext,
            ) { id ->
              onShowClicked(id)
            },
        )
      }
      Config.Search -> {
        Child.Search(
          presenter =
            searchPresenterFactory.create(
              componentContext,
              { id -> onShowClicked(id) }
            ),
        )
      }
      Config.Settings -> {
        Child.Settings(
          presenter =
            settingsPresenterFactory.create(
              componentContext,
            ) {
              traktAuthManager.launchWebView()
            },
        )
      }
    }

  sealed interface Child {
    class Discover(val presenter: DiscoverShowsPresenter) : Child

    class Library(val presenter: LibraryPresenter) : Child

    class Search(val presenter: SearchShowsPresenter) : Child

    class Settings(val presenter: SettingsPresenter) : Child
  }

  @Serializable
  sealed interface Config {
    @Serializable data object Discover : Config

    @Serializable data object Library : Config

    @Serializable data object Search : Config

    @Serializable data object Settings : Config
  }
}
