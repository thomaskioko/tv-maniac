package com.thomaskioko.tvmaniac.presentation.profile

import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class ProfileStateMachine(
    private val traktAuthRepository: TraktAuthRepository,
    private val datastoreRepository: DatastoreRepository,
    private val profileRepository: ProfileRepository,
) : FlowReduxStateMachine<ProfileState, ProfileActions>(initialState = LoggedOutContent()) {

    init {
        spec {

            inState<ProfileState> {

                on<ShowTraktDialog> { _, state ->
                    state.mutate {
                        when (this) {
                            is LoggedOutContent -> copy(showTraktDialog = true)
                            is LoggedInContent -> copy(showTraktDialog = true)
                        }
                    }
                }

                on<DismissTraktDialog> { _, state ->
                    state.mutate {
                        when (this) {
                            is LoggedOutContent -> copy(showTraktDialog = true)
                            is LoggedInContent -> copy(showTraktDialog = true)
                        }
                    }
                }
            }

            inState<LoggedOutContent> {

                collectWhileInState(datastoreRepository.observeAuthState()) { result, state ->
                    if (result.isAuthorized) {
                        state.override { LoggedInContent() }
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
                            state.override { LoggedInContent() }
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

                    state.override { LoggedOutContent() }
                }
            }
        }
    }
}
