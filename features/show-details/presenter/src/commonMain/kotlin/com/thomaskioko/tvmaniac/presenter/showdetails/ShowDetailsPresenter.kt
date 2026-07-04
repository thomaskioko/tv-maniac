package com.thomaskioko.tvmaniac.presenter.showdetails

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.ShowDetailsCastPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.di.ShowDetailsCastChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.header.di.ShowDetailsHeaderChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.ShowDetailsProvidersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.di.ShowDetailsProvidersChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.di.ShowDetailsSeasonsEpisodesChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.di.ShowDetailsSimilarChildGraph
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.di.ShowDetailsTrailersChildGraph
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@NavDestination(
    route = ShowDetailsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class ShowDetailsPresenter(
    componentContext: ComponentContext,
    @Assisted private val param: ShowDetailsParam,
    headerGraphFactory: ShowDetailsHeaderChildGraph.Factory,
    seasonsEpisodesGraphFactory: ShowDetailsSeasonsEpisodesChildGraph.Factory,
    castGraphFactory: ShowDetailsCastChildGraph.Factory,
    providersGraphFactory: ShowDetailsProvidersChildGraph.Factory,
    trailersGraphFactory: ShowDetailsTrailersChildGraph.Factory,
    similarGraphFactory: ShowDetailsSimilarChildGraph.Factory,
    private val navigator: Navigator,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    public val headerPresenter: ShowDetailsHeaderPresenter =
        headerGraphFactory.createShowDetailsHeaderGraph(childContext(key = "Header"))
            .showDetailsHeaderFactory.create(param.showId, param.forceRefresh)

    public val seasonsEpisodesPresenter: ShowDetailsSeasonsEpisodesPresenter =
        seasonsEpisodesGraphFactory.createShowDetailsSeasonsEpisodesGraph(childContext(key = "SeasonsEpisodes"))
            .showDetailsSeasonsEpisodesFactory.create(param.showId, param.forceRefresh)

    public val castPresenter: ShowDetailsCastPresenter =
        castGraphFactory.createShowDetailsCastGraph(childContext(key = "Cast"))
            .showDetailsCastFactory.create(param.showId, param.forceRefresh)

    public val providersPresenter: ShowDetailsProvidersPresenter =
        providersGraphFactory.createShowDetailsProvidersGraph(childContext(key = "Providers"))
            .showDetailsProvidersFactory.create(param.showId, param.forceRefresh)

    public val trailersPresenter: ShowDetailsTrailersPresenter =
        trailersGraphFactory.createShowDetailsTrailersGraph(childContext(key = "Trailers"))
            .showDetailsTrailersFactory.create(param.showId, param.forceRefresh)

    public val similarPresenter: ShowDetailsSimilarPresenter =
        similarGraphFactory.createShowDetailsSimilarGraph(childContext(key = "Similar"))
            .showDetailsSimilarFactory.create(param.showId, param.forceRefresh)

    public val state: StateFlow<ShowDetailsState> = combine(
        headerPresenter.state,
        castPresenter.state,
        providersPresenter.state,
        trailersPresenter.state,
        similarPresenter.state,
    ) { header, cast, providers, trailers, similar ->
        ShowDetailsState(
            isRefreshing = header.isRefreshing || cast.isRefreshing || providers.isRefreshing ||
                trailers.isRefreshing || similar.isRefreshing,
            message = header.message ?: cast.message ?: providers.message ?: trailers.message ?: similar.message,
        )
    }.combine(seasonsEpisodesPresenter.state) { hostState, seasonsEpisodes ->
        hostState.copy(
            isRefreshing = hostState.isRefreshing || seasonsEpisodes.isRefreshing,
            message = hostState.message ?: seasonsEpisodes.message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ShowDetailsState.Empty,
    )

    public val stateValue: Value<ShowDetailsState> = state.asValue(coroutineScope)

    public fun dispatch(action: ShowDetailsAction) {
        when (action) {
            ShowDetailsBackClicked -> navigator.navigateBack()
            ShowDetailsReload -> {
                headerPresenter.refresh()
                seasonsEpisodesPresenter.refresh()
                castPresenter.refresh()
                providersPresenter.refresh()
                trailersPresenter.refresh()
                similarPresenter.refresh()
            }
            is ShowDetailsMessageShown -> {
                headerPresenter.clearMessage(action.id)
                castPresenter.clearMessage(action.id)
                providersPresenter.clearMessage(action.id)
                trailersPresenter.clearMessage(action.id)
                similarPresenter.clearMessage(action.id)
                seasonsEpisodesPresenter.clearMessage(action.id)
            }
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ShowDetailsParam): ShowDetailsPresenter
    }
}
