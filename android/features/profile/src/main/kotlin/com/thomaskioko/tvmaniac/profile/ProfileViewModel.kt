package com.thomaskioko.tvmaniac.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val stateMachine: ProfileStateMachine,
    private val traktAuthManager: TraktAuthManager,
) : ViewModel(), TraktAuthManager by traktAuthManager {

    val state: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileContent.EMPTY)

    init {
        viewModelScope.launch {
            stateMachine.state.collect { newState ->
                state.value = newState
            }
        }
    }

    fun dispatch(action : ProfileActions) {
        viewModelScope.launch {
            stateMachine.dispatch(action)
        }
    }

}

