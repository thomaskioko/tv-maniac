package com.thomaskioko.tvmaniac.presentation.profile

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.FlowReduxStateMachine
import com.freeletics.flowredux.dsl.State
import com.thomaskioko.tvmaniac.core.networkutil.Either
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
    private val repository: ProfileRepository,
    private val exceptionHandler: ExceptionHandler,
) : FlowReduxStateMachine<ProfileState, ProfileActions>(initialState = ProfileContent.EMPTY) {

    init {
        spec {
            inState<ProfileContent> {

                collectWhileInStateEffect(traktAuthRepository.state) { result, _ ->
                    when (result) {
                        TraktAuthState.LOGGED_IN -> dispatch(FetchTraktUserProfile)
                        TraktAuthState.LOGGED_OUT -> {}
                    }
                }

                collectWhileInState(statsRepository.observeStats("me")) { result, state ->
                    when (result) {
                        is Either.Left -> state.override { ProfileStatsError(result.error.errorMessage) }
                        is Either.Right -> state.mutate {
                            copy(
                                profileStats = result.data?.let {
                                    ProfileStats(
                                        showMonths = it.months,
                                        showDays = it.days,
                                        showHours = it.hours,
                                        collectedShows = it.collected_shows,
                                        episodesWatched = it.episodes_watched,
                                    )
                                },
                            )
                        }
                    }
                }

                on<FetchTraktUserProfile> { _, state ->
                    fetchUserProfile(state)
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
                    traktAuthRepository.clearAuth()
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

    private suspend fun fetchUserProfile(state: State<ProfileContent>): ChangedState<ProfileState> {
        var nextState: ChangedState<ProfileState> = state.noChange()

        repository.observeProfile("me")
            .collect { result ->
                nextState = when (result) {
                    is StoreReadResponse.NoNewData -> state.noChange()
                    is StoreReadResponse.Loading -> state.mutate {
                        copy(isLoading = true)
                    }

                    is StoreReadResponse.Data -> state.mutate {
                        copy(
                            isLoading = true,
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
