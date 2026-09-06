package com.thomaskioko.tvmaniac.presentation.showlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.lists.CreateListInteractor
import com.thomaskioko.tvmaniac.domain.lists.ObserveListsForShowInteractor
import com.thomaskioko.tvmaniac.domain.lists.SyncListsInteractor
import com.thomaskioko.tvmaniac.domain.lists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showlist.nav.ShowListParam
import com.thomaskioko.tvmaniac.showlist.nav.ShowListRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = ShowListRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class ShowListPresenter(
    @Assisted private val param: ShowListParam,
    componentContext: ComponentContext,
    observeListsForShowInteractor: ObserveListsForShowInteractor,
    private val navigator: Navigator,
    private val accountManager: AccountManager,
    private val syncListsInteractor: SyncListsInteractor,
    private val createListInteractor: CreateListInteractor,
    private val toggleShowInListInteractor: ToggleShowInListInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val mapper: ShowListMapper,
    private val logger: Logger,
    private val appScopeLauncher: AppScopeLauncher,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val actionLoadingState = ObservableLoadingCounter()
    private val createListState = MutableStateFlow(CreateListUiState())
    private val togglingListIds = MutableStateFlow<PersistentSet<Long>>(persistentSetOf())
    private val labels: ShowListCopy = mapper.resolveCopy()

    public val state: StateFlow<ShowListState> = combine(
        observeListsForShowInteractor.flow,
        uiMessageManager.message,
        createListState,
        togglingListIds,
    ) { lists, message, createUi, togglingIds ->
        ShowListState(
            isLoading = false,
            lists = mapper.toModels(lists, togglingIds),
            showCreateListField = createUi.showField,
            isCreatingList = createUi.isCreating,
            createListName = createUi.name,
            createListError = createUi.error,
            labels = labels,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ShowListState(labels = labels),
    )

    public val stateValue: Value<ShowListState> = state.asValue(coroutineScope)

    init {
        observeListsForShowInteractor(param.showId)
        observeAuthAndSync()
    }

    public fun dispatch(action: ShowListAction) {
        when (action) {
            ShowListAction.ShowCreateListField -> createListState.update {
                it.copy(showField = true, error = null)
            }
            ShowListAction.DismissCreateListField -> createListState.update {
                it.copy(showField = false, name = "", error = null)
            }
            is ShowListAction.UpdateCreateListName -> createListState.update {
                it.copy(name = action.name)
            }
            ShowListAction.CreateListSubmitted -> createList()
            is ShowListAction.ToggleShowInList -> {
                if (action.listId !in togglingListIds.value) {
                    toggleShowInList(action.listId, action.isCurrentlyInList)
                }
            }
            ShowListAction.Dismiss -> navigator.dismissOverlay()
            is ShowListAction.MessageShown -> clearMessage(action.id)
        }
    }

    private fun observeAuthAndSync() {
        coroutineScope.launch {
            accountManager.isConnected
                .filter { it }
                .collect {
                    syncListsInteractor(SyncListsInteractor.Params())
                        .collectStatus(
                            actionLoadingState,
                            logger,
                            uiMessageManager,
                            errorToStringMapper = errorToStringMapper,
                        )
                }
        }
    }

    private fun createList() {
        val name = createListState.value.name
        appScopeLauncher.launch(TAG) {
            createListState.update { it.copy(isCreating = true) }
            createListInteractor(CreateListInteractor.Params(name = name))
                .collectStatus(
                    actionLoadingState,
                    logger,
                    uiMessageManager,
                    errorToStringMapper = errorToStringMapper,
                )
            createListState.update {
                it.copy(isCreating = false, showField = false, name = "")
            }
        }
    }

    private fun toggleShowInList(listId: Long, isCurrentlyInList: Boolean) {
        togglingListIds.update { it.adding(listId) }
        appScopeLauncher.launch(TAG) {
            try {
                toggleShowInListInteractor(
                    ToggleShowInListInteractor.Params(
                        listId = listId,
                        showId = param.showId,
                        isCurrentlyInList = isCurrentlyInList,
                    ),
                ).collectStatus(
                    actionLoadingState,
                    logger,
                    uiMessageManager,
                    errorToStringMapper = errorToStringMapper,
                )
            } finally {
                togglingListIds.update { it.removing(listId) }
            }
        }
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }

    private data class CreateListUiState(
        val showField: Boolean = false,
        val isCreating: Boolean = false,
        val name: String = "",
        val error: String? = null,
    )

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ShowListParam): ShowListPresenter
    }

    private companion object {
        private const val TAG = "ShowListPresenter"
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
