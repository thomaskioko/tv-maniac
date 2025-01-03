package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.pushToFront
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asStateFlow
import com.thomaskioko.tvmaniac.core.base.extensions.componentCoroutineScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, RootPresenter::class)
class DefaultRootPresenter(
  componentContext: ComponentContext,
  private val homePresenterFactory: HomePresenter.Factory,
  private val moreShowsPresenterFactory: MoreShowsPresenterFactory,
  private val showDetailsPresenterFactory: ShowDetailsPresenterFactory,
  private val seasonDetailsPresenterFactory: SeasonDetailsPresenterFactory,
  private val trailersPresenterFactory: TrailersPresenterFactory,
  coroutineScope: CoroutineScope = componentContext.coroutineScope(),
  datastoreRepository: DatastoreRepository,
) : RootPresenter, ComponentContext by componentContext {

  private val navigation = StackNavigation<RootDestinationConfig>()

  override val childStack: StateFlow<ChildStack<*, Child>> = childStack(
    source = navigation,
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

  override fun bringToFront(config: RootDestinationConfig) {
    navigation.bringToFront(config)
  }

  override fun onBackClicked() {
    navigation.pop()
  }

  override fun onBackClicked(toIndex: Int) {
    navigation.popTo(index = toIndex)
  }

  private fun createScreen(config: RootDestinationConfig, componentContext: ComponentContext): Child =
    when (config) {
      is RootDestinationConfig.Home ->
        Child.Home(
          presenter =
            homePresenterFactory.create(
              componentContext,
              { id -> navigation.pushNew(RootDestinationConfig.ShowDetails(id)) },
              { id -> navigation.pushNew(RootDestinationConfig.MoreShows(id)) },
              { id -> navigation.pushNew(RootDestinationConfig.GenreShows(id)) },
            ),
        )
      is RootDestinationConfig.ShowDetails ->
        Child.ShowDetails(
          presenter =
            showDetailsPresenterFactory.create(
              componentContext,
              config.id,
              navigation::pop,
              { id -> navigation.pushToFront(RootDestinationConfig.ShowDetails(id)) },
              { params ->
                navigation.pushNew(
                  RootDestinationConfig.SeasonDetails(
                    SeasonDetailsUiParam(
                      showId = params.showId,
                      seasonNumber = params.seasonNumber,
                      seasonId = params.seasonId,
                    ),
                  ),
                )
              },
              { id -> navigation.pushNew(RootDestinationConfig.Trailers(id)) },
            ),
        )
      is RootDestinationConfig.SeasonDetails ->
        Child.SeasonDetails(
          presenter =
            seasonDetailsPresenterFactory.create(
              componentContext,
              config.param,
              navigation::pop,
            ) { _ ->
              // TODO:: Navigate to episode details
            },
        )
      is RootDestinationConfig.Trailers ->
        Child.Trailers(
          presenter =
            trailersPresenterFactory.create(
              componentContext,
              config.id,
            ),
        )
      is RootDestinationConfig.MoreShows ->
        Child.MoreShows(
          presenter =
            moreShowsPresenterFactory.create(
              componentContext,
              config.id,
              navigation::pop,
            ) { id ->
              navigation.pushNew(RootDestinationConfig.ShowDetails(id))
            },
        )
      is RootDestinationConfig.GenreShows -> Child.GenreShows
    }
}
