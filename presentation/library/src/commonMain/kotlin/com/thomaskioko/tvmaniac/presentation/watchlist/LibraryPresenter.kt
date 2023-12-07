package com.thomaskioko.tvmaniac.presentation.watchlist

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import com.thomaskioko.tvmaniac.util.decompose.asValue
import com.thomaskioko.tvmaniac.util.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias LibraryPresenterFactory = (
    ComponentContext,
    navigateToShowDetails: (showDetails: Long) -> Unit,
) -> LibraryPresenter

@Inject
class LibraryPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigateToShowDetails: (id: Long) -> Unit,
    private val repository: LibraryRepository,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val _state = MutableStateFlow<LibraryState>(LoadingShows)
    val state: Value<LibraryState> = _state
        .asValue(initialValue = _state.value, lifecycle = lifecycle)

    init {
        fetchShowData()
        observeLibraryData()
    }

    fun dispatch(action: LibraryAction) {
        when (action) {
            is ReloadLibrary -> coroutineScope.launch { fetchShowData() }
            is LibraryShowClicked -> navigateToShowDetails(action.id)
        }
    }

    private fun fetchShowData() {
        coroutineScope.launch {
            val result = repository.getLibraryShows()
            _state.update {
                LibraryContent(result.entityToLibraryShowList())
            }
        }
    }

    private fun observeLibraryData() {
        coroutineScope.launch {
            repository.observeLibrary()
                .collectLatest { result ->
                    result.fold(
                        { failure -> _state.update { ErrorLoadingShows(failure.errorMessage) } },
                        { success ->
                            _state.update { state ->
                                (state as? LibraryContent)?.copy(
                                    list = success.entityToLibraryShowList(),
                                ) ?: state
                            }
                        },
                    )
                }
        }
    }
}
