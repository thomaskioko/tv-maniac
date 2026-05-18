package com.thomaskioko.tvmaniac.featureflags.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor.Param
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlags
import com.thomaskioko.tvmaniac.featureflags.nav.FeatureFlagsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Instant

@NavDestination(
    route = FeatureFlagsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class FeatureFlagsPresenter(
    componentContext: ComponentContext,
    private val featureFlags: FeatureFlags,
    private val localStore: FeatureFlagLocalStore,
    private val navigator: Navigator,
    private val dateTimeProvider: DateTimeProvider,
    private val observeRows: ObserveFeatureFlagRowsInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val params = MutableStateFlow(Param())
    private val lastFetchedAt = MutableStateFlow<Instant?>(null)

    init {
        params
            .onEach { observeRows(it) }
            .launchIn(coroutineScope)
    }

    public val state: StateFlow<FeatureFlagsState> = combine(
        observeRows.flow,
        lastFetchedAt,
    ) { result, fetched ->
        FeatureFlagsState(
            rows = result.rows,
            searchQuery = result.params.query,
            sort = result.params.sort,
            ascending = result.params.ascending,
            groupByType = result.params.groupByType,
            lastFetchedAt = fetched,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeatureFlagsState.DEFAULT_STATE,
    )

    public val stateValue: Value<FeatureFlagsState> = state.asValue(coroutineScope)

    public fun dispatch(action: FeatureFlagsActions) {
        when (action) {
            BackClicked -> navigator.navigateBack()
            is ToggleFlag -> coroutineScope.launch {
                localStore.set(action.flag, action.value)
            }
            is ClearLocal -> coroutineScope.launch {
                localStore.clear(action.flag)
            }
            ClearAllLocals -> coroutineScope.launch {
                localStore.clearAll()
            }
            ForceRefresh -> coroutineScope.launch {
                featureFlags.refresh()
                lastFetchedAt.update { dateTimeProvider.now() }
            }
            is SearchQueryChanged -> params.update { it.copy(query = action.query) }
            is SortChanged -> params.update { it.copy(sort = action.sort) }
            DirectionToggled -> params.update { it.copy(ascending = !it.ascending) }
            GroupByTypeToggled -> params.update { it.copy(groupByType = !it.groupByType) }
        }
    }
}
