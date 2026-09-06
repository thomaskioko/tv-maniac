package com.thomaskioko.tvmaniac.lists.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.CrashReportKeys
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.lists.ObserveUserListsInteractor
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import com.thomaskioko.tvmaniac.lists.nav.ListsRoute
import com.thomaskioko.tvmaniac.lists.presenter.ListsAction.BackClicked
import com.thomaskioko.tvmaniac.lists.presenter.ListsAction.ListClicked
import com.thomaskioko.tvmaniac.lists.presenter.model.UserListItem
import com.thomaskioko.tvmaniac.navigation.Navigator
import dev.zacsweers.metro.Inject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@NavDestination(
    route = ListsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@Inject
public class ListsPresenter internal constructor(
    componentContext: ComponentContext,
    private val navigator: Navigator,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    observeUserListsInteractor: ObserveUserListsInteractor,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val initialState = ListsState(
        title = localizer.getString(StringResourceKey.ProfileLists),
        emptyMessage = localizer.getString(StringResourceKey.LabelWatchlistEmptyList),
    )

    init {
        observeUserListsInteractor(Unit)
    }

    public val state: StateFlow<ListsState> = observeUserListsInteractor.flow
        .map { lists ->
            initialState.copy(
                isLoading = false,
                lists = lists.map { it.toListItem() }.toImmutableList(),
            )
        }
        .catch { error ->
            logger.error(LOG_TAG, "Lists failed", error, mapOf(CrashReportKeys.SOURCE to LISTS_SOURCE_ID))
            emit(initialState.copy(isLoading = false, errorMessage = errorToStringMapper.mapError(error)))
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = initialState,
        )

    public val stateValue: Value<ListsState> = state.asValue(coroutineScope)

    public fun dispatch(action: ListsAction) {
        when (action) {
            is ListClicked -> Unit
            BackClicked -> navigator.navigateBack()
        }
    }

    private fun UserListEntity.toListItem(): UserListItem = UserListItem(
        id = id,
        name = name,
        itemCount = itemCount.toInt(),
        itemCountLabel = localizer.getPlural(
            key = PluralsResourceKey.ShowCount,
            quantity = itemCount.toInt(),
            itemCount.toInt(),
        ),
        posterUrls = posterPaths.toImmutableList(),
    )
}

private const val LOG_TAG = "ListsPresenter"
private const val LISTS_SOURCE_ID = "Lists"
