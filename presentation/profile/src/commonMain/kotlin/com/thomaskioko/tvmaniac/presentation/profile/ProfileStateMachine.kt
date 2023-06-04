package com.thomaskioko.tvmaniac.presentation.profile

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.ExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class ProfileStateMachine(
    private val traktAuthRepository: TraktAuthRepository,
    private val profileRepository: ProfileRepository,
    private val exceptionHandler: ExceptionHandler,
) : FlowReduxStateMachine<ProfileState, ProfileActions>(initialState = LoggedOutContent()) {

    init {
        spec {

            inState<LoggedOutContent> {

                collectWhileInState(traktAuthRepository.state) { result, state ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> fetchUserProfile(state)
                        TraktAuthState.LOGGED_OUT -> {
                            traktAuthRepository.clearAuth()
                            state.override { LoggedOutContent() }
                        }
                    }
                }

                on<FetchTraktUserProfile> { _, state ->
                    fetchUserProfile(state)
                }
            }

            inState<SignedInContent> {

                collectWhileInState(traktAuthRepository.state) { result, state ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> state.noChange()
                        TraktAuthState.LOGGED_OUT -> {
                            traktAuthRepository.clearAuth()
                            state.override { LoggedOutContent() }
                        }
                    }
                }

                on<TraktLogout> { _, state ->
                    traktAuthRepository.clearAuth()
                    state.mutate {
                        copy(showLogoutDialog = false)
                    }
                }

                on<TraktLogin> { _, state ->
                    state.mutate {
                        copy(showLogoutDialog = false)
                    }
                }
            }
        }
    }

    private suspend fun fetchUserProfile(state: State<LoggedOutContent>): ChangedState<ProfileState> {
        var nextState: ChangedState<ProfileState> = state.noChange()

        profileRepository.observeProfile("me")
            .collect { result ->
                nextState = when (result) {
                    is StoreReadResponse.NoNewData -> state.noChange()
                    is StoreReadResponse.Loading -> state.override {
                        SignedInContent(
                            isLoading = true,
                        )
                    }

                    is StoreReadResponse.Data -> state.override {
                        SignedInContent(
                            isLoading = false,
                            traktUser = TraktUser(
                                slug = result.requireData().slug,
                                userName = result.requireData().user_name,
                                fullName = result.requireData().full_name,
                                userPicUrl = result.requireData().profile_picture,
                            ),
                        )
                    }

                    is StoreReadResponse.Error.Exception -> state.override {
                        ProfileStatsError(exceptionHandler.resolveError(result.error))
                    }

                    is StoreReadResponse.Error.Message -> state.override {
                        ProfileStatsError(result.message)
                    }
                }
            }
        return nextState
    }
}
