package com.thomaskioko.tvmaniac.presentation.settings

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Inject
class SettingsStateMachine(
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
    private val traktAuthRepository: TraktAuthRepository,
) : FlowReduxStateMachine<SettingsState, SettingsActions>(initialState = SettingsContent.EMPTY) {

    init {
        spec {

            inState<SettingsContent> {

                collectWhileInState(datastoreRepository.observeTheme()) { theme, state ->
                    state.mutate {
                        copy(theme = theme)
                    }
                }

                collectWhileInState(traktAuthRepository.state) { result, state ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> {
                            profileRepository.observeProfile("me").collect()
                            state.noChange()
                        }

                        TraktAuthState.LOGGED_OUT -> state.override { SettingsContent.EMPTY }
                    }
                }

                collectWhileInState(profileRepository.observeProfile("me")) { response, state ->
                    when (response) {
                        is StoreReadResponse.Data -> state.mutate {
                            copy(
                                loggedIn = true,
                                traktFullName = response.requireData().full_name,
                                traktUserName = response.requireData().user_name,
                                traktUserPicUrl = response.requireData().profile_picture,
                            )
                        }

                        is StoreReadResponse.Error.Exception -> state.mutate {
                            copy(
                                errorMessage = response.errorMessageOrNull()
                                    ?: "Something went wrong",
                            )
                        }

                        is StoreReadResponse.Error.Message -> state.mutate {
                            copy(
                                errorMessage = response.message,
                            )
                        }

                        is StoreReadResponse.Loading -> state.noChange()
                        is StoreReadResponse.NoNewData -> state.noChange()
                    }
                }

                on<ChangeThemeClicked> { _, state ->
                    state.mutate {
                        copy(showPopup = true)
                    }
                }

                on<ThemeSelected> { action, state ->
                    datastoreRepository.saveTheme(action.theme)
                    state.mutate { copy(showPopup = false) }
                }

                on<DimissThemeClicked> { _, state ->
                    state.mutate {
                        copy(showPopup = false)
                    }
                }

                on<ShowTraktDialog> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = true)
                    }
                }

                on<DismissTraktDialog> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = false)
                    }
                }

                on<TraktLogin> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = !showTraktDialog)
                    }
                }

                on<TraktLogout> { _, state ->
                    traktAuthRepository.clearAuth()
                    profileRepository.clearProfile()
                    state.override { SettingsContent.EMPTY }
                }
            }
        }
    }
}
