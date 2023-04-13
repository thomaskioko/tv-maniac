package com.thomaskioko.tvmaniac

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.networkutil.ConnectionState
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class MainActivityViewModel(
    datastoreRepository: DatastoreRepository,
    observeNetwork: NetworkRepository
) : ViewModel() {

    val state: StateFlow<MainState> = combine(
        observeNetwork.observeConnectionState(),
        datastoreRepository.observeTheme()
    ) { connectionState, theme ->

        MainState(
            theme = theme,
            connectionState = connectionState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MainState(),
    )

}

data class MainState(
    val theme: Theme = Theme.SYSTEM,
    val connectionState: ConnectionState = ConnectionState.ConnectionAvailable
)