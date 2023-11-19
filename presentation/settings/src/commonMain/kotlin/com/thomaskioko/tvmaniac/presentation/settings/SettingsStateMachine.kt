package com.thomaskioko.tvmaniac.presentation.settings

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.model.Either
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class SettingsStateMachine(
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : FlowReduxStateMachine<SettingsState, SettingsActions>(initialState = Default.EMPTY) {

    init {
        spec {

            inState<SettingsState> {

                collectWhileInState(datastoreRepository.observeTheme()) { theme, state ->
                    state.mutate {
                        when (this) {
                            is Default -> copy(theme = theme)
                            is LoggedInContent -> copy(theme = theme)
                        }
                    }
                }

                on<DimissThemeClicked> { _, state ->
                    state.mutate {
                        when (this) {
                            is Default -> copy(showPopup = false)
                            is LoggedInContent -> copy(showPopup = false)
                        }
                    }
                }

                on<ChangeThemeClicked> { _, state ->
                    state.mutate {
                        when (this) {
                            is Default -> copy(showPopup = true)
                            is LoggedInContent -> copy(showPopup = true)
                        }
                    }
                }

                on<ThemeSelected> { action, state ->
                    datastoreRepository.saveTheme(action.theme)
                    state.mutate {
                        when (this) {
                            is Default -> copy(showPopup = false)
                            is LoggedInContent -> copy(showPopup = false)
                        }
                    }
                }

                on<ShowTraktDialog> { _, state ->
                    state.mutate {
                        when (this) {
                            is Default -> copy(showTraktDialog = true)
                            is LoggedInContent -> copy(showTraktDialog = true)
                        }
                    }
                }

                on<DismissTraktDialog> { _, state ->
                    state.mutate {
                        when (this) {
                            is Default -> copy(showTraktDialog = false)
                            is LoggedInContent -> copy(showTraktDialog = false)
                        }
                    }
                }
            }

            inState<Default> {

                collectWhileInState(datastoreRepository.observeAuthState()) { result, state ->
                    if (result.isAuthorized) {
                        state.override { LoggedInContent.DEFAULT_STATE }
                    } else {
                        state.noChange()
                    }
                }

                on<TraktLoginClicked> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = !showTraktDialog)
                    }
                }
            }

            inState<LoggedInContent> {
                collectWhileInState(traktAuthRepository.observeState()) { result, state ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> state.noChange()
                        TraktAuthState.LOGGED_OUT -> {
                            datastoreRepository.clearAuthState()
                            traktAuthRepository.clearAuth()
                            state.override { Default.EMPTY }
                        }
                    }
                }

                collectWhileInState(profileRepository.observeProfile("me")) { response, state ->
                    when (response) {
                        is Either.Left -> state.mutate {
                            copy(
                                isLoading = false,
                                errorMessage = response.error.errorMessage,
                            )
                        }
                        is Either.Right -> state.mutate {
                            copy(
                                isLoading = false,
                                userInfo = UserInfo(
                                    slug = response.data.slug,
                                    userName = response.data.user_name,
                                    fullName = response.data.full_name,
                                    userPicUrl = response.data.profile_picture,
                                ),
                            )
                        }
                    }
                }

                on<TraktLogoutClicked> { _, state ->
                    traktAuthRepository.clearAuth()
                    profileRepository.clearProfile()

                    state.override {
                        Default.EMPTY
                    }
                }
            }
        }
    }
}
