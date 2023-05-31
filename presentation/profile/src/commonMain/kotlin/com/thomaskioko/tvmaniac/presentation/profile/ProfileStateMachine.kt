package com.thomaskioko.tvmaniac.presentation.profile

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.profilestats.api.StatsRepository
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
    private val statsRepository: StatsRepository,
    private val profileRepository: ProfileRepository,
    private val exceptionHandler: ExceptionHandler,
) : FlowReduxStateMachine<ProfileState, ProfileActions>(initialState = LoggedOutUser()) {

    init {
        spec {

            inState<LoggedOutUser> {

                collectWhileInStateEffect(traktAuthRepository.state) { result, state ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> dispatch(FetchTraktUserProfile)
                        TraktAuthState.LOGGED_OUT -> {}
                    }
                }

                on<FetchTraktUserProfile> { _, state ->
                    fetchUserProfile(state)
                }
            }

            inState<SignedInProfileContent> {

                collectWhileInStateEffect(traktAuthRepository.state) { result, _ ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> {}
                        TraktAuthState.LOGGED_OUT -> {
                            // Clear data and reset state
                        }
                    }
                }

                collectWhileInState(statsRepository.observeStats("me")) { storeReadResponse, state ->
                    when (storeReadResponse) {
                        is StoreReadResponse.NoNewData -> state.noChange()
                        is StoreReadResponse.Loading -> state.mutate {
                            copy(isLoading = true)
                        }

                        is StoreReadResponse.Data -> state.mutate {
                            copy(
                                profileStats = ProfileStats(
                                    showMonths = storeReadResponse.requireData().months,
                                    showDays = storeReadResponse.requireData().days,
                                    showHours = storeReadResponse.requireData().hours,
                                    collectedShows = storeReadResponse.requireData().collected_shows,
                                    episodesWatched = storeReadResponse.requireData().episodes_watched,
                                ),
                            )
                        }

                        is StoreReadResponse.Error.Exception -> state.override {
                            ProfileStatsError(exceptionHandler.resolveError(storeReadResponse.error))
                        }

                        is StoreReadResponse.Error.Message -> state.override {
                            ProfileStatsError(storeReadResponse.message)
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

                on<RefreshTraktAuthToken> { _, state ->
                    /** Implement token refresh **/
                    state.noChange()
                }
            }
        }
    }

    private suspend fun fetchUserProfile(state: State<LoggedOutUser>): ChangedState<ProfileState> {
        var nextState: ChangedState<ProfileState> = state.noChange()

        profileRepository.observeProfile("me")
            .collect { result ->
                nextState = when (result) {
                    is StoreReadResponse.NoNewData -> state.noChange()
                    is StoreReadResponse.Loading -> state.override {
                        SignedInProfileContent(
                            isLoading = true,
                        )
                    }

                    is StoreReadResponse.Data -> state.override {
                        SignedInProfileContent(
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
