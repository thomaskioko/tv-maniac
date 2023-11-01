package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class MainActivityViewModel(
    datastoreRepository: DatastoreRepository,
) : ViewModel() {

    val state: StateFlow<MainActivityUiState> = datastoreRepository.observeTheme()
        .map { theme ->
            MainActivityUiState.DataLoaded(theme = theme)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState.Loading,
        )
}

sealed interface MainActivityUiState {
    data object Loading: MainActivityUiState
    data class DataLoaded(
        val theme: Theme = Theme.SYSTEM,
    ) : MainActivityUiState
}
