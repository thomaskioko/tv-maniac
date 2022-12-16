package com.thomaskioko.tvmaniac.profile

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import com.thomaskioko.tvmaniac.traktauth.TraktAuthState
import com.thomaskioko.tvmaniac.traktauth.TraktManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ProfileStateMachine constructor(
    private val traktManager: TraktManager,
    private val repository: TraktRepository
) : FlowReduxStateMachine<ProfileState, ProfileActions>(initialState = ProfileContent.EMPTY) {

    init {
        spec {
            inState<ProfileContent> {

                collectWhileInStateEffect(traktManager.state) { result, _ ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> dispatch(FetchTraktUserProfile)
                        TraktAuthState.LOGGED_OUT -> {}
                    }
                }

                on<FetchTraktUserProfile> { _, state ->
                    fetchUserProfile(state)
                }

                on<FetchUserStatsProfile> { _, state ->
                    fetchTraktStats(state)
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

                on<TraktLogout> { _, state ->
                    traktManager.clearAuth()
                    state.mutate {
                        copy(showTraktDialog = false)
                    }
                }

                on<TraktLogin> { _, state ->
                    state.mutate {
                        copy(showTraktDialog = false)
                    }
                }

                on<RefreshTraktAuthToken> { _, state ->
                    /** Implement token refresh **/
                    state.noChange()
                }
            }
        }
    }

    private suspend fun fetchTraktStats(state: State<ProfileContent>): ChangedState<ProfileState> {
        var nextState: ChangedState<ProfileState> = state.noChange()
        repository.observeStats("me")
            .collect { result ->
                nextState = when (result) {
                    is Resource.Error -> state.override { ProfileStatsError(result.errorMessage) }
                    is Resource.Success -> state.mutate {
                        copy(
                            profileStats = result.data?.let {
                                ProfileStats(
                                    showMonths = it.months,
                                    showDays = it.days,
                                    showHours = it.hours,
                                    collectedShows = it.collected_shows,
                                    episodesWatched = it.episodes_watched
                                )
                            }
                        )
                    }
                }
            }

        return nextState
    }

    private suspend fun fetchUserProfile(state: State<ProfileContent>): ChangedState<ProfileState> {
        var nextState: ChangedState<ProfileState> = state.noChange()

        repository.observeMe("me")
            .collect { result ->
                nextState = when (result) {
                    is Resource.Error -> state.override { ProfileError(result.errorMessage) }
                    is Resource.Success -> {
                        dispatch(FetchUserStatsProfile)
                        state.mutate {
                            copy(
                                traktUser = result.data?.let {
                                    TraktUser(
                                        slug = it.slug,
                                        userName = it.user_name,
                                        fullName = it.full_name,
                                        userPicUrl = it.profile_picture,
                                    )
                                }
                            )
                        }

                    }
                }
            }

        return nextState
    }
}
