package com.thomaskioko.tvmaniac.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thomaskioko.tvmaniac.core.util.CoroutineScopeOwner
import com.thomaskioko.tvmaniac.shared.core.ui.Store
import com.thomaskioko.tvmaniac.shared.core.ui.di.DefaultDispatcher
import com.thomaskioko.tvmaniac.trakt.api.ObserveStatsInteractor
import com.thomaskioko.tvmaniac.trakt.api.ObserveTraktUserInteractor
import com.thomaskioko.tvmaniac.trakt.api.StatsParam
import com.thomaskioko.tvmaniac.trakt.api.TraktUiUser
import com.thomaskioko.tvmaniac.traktauth.ObserveTraktAuthStateInteractor
import com.thomaskioko.tvmaniac.traktauth.TraktAuthManager
import com.thomaskioko.tvmaniac.traktauth.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.TraktManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val traktManager: TraktManager,
    private val traktAuthManager: TraktAuthManager,
    private val traktAuthInteractor: ObserveTraktAuthStateInteractor,
    private val observeTraktUserInteractor: ObserveTraktUserInteractor,
    private val observeStatsInteractor: ObserveStatsInteractor,
    @DefaultDispatcher private val ioDispatcher: CoroutineDispatcher
) : Store<ProfileStateContent, ProfileActions, ProfileEffect>, CoroutineScopeOwner, ViewModel(),
    TraktAuthManager by traktAuthManager {

    private val sideEffect = MutableSharedFlow<ProfileEffect>()

    override val state = MutableStateFlow(ProfileStateContent.EMPTY)

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    init {
        viewModelScope.launch {
            traktAuthInteractor.invoke(Unit)
                .collect {

                    val newState = state.value.copy(
                        loggedIn = it == TraktAuthState.LOGGED_IN
                    )
                    state.emit(newState)

                    when (it) {
                        TraktAuthState.LOGGED_IN -> dispatch(ProfileActions.FetchTraktUserProfile)
                        TraktAuthState.LOGGED_OUT -> dispatch(ProfileActions.RefreshTraktAuthToken)
                    }

                }
        }
    }

    override fun observeState(): StateFlow<ProfileStateContent> = state

    override fun observeSideEffect(): Flow<ProfileEffect> = sideEffect

    override fun dispatch(action: ProfileActions) {
        when (action) {
            ProfileActions.DismissTraktDialog -> {
                viewModelScope.launch(context = ioDispatcher) {
                    val newState = state.value.copy(
                        showTraktDialog = false
                    )
                    state.emit(newState)
                }
            }

            ProfileActions.ShowTraktDialog -> {
                viewModelScope.launch(context = ioDispatcher) {
                    val newState = state.value.copy(
                        showTraktDialog = true
                    )
                    state.emit(newState)
                }
            }

            ProfileActions.TraktLogin -> {
                viewModelScope.launch {
                    val newState = state.value.copy(
                        showTraktDialog = false
                    )
                    state.emit(newState)
                }
            }

            ProfileActions.TraktLogout -> logoutOfTrakt()
            ProfileActions.FetchTraktUserProfile -> fetchUserInfo()
            ProfileActions.RefreshTraktAuthToken -> {

            }
        }
    }


    private fun logoutOfTrakt() {
        viewModelScope.launch {
            val newState = state.value.copy(
                showTraktDialog = false
            )
            state.emit(newState)
            traktManager.clearAuth()
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            observeTraktUserInteractor.invoke("me")
                .collect {
                    val newState = state.value.copy(
                        traktUser = ProfileStateContent.TraktUser(
                            userName = it.userName,
                            fullName = it.fullName,
                            userPicUrl = it.profilePicUrl,
                        )
                    )
                    state.emit(newState)

                    fetchUserStats(it)
                }
        }
    }

    private suspend fun fetchUserStats(it: TraktUiUser) {
        observeStatsInteractor.execute(StatsParam(slug = it.slug)) {
            onNext {
                viewModelScope.launch {
                    it?.let {
                        val updatedState = state.value.copy(
                            profileStats = ProfileStateContent.ProfileStats(
                                showMonths = it.showMonths,
                                showDays = it.showDays,
                                showHours = it.showHours,
                                collectedShows = it.collectedShows,
                                episodesWatched = it.episodesWatched
                            )
                        )

                        state.emit(updatedState)
                    }
                }
            }
        }
    }
}

