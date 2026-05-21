package com.thomaskioko.tvmaniac.featureflags.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.domain.featureflags.FeatureFlagRow
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor
import com.thomaskioko.tvmaniac.domain.featureflags.ObserveFeatureFlagRowsInteractor.Param
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.nav.FeatureFlagsRoute
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = FeatureFlagsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class FeatureFlagsPresenter(
    componentContext: ComponentContext,
    private val remoteConfig: FeatureFlagsRemoteConfig,
    private val localStore: FeatureFlagLocalStore,
    private val navigator: Navigator,
    private val dateTimeProvider: DateTimeProvider,
    private val localizer: Localizer,
    private val observeRows: ObserveFeatureFlagRowsInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val params = MutableStateFlow(Param())
    private val lastFetchedAt = MutableStateFlow<String?>(null)

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
            items = result.rows.toItems(),
            searchQuery = result.params.query,
            sort = result.params.sort,
            ascending = result.params.ascending,
            groupByType = result.params.groupByType,
            title = localizer.getString(StringResourceKey.LabelDebugFeatureFlagsTitle),
            searchHint = localizer.getString(StringResourceKey.LabelFeatureFlagsSearchHint),
            resetAllTitle = localizer.getString(StringResourceKey.LabelFeatureFlagsResetAllTitle),
            resetAllSubtitle = localizer.getString(StringResourceKey.LabelFeatureFlagsResetAllDescription),
            forceRefreshTitle = localizer.getString(StringResourceKey.LabelFeatureFlagsForceRefreshTitle),
            forceRefreshSubtitle = fetched
                ?: localizer.getString(StringResourceKey.LabelFeatureFlagsForceRefreshDescription),
            emptyResults = localizer.getString(StringResourceKey.LabelFeatureFlagsEmptyResults),
            resetButtonLabel = localizer.getString(StringResourceKey.LabelFeatureFlagReset),
            moreActionsLabel = localizer.getString(StringResourceKey.LabelFeatureFlagsMoreActions),
            groupByTypeLabel = localizer.getString(StringResourceKey.LabelFeatureFlagsGroupByType),
            noGroupingLabel = localizer.getString(StringResourceKey.LabelFeatureFlagsNoGrouping),
            sortAscendingLabel = localizer.getString(StringResourceKey.LabelFeatureFlagsSortAscending),
            sortDescendingLabel = localizer.getString(StringResourceKey.LabelFeatureFlagsSortDescending),
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
                localStore.set(action.key, action.value)
            }
            is ClearLocal -> coroutineScope.launch {
                localStore.clear(action.key)
            }
            ClearAllLocals -> coroutineScope.launch {
                localStore.clearAll()
            }
            ForceRefresh -> coroutineScope.launch {
                remoteConfig.refresh()
                lastFetchedAt.update {
                    val timestamp = dateTimeProvider.epochToDisplayDateTime(dateTimeProvider.now().toEpochMilliseconds())
                    localizer.getString(StringResourceKey.LabelFeatureFlagsLastFetchedAt, timestamp)
                }
            }
            is SearchQueryChanged -> params.update { it.copy(query = action.query) }
            is SortChanged -> params.update { it.copy(sort = action.sort) }
            DirectionToggled -> params.update { it.copy(ascending = !it.ascending) }
            GroupByTypeToggled -> params.update { it.copy(groupByType = !it.groupByType) }
        }
    }

    private fun ImmutableList<FeatureFlagRow>.toItems(): ImmutableList<FeatureFlagItem> =
        map { row ->
            val sourceLabel = when (row.featureFlagSource) {
                FeatureFlagSource.Firebase -> localizer.getString(StringResourceKey.LabelFeatureFlagSourceFirebase)
                FeatureFlagSource.Local -> localizer.getString(StringResourceKey.LabelFeatureFlagSourceLocal)
            }
            FeatureFlagItem(
                key = row.featureFlag.key,
                title = row.featureFlag.title,
                description = row.featureFlag.description,
                source = sourceLabel,
                value = row.value,
                isLocal = row.featureFlagSource == FeatureFlagSource.Local,
            )
        }.toImmutableList()
}
