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

    val state: StateFlow<MainState> = datastoreRepository.observeTheme()
        .map { theme ->
            MainState(
                theme = theme,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MainState(),
        )
}

data class MainState(
    val theme: Theme = Theme.SYSTEM,
)
