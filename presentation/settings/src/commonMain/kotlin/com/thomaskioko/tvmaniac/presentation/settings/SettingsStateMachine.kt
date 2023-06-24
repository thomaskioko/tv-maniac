package com.thomaskioko.tvmaniac.presentation.settings

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
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
                        is StoreReadResponse.NoNewData -> state.noChange()
                        is StoreReadResponse.Loading -> state.mutate {
                            copy(
                                isLoading = true,
                            )
                        }
                        is StoreReadResponse.Data -> state.mutate {
                            copy(
                                isLoading = false,
                                userInfo = UserInfo(
                                    slug = response.requireData().slug,
                                    userName = response.requireData().user_name,
                                    fullName = response.requireData().full_name,
                                    userPicUrl = response.requireData().profile_picture,
                                ),
                            )
                        }

                        is StoreReadResponse.Error.Exception -> state.mutate {
                            copy(
                                isLoading = false,
                                errorMessage = response.error.message,
                            )
                        }

                        is StoreReadResponse.Error.Message -> state.mutate {
                            copy(
                                isLoading = false,
                                errorMessage = response.message,
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
