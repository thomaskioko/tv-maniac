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
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootComponent.Child
import com.thomaskioko.tvmaniac.presentation.home.HomeComponentFactory
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsComponentFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsComponentFactory
import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsComponentFactory
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersComponentFactory
import kotlinx.coroutines.CoroutineScope
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
class DefaultRootComponent(
  componentContext: ComponentContext,
  private val homeComponentFactory: HomeComponentFactory,
  private val moreShowsComponentFactory: MoreShowsComponentFactory,
  private val showDetailsComponentFactory: ShowDetailsComponentFactory,
  private val seasonDetailsComponentFactory: SeasonDetailsComponentFactory,
  private val trailersComponentFactory: TrailersComponentFactory,
  private val coroutineScope: CoroutineScope = componentContext.coroutineScope(),
  datastoreRepository: DatastoreRepository,
) : RootComponent, ComponentContext by componentContext {

  private val navigation = StackNavigation<Config>()
  private val childStack: Value<ChildStack<*, Child>> =
    childStack(
      source = navigation,
      key = "RootChildStackKey",
      initialConfiguration = Config.Home,
      serializer = Config.serializer(),
      handleBackButton = true,
      childFactory = ::createScreen,
    )

  private val _state: MutableStateFlow<ChildStack<*, Child>> = MutableStateFlow(childStack.value)

  init {
    childStack.subscribe { coroutineScope.launch { _state.emit(it) } }
  }

  override val stack: StateFlow<ChildStack<*, Child>> = _state.asStateFlow()

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

  override fun onBackClicked() {
    navigation.pop()
  }

  override fun onBackClicked(toIndex: Int) {
    navigation.popTo(index = toIndex)
  }

  private fun createScreen(config: Config, componentContext: ComponentContext): Child =
    when (config) {
      is Config.Home ->
        Child.Home(
          component =
            homeComponentFactory(
              componentContext,
              { id -> navigation.pushNew(Config.ShowDetails(id)) },
              { id -> navigation.pushNew(Config.MoreShows(id)) },
            ),
        )
      is Config.ShowDetails ->
        Child.ShowDetails(
          component =
            showDetailsComponentFactory(
              componentContext,
              config.id,
              navigation::pop,
              { id -> navigation.pushToFront(Config.ShowDetails(id)) },
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
      is Config.SeasonDetails ->
        Child.SeasonDetails(
          component =
            seasonDetailsComponentFactory(
              componentContext,
              config.param,
              navigation::pop,
            ) { _ ->
              // TODO:: Navigate to episode details
            },
        )
      is Config.Trailers ->
        Child.Trailers(
          component =
            trailersComponentFactory(
              componentContext,
              config.id,
            ),
        )
      is Config.MoreShows ->
        Child.MoreShows(
          component =
            moreShowsComponentFactory(
              componentContext,
              config.id,
              navigation::pop,
            ) { id ->
              navigation.pushNew(Config.ShowDetails(id))
            },
        )
    }
}
