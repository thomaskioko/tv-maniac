package com.thomaskioko.tvmaniac.lists.presenter

import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import androidx.paging.map
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.InvokeError
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.lists.DeleteListInteractor
import com.thomaskioko.tvmaniac.domain.lists.FetchMissingListShowDetailsInteractor
import com.thomaskioko.tvmaniac.domain.lists.RenameListInteractor
import com.thomaskioko.tvmaniac.domain.lists.SyncListsInteractor
import com.thomaskioko.tvmaniac.domain.lists.ToggleShowInListInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.ListShowItem
import com.thomaskioko.tvmaniac.lists.nav.ListDetailRoute
import com.thomaskioko.tvmaniac.lists.nav.model.ListDetailParam
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.BackClicked
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.DeleteConfirmed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.DeleteDismissed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.DeleteRequested
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.DismissErrorMessage
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RefreshList
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RemoveConfirmed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RemoveDismissed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RemoveRequested
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RenameConfirmed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RenameDismissed
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RenameNameChanged
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RenameRequested
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.RetryLoadMore
import com.thomaskioko.tvmaniac.lists.presenter.ListDetailAction.ShowClicked
import com.thomaskioko.tvmaniac.lists.presenter.model.DeleteConfirmation
import com.thomaskioko.tvmaniac.lists.presenter.model.ListShow
import com.thomaskioko.tvmaniac.lists.presenter.model.RemoveConfirmation
import com.thomaskioko.tvmaniac.lists.presenter.model.RenameDialog
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = ListDetailRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class ListDetailPresenter(
    componentContext: ComponentContext,
    @Assisted private val param: ListDetailParam,
    private val navigator: Navigator,
    private val localizer: Localizer,
    private val listRepository: ListRepository,
    private val toggleShowInListInteractor: ToggleShowInListInteractor,
    private val fetchMissingListShowDetailsInteractor: FetchMissingListShowDetailsInteractor,
    private val syncListsInteractor: SyncListsInteractor,
    private val renameListInteractor: RenameListInteractor,
    private val deleteListInteractor: DeleteListInteractor,
    private val accountManager: AccountManager,
    private val activeProviderFeatures: () -> ProviderFeatures,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val missingDetailsState = ObservableLoadingCounter()

    private val _state = MutableStateFlow(
        ListDetailState(
            title = param.name,
            emptyMessage = localizer.getString(StringResourceKey.ListDetailEmpty),
            renameLabel = localizer.getString(StringResourceKey.ListDetailMenuRename),
            deleteLabel = localizer.getString(StringResourceKey.ListDetailMenuDelete),
        ),
    )

    private val showsPagingDataPresenter = object : PagingDataPresenter<ListShow>() {
        override suspend fun presentPagingDataEvent(event: PagingDataEvent<ListShow>) {
            updateItemsFromSnapshot()
        }
    }

    init {
        observeTitle()
        observeShows()
        observeLoadStates()
        observeRefreshAvailability()
        fetchMissingDetails()
    }

    public val state: StateFlow<ListDetailState> = _state.asStateFlow()

    public val stateValue: Value<ListDetailState> = state.asValue(coroutineScope)

    public fun dispatch(action: ListDetailAction) {
        when (action) {
            is ShowClicked -> navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(showId = action.tmdbId)))
            is RemoveRequested -> requestRemoval(action.tmdbId)
            RemoveConfirmed -> confirmRemoval()
            RemoveDismissed -> _state.update { it.copy(removeConfirmation = null) }
            RenameRequested -> requestRename()
            is RenameNameChanged -> updateRenameName(action.name)
            RenameConfirmed -> confirmRename()
            RenameDismissed -> _state.update { it.copy(renameDialog = null) }
            DeleteRequested -> requestDelete()
            DeleteConfirmed -> confirmDelete()
            DeleteDismissed -> _state.update { it.copy(deleteConfirmation = null) }
            RefreshList -> refreshList()
            RetryLoadMore -> showsPagingDataPresenter.retry()
            DismissErrorMessage -> _state.update { it.copy(errorMessage = null) }
            BackClicked -> navigator.navigateBack()
        }
    }

    public fun onItemVisible(index: Int) {
        showsPagingDataPresenter[index]
    }

    public fun loadMore() {
        val index = showsPagingDataPresenter.size - 1
        showsPagingDataPresenter[index]
    }

    private fun observeTitle() {
        coroutineScope.launch {
            listRepository.observeLists()
                .map { lists -> lists.firstOrNull { it.id == param.listId }?.name ?: param.name }
                .distinctUntilChanged()
                .collect { name -> _state.update { it.copy(title = name) } }
        }
    }

    private fun observeShows() {
        coroutineScope.launch {
            val pagingList: Flow<PagingData<ListShow>> = listRepository
                .observePagedListShows(param.listId)
                .map { pagingData -> pagingData.map { it.toListShow() } }
                .cachedIn(coroutineScope)

            _state.update { it.copy(pagingDataFlow = pagingList) }

            pagingList.collectLatest { showsPagingDataPresenter.collectFrom(it) }
        }
    }

    private fun observeLoadStates() {
        coroutineScope.launch {
            showsPagingDataPresenter.loadStateFlow.collectLatest { loadStates ->
                loadStates ?: return@collectLatest
                _state.update {
                    it.copy(
                        isRefreshLoading = loadStates.refresh is LoadState.Loading,
                        isAppendLoading = loadStates.append is LoadState.Loading,
                        appendError = (loadStates.append as? LoadState.Error)?.let { error -> errorToStringMapper.mapError(error.error) },
                        errorMessage = (loadStates.refresh as? LoadState.Error)?.let { error -> errorToStringMapper.mapError(error.error) }
                            ?: it.errorMessage,
                    )
                }
            }
        }
    }

    private fun observeRefreshAvailability() {
        coroutineScope.launch {
            accountManager.isConnected.collect { connected ->
                _state.update { it.copy(canRefresh = connected && activeProviderFeatures().supportsLists) }
            }
        }
    }

    private fun refreshList() {
        if (!_state.value.canRefresh || _state.value.isRefreshing) return
        coroutineScope.launch {
            syncListsInteractor(SyncListsInteractor.Params(forceRefresh = true)).collect { status ->
                when (status) {
                    InvokeStarted -> _state.update { it.copy(isRefreshing = true) }
                    InvokeSuccess -> _state.update { it.copy(isRefreshing = false) }
                    is InvokeError -> {
                        logger.error(LOG_TAG, "Refreshing the list failed", status.throwable)
                        _state.update { it.copy(isRefreshing = false, errorMessage = errorToStringMapper.mapError(status.throwable)) }
                    }
                }
            }
        }
    }

    private fun fetchMissingDetails() {
        coroutineScope.launch {
            fetchMissingListShowDetailsInteractor(FetchMissingListShowDetailsInteractor.Params(listId = param.listId))
                .collectStatus(missingDetailsState, logger)
        }
    }

    private fun requestRemoval(tmdbId: Long) {
        val show = _state.value.items.firstOrNull { it.tmdbId == tmdbId } ?: return
        _state.update {
            it.copy(
                removeConfirmation = RemoveConfirmation(
                    tmdbId = show.tmdbId,
                    title = localizer.getString(StringResourceKey.ListDetailRemoveTitle),
                    message = localizer.getString(StringResourceKey.ListDetailRemoveMessage, show.title, param.name),
                    confirmLabel = localizer.getString(StringResourceKey.ListDetailRemoveButton),
                ),
            )
        }
    }

    private fun confirmRemoval() {
        val confirmation = _state.value.removeConfirmation ?: return
        _state.update { it.copy(removeConfirmation = null) }
        coroutineScope.launch {
            toggleShowInListInteractor(
                ToggleShowInListInteractor.Params(
                    listId = param.listId,
                    showId = confirmation.tmdbId,
                    isCurrentlyInList = true,
                ),
            ).collect { status ->
                if (status is InvokeError) {
                    logger.error(LOG_TAG, "Removing a show from the list failed", status.throwable)
                    _state.update { it.copy(errorMessage = errorToStringMapper.mapError(status.throwable)) }
                }
            }
        }
    }

    private fun requestRename() {
        _state.update {
            it.copy(
                renameDialog = RenameDialog(
                    title = localizer.getString(StringResourceKey.ListDetailRenameTitle),
                    name = it.title,
                    canSave = false,
                    saveLabel = localizer.getString(StringResourceKey.LabelSave),
                ),
            )
        }
    }

    private fun updateRenameName(name: String) {
        _state.update {
            val dialog = it.renameDialog ?: return@update it
            val trimmed = name.trim()
            it.copy(renameDialog = dialog.copy(name = name, canSave = trimmed.isNotEmpty() && trimmed != it.title))
        }
    }

    private fun confirmRename() {
        val dialog = _state.value.renameDialog ?: return
        if (!dialog.canSave || dialog.isSaving) return
        coroutineScope.launch {
            renameListInteractor(RenameListInteractor.Params(listId = param.listId, name = dialog.name)).collect { status ->
                when (status) {
                    InvokeStarted -> _state.update { it.copy(renameDialog = it.renameDialog?.copy(isSaving = true)) }
                    InvokeSuccess -> _state.update { it.copy(renameDialog = null) }
                    is InvokeError -> {
                        logger.error(LOG_TAG, "Renaming the list failed", status.throwable)
                        _state.update { it.copy(renameDialog = null, errorMessage = errorToStringMapper.mapError(status.throwable)) }
                    }
                }
            }
        }
    }

    private fun requestDelete() {
        _state.update {
            it.copy(
                deleteConfirmation = DeleteConfirmation(
                    title = localizer.getString(StringResourceKey.ListDetailDeleteTitle),
                    message = localizer.getString(StringResourceKey.ListDetailDeleteMessage, it.title),
                    confirmLabel = localizer.getString(StringResourceKey.ListDetailDeleteButton),
                ),
            )
        }
    }

    private fun confirmDelete() {
        if (_state.value.deleteConfirmation == null) return
        _state.update { it.copy(deleteConfirmation = null) }
        coroutineScope.launch {
            deleteListInteractor(DeleteListInteractor.Params(listId = param.listId)).collect { status ->
                when (status) {
                    InvokeStarted -> Unit
                    InvokeSuccess -> navigator.navigateBack()
                    is InvokeError -> {
                        logger.error(LOG_TAG, "Deleting the list failed", status.throwable)
                        _state.update { it.copy(errorMessage = errorToStringMapper.mapError(status.throwable)) }
                    }
                }
            }
        }
    }

    private fun updateItemsFromSnapshot() {
        val newItems = showsPagingDataPresenter.snapshot().filterNotNull().toImmutableList()
        _state.update { current ->
            when {
                current.items == newItems -> current
                newItems.size < current.items.size -> {
                    if (newItems.isNotEmpty()) {
                        showsPagingDataPresenter[newItems.size - 1]
                    }
                    current
                }
                else -> current.copy(items = newItems)
            }
        }
    }

    private fun ListShowItem.toListShow(): ListShow = ListShow(
        tmdbId = tmdbId,
        title = name,
        posterUrl = posterPath,
    )

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: ListDetailParam): ListDetailPresenter
    }
}

private const val LOG_TAG = "ListDetailPresenter"
