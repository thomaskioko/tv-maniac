package com.thomaskioko.tvmaniac.presentation.watchlist

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.thomaskioko.tvmaniac.shows.api.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class LibraryScreenModel @Inject constructor(
    private val repository: LibraryRepository,
) : ScreenModel {

    private val _state = MutableStateFlow<LibraryState>(LoadingShows)
    val state = _state.asStateFlow()

    init {
        fetchShowData()
        observeLibraryData()
    }

    fun dispatch(action: LibraryAction) {
        when (action) {
            is ReloadLibrary -> screenModelScope.launch { fetchShowData() }
        }
    }

    private fun fetchShowData() {
        screenModelScope.launch {
            val result = repository.getLibraryShows()
            _state.update {
                LibraryContent(result.entityToLibraryShowList())
            }
        }
    }

    private fun observeLibraryData() {
        screenModelScope.launch {
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
