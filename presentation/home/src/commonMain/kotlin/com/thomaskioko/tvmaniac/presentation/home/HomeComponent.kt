package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsComponent
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsComponentFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchComponent
import com.thomaskioko.tvmaniac.presentation.search.SearchComponentFactory
import com.thomaskioko.tvmaniac.presentation.settings.SettingsComponent
import com.thomaskioko.tvmaniac.presentation.settings.SettingsComponentFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryComponent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryComponentFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias HomeComponentFactory =
  (
    ComponentContext,
    onShowClicked: (id: Long) -> Unit,
    onMoreShowClicked: (id: Long) -> Unit,
  ) -> HomeComponent

@Inject
class HomeComponent(
  @Assisted componentContext: ComponentContext,
  @Assisted private val onShowClicked: (id: Long) -> Unit,
  @Assisted private val onMoreShowClicked: (id: Long) -> Unit,
  private val discoverComponentFactory: DiscoverShowsComponentFactory,
  private val libraryComponentFactory: LibraryComponentFactory,
  private val searchComponentFactory: SearchComponentFactory,
  private val settingsComponentFactory: SettingsComponentFactory,
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
          component =
            discoverComponentFactory(
              componentContext,
              { id -> onShowClicked(id) },
              { id -> onMoreShowClicked(id) },
            ),
        )
      }
      Config.Library -> {
        Child.Library(
          component =
            libraryComponentFactory(
              componentContext,
            ) { id ->
              onShowClicked(id)
            },
        )
      }
      Config.Search -> {
        Child.Search(
          component =
            searchComponentFactory(
              componentContext,
              navigation::pop,
            ),
        )
      }
      Config.Settings -> {
        Child.Settings(
          component =
            settingsComponentFactory(
              componentContext,
            ) {
              traktAuthManager.launchWebView()
            },
        )
      }
    }

  sealed interface Child {
    class Discover(val component: DiscoverShowsComponent) : Child

    class Library(val component: LibraryComponent) : Child

    class Search(val component: SearchComponent) : Child

    class Settings(val component: SettingsComponent) : Child
  }

  @Serializable
  sealed interface Config {
    @Serializable data object Discover : Config

    @Serializable data object Library : Config

    @Serializable data object Search : Config

    @Serializable data object Settings : Config
  }
}
