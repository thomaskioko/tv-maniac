package com.thomaskioko.tvmaniac.discover.presenter.startwatching

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.discover.nav.scope.DiscoverChildScope
import com.thomaskioko.tvmaniac.discover.presenter.toStartWatchingShowList
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.shows.api.model.Category
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.ChildPresenter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ChildPresenter(scope = DiscoverChildScope::class, parentScope = DiscoverRoot::class)
@Inject
public class DiscoverStartWatchingPresenter(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val observeStartWatchingInteractor: ObserveStartWatchingInteractor,
    private val localizer: Localizer,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    init {
        observeStartWatchingInteractor(Unit)
    }

    public val state: StateFlow<DiscoverStartWatchingState> = observeStartWatchingInteractor.flow
        .map { shows ->
            DiscoverStartWatchingState(
                startWatchingShows = shows.toStartWatchingShowList(),
                startWatchingTitle = localizer.getString(StringResourceKey.LabelStartWatching),
            )
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DiscoverStartWatchingState(
                startWatchingTitle = localizer.getString(StringResourceKey.LabelStartWatching),
            ),
        )

    public val stateValue: Value<DiscoverStartWatchingState> = state.asValue(coroutineScope)

    public fun dispatch(action: DiscoverStartWatchingAction) {
        when (action) {
            is StartWatchingItemClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.showId)))
            StartWatchingMoreClicked -> navigator.navigateTo(MoreShowsRoute(Category.START_WATCHING.id))
        }
    }
}
