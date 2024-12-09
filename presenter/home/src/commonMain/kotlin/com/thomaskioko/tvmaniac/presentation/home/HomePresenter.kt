package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.childStack
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverPresenterFactory
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenterFactory
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthManager
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.collections.plus

@Inject
@SingleIn(ActivityScope::class)
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
) : ComponentContext by componentContext {
  private val navigation = StackNavigation<HomeConfig>()

  val homeChildStack: StateFlow<ChildStack<*, Child>> = childStack(
    source = navigation,
    key = "HomeChildStackKey",
    initialConfiguration = HomeConfig.Discover,
    serializer = HomeConfig.serializer(),
    handleBackButton = true,
    childFactory = ::child,
  ).asStateFlow(componentContext.componentCoroutineScope())

  fun onDiscoverClicked() {
    onTabClicked(HomeConfig.Discover)
  }

  fun onLibraryClicked() {
    onTabClicked(HomeConfig.Library)
  }

  fun onSearchClicked() {
    onTabClicked(HomeConfig.Search)
  }

  fun onSettingsClicked() {
    onTabClicked(HomeConfig.Settings)
  }

  fun onTabClicked(tab: HomeConfig) {
    navigation.switchTab(tab)
  }

  private inline fun <C : Any> StackNavigator<C>.switchTab(configuration: C, crossinline onComplete: () -> Unit = {}) {
    navigate(
      transformer = { stack ->
        val existing = stack.find { it::class == configuration::class }
        if (existing != null) {
          stack.filterNot { it::class == configuration::class } + existing
        } else {
          stack + configuration
        }
      },
      onComplete = { _, _ -> onComplete() },
    )
  }

  private fun child(config: HomeConfig, componentContext: ComponentContext): Child =
    when (config) {
      is HomeConfig.Discover -> {
        Child.Discover(
          presenter =
            discoverPresenterFactory.create(
              componentContext,
              { id -> onShowClicked(id) },
              { id -> onMoreShowClicked(id) },
            ),
        )
      }
      HomeConfig.Library -> {
        Child.Library(
          presenter =
            libraryPresenterFactory.create(
              componentContext,
            ) { id ->
              onShowClicked(id)
            },
        )
      }
      HomeConfig.Search -> {
        Child.Search(
          presenter =
            searchPresenterFactory.create(
              componentContext,
              { id -> onShowClicked(id) },
            ),
        )
      }
      HomeConfig.Settings -> {
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
  sealed interface HomeConfig {
    @Serializable
    data object Discover : HomeConfig

    @Serializable
    data object Library : HomeConfig

    @Serializable
    data object Search : HomeConfig

    @Serializable
    data object Settings : HomeConfig
  }
}
