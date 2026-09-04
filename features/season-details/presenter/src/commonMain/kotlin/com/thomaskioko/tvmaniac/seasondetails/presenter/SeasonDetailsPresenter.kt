package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
import com.thomaskioko.tvmaniac.domain.episode.ShouldPickWatchDateInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.FetchPreviousSeasonsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.FetchPreviousSeasonsParams
import com.thomaskioko.tvmaniac.domain.seasondetails.MarkSeasonUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.MarkSeasonUnwatchedParams
import com.thomaskioko.tvmaniac.domain.seasondetails.MarkSeasonWatchedInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.MarkSeasonWatchedParams
import com.thomaskioko.tvmaniac.domain.seasondetails.ObservableSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.ObserveSeasonWatchProgressInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.ObserveSeasonWatchProgressParams
import com.thomaskioko.tvmaniac.domain.seasondetails.ObserveUnwatchedInPreviousSeasonsInteractor
import com.thomaskioko.tvmaniac.domain.seasondetails.ObserveUnwatchedInPreviousSeasonsParams
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetParam
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.EpisodeSheetRoute
import com.thomaskioko.tvmaniac.espisodedetails.nav.model.ScreenSource
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionParam
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@NavDestination(
    route = SeasonDetailsRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.SCREEN,
)
@AssistedInject
public class SeasonDetailsPresenter internal constructor(
    componentContext: ComponentContext,
    @Assisted private val param: SeasonDetailsUiParam,
    private val navigator: Navigator,
    observableSeasonDetailsInteractor: ObservableSeasonDetailsInteractor,
    private val seasonDetailsInteractor: SeasonDetailsInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val shouldPromptForRatingInteractor: ShouldPromptForRatingInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val markSeasonWatchedInteractor: MarkSeasonWatchedInteractor,
    private val markSeasonUnwatchedInteractor: MarkSeasonUnwatchedInteractor,
    private val fetchPreviousSeasonsInteractor: FetchPreviousSeasonsInteractor,
    observeSeasonWatchProgressInteractor: ObserveSeasonWatchProgressInteractor,
    observeUnwatchedInPreviousSeasonsInteractor: ObserveUnwatchedInPreviousSeasonsInteractor,
    observeRatingInteractor: ObserveRatingInteractor,
    private val shouldPickWatchDateInteractor: ShouldPickWatchDateInteractor,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val seasonDetailsParam: SeasonDetailsParam = SeasonDetailsParam(
        showId = param.showId,
        seasonId = param.seasonId,
        seasonNumber = param.seasonNumber,
    )
    private val seasonDetailsLoadingState = ObservableLoadingCounter()
    private val episodeLoadingState = ObservableLoadingCounter()
    private val checkingPreviousSeasonsLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val _state: MutableStateFlow<SeasonDetailsModel> = MutableStateFlow(SeasonDetailsModel.Empty)

    public val state: StateFlow<SeasonDetailsModel> = combine(
        seasonDetailsLoadingState.observable,
        checkingPreviousSeasonsLoadingState.observable,
        episodeLoadingState.observable,
        observableSeasonDetailsInteractor.flow,
        observeSeasonWatchProgressInteractor.flow,
        observeUnwatchedInPreviousSeasonsInteractor.flow,
        uiMessageManager.message,
        observeRatingInteractor.flow,
        _state,
    ) { seasonDetailsUpdating, checkingPreviousSeasons, episodeUpdating,
        detailsResult, watchProgress, unwatchedInPreviousSeasons, message,
        userRating, currentState,
        ->
        currentState.copy(
            userRating = userRating,
            isSeasonDetailsUpdating = seasonDetailsUpdating,
            isEpisodeUpdating = episodeUpdating,
            seasonId = detailsResult.seasonDetails.seasonId,
            seasonName = detailsResult.seasonDetails.name,
            seasonOverview = detailsResult.seasonDetails.seasonOverview ?: "",
            episodeCount = detailsResult.seasonDetails.episodeCount,
            imageUrl = detailsResult.seasonDetails.imageUrl,
            episodeDetailsList = detailsResult.seasonDetails.episodes.toEpisodes(
                updatingEpisodesId = currentState.updatingEpisodeIds,
            ),
            seasonImages = detailsResult.images.toImageList(),
            seasonCast = detailsResult.cast.toCastList(),
            watchProgress = watchProgress.progressPercentage,
            isCheckingPreviousSeasons = checkingPreviousSeasons,
            isSeasonWatched = watchProgress.isSeasonWatched,
            watchedEpisodeCount = watchProgress.watchedCount,
            hasUnwatchedInPreviousSeasons = unwatchedInPreviousSeasons,
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SeasonDetailsModel.Empty,
    )

    public val stateValue: Value<SeasonDetailsModel> = state.asValue(coroutineScope)

    init {
        observableSeasonDetailsInteractor(seasonDetailsParam)
        observeSeasonWatchProgressInteractor(
            ObserveSeasonWatchProgressParams(
                showId = param.showId,
                seasonNumber = param.seasonNumber,
            ),
        )
        observeUnwatchedInPreviousSeasonsInteractor(
            ObserveUnwatchedInPreviousSeasonsParams(
                showId = param.showId,
                seasonNumber = param.seasonNumber,
            ),
        )
        observeRatingInteractor(ObserveRatingInteractor.Param(RatingEntityType.SEASON, param.seasonId))
        observeSeasonDetails(forceReload = param.forceRefresh)
        prefetchPreviousSeasonsData()
    }

    public fun dispatch(action: SeasonDetailsAction) {
        coroutineScope.launch {
            when (action) {
                is EpisodeClicked -> navigator.navigateTo(
                    EpisodeSheetRoute(EpisodeSheetParam(episodeId = action.id, source = ScreenSource.SEASON_DETAILS)),
                )
                SeasonDetailsBackClicked -> navigator.navigateBack()
                ReloadSeasonDetails -> observeSeasonDetails()
                OnEpisodeHeaderClicked -> updateState { copy(expandEpisodeItems = !expandEpisodeItems) }
                ShowGallery -> updateState { copy(dialogState = SeasonDialogState.Gallery) }
                is MarkSeasonAsWatched -> handleMarkSeasonAsWatched(action.hasUnwatchedInPreviousSeasons)
                MarkSeasonAsUnwatched -> handleMarkSeasonAsUnwatched()
                is MarkEpisodeWatched -> handleMarkEpisodeWatched(action)
                is EpisodeWatchedLongPressed -> navigator.navigateTo(
                    WatchDateSelectionRoute(
                        WatchDateSelectionParam(
                            target = WatchedDateTarget.EPISODE,
                            showId = param.showId,
                            episodeId = action.episodeId,
                            seasonNumber = action.seasonNumber,
                            episodeNumber = action.episodeNumber,
                            isEdit = true,
                        ),
                    ),
                )
                is MarkEpisodeUnwatched -> updateState {
                    copy(
                        dialogState = SeasonDialogState.UnwatchEpisodeConfirmation(
                            primaryChange = WatchChange.UnwatchEpisode(param.showId, action.episodeId),
                        ),
                    )
                }
                is ToggleEpisodeWatched -> handleToggleEpisodeWatched(action.episodeId)
                ToggleSeasonWatched -> handleToggleSeasonWatched()
                DismissDialog -> updateState { copy(dialogState = SeasonDialogState.Hidden) }
                is SeasonDetailsMessageShown -> uiMessageManager.clearMessage(action.id)
                ConfirmDialogAction -> handleConfirmDialogAction()
                SecondaryDialogAction -> handleSecondaryDialogAction()
                SeasonRatingClicked -> navigator.navigateTo(
                    RatingSheetRoute(RatingSheetParam(ratingType = RatingEntityType.SEASON, id = param.seasonId)),
                )
            }
        }
    }

    private suspend fun handleMarkEpisodeWatched(action: MarkEpisodeWatched) {
        val params = MarkEpisodeWatchedParams(
            showId = param.showId,
            episodeId = action.episodeId,
            seasonNumber = action.seasonNumber,
            episodeNumber = action.episodeNumber,
        )
        if (action.hasPreviousUnwatched) {
            updateState {
                copy(
                    dialogState = SeasonDialogState.MarkPreviousEpisodesConfirmation(
                        primaryChange = WatchChange.WatchEpisode(params.copy(markPreviousEpisodes = true)),
                        secondaryChange = WatchChange.WatchEpisode(params),
                    ),
                )
            }
        } else {
            execute(WatchChange.WatchEpisode(params))
        }
    }

    private fun handleMarkSeasonAsUnwatched() {
        updateState {
            copy(
                dialogState = SeasonDialogState.UnwatchSeasonConfirmation(
                    primaryChange = WatchChange.UnwatchSeason(param.showId, param.seasonNumber),
                ),
            )
        }
    }

    private fun handleMarkSeasonAsWatched(hasUnwatchedInPreviousSeasons: Boolean) {
        if (hasUnwatchedInPreviousSeasons) {
            updateState {
                copy(
                    dialogState = SeasonDialogState.MarkPreviousSeasonsConfirmation(
                        primaryChange = WatchChange.WatchSeason(param.showId, param.seasonNumber, markPreviousSeasons = true),
                        secondaryChange = WatchChange.WatchSeason(param.showId, param.seasonNumber, markPreviousSeasons = false),
                    ),
                )
            }
            return
        }
        updateState {
            copy(
                dialogState = SeasonDialogState.WatchSeasonConfirmation(
                    primaryChange = WatchChange.WatchSeason(param.showId, param.seasonNumber),
                ),
            )
        }
    }

    private suspend fun handleToggleEpisodeWatched(episodeId: Long) {
        if (episodeId in state.value.updatingEpisodeIds) return

        val episode = state.value.episodeDetailsList.find { it.id == episodeId } ?: return

        if (episode.isWatched) {
            updateState {
                copy(
                    dialogState = SeasonDialogState.UnwatchEpisodeConfirmation(
                        primaryChange = WatchChange.UnwatchEpisode(param.showId, episodeId),
                    ),
                )
            }
        } else {
            handleMarkEpisodeWatched(
                MarkEpisodeWatched(
                    episodeId = episodeId,
                    seasonNumber = episode.seasonNumber,
                    episodeNumber = episode.episodeNumber,
                    hasPreviousUnwatched = episode.hasPreviousUnwatched,
                ),
            )
        }
    }

    private suspend fun handleToggleSeasonWatched() {
        if (state.value.isSeasonUpdatingProcessing) return

        updateState { copy(isSeasonUpdatingProcessing = true) }

        if (state.value.isSeasonWatched) {
            handleMarkSeasonAsUnwatched()
        } else {
            handleMarkSeasonAsWatched(state.value.hasUnwatchedInPreviousSeasons)
        }

        updateState { copy(isSeasonUpdatingProcessing = false) }
    }

    private suspend fun handleConfirmDialogAction() {
        val change = (state.value.dialogState as? SeasonDialogState.Confirmation)?.primaryChange ?: return
        updateState { copy(dialogState = SeasonDialogState.Hidden) }
        execute(change, fromConfirmation = true)
    }

    private suspend fun handleSecondaryDialogAction() {
        val change = (state.value.dialogState as? SeasonDialogState.Confirmation)?.secondaryChange ?: return
        updateState { copy(dialogState = SeasonDialogState.Hidden) }
        execute(change, fromConfirmation = true)
    }

    private fun watchDateRouteOrNull(change: WatchChange): WatchDateSelectionRoute? = when (change) {
        is WatchChange.WatchEpisode -> WatchDateSelectionRoute(
            WatchDateSelectionParam(
                target = WatchedDateTarget.EPISODE,
                showId = change.params.showId,
                episodeId = change.params.episodeId,
                seasonNumber = change.params.seasonNumber,
                episodeNumber = change.params.episodeNumber,
                markPrevious = change.params.markPreviousEpisodes,
            ),
        )
        is WatchChange.WatchSeason -> WatchDateSelectionRoute(
            WatchDateSelectionParam(
                target = WatchedDateTarget.SEASON,
                showId = change.showId,
                seasonNumber = change.seasonNumber,
                markPrevious = change.markPreviousSeasons,
            ),
        )
        is WatchChange.UnwatchEpisode, is WatchChange.UnwatchSeason -> null
    }

    private suspend fun execute(change: WatchChange, fromConfirmation: Boolean = false) {
        if (fromConfirmation && shouldPickWatchDateInteractor()) {
            val route = watchDateRouteOrNull(change)
            if (route != null) {
                navigator.navigateTo(route)
                return
            }
        }

        val trackedEpisodeId = when (change) {
            is WatchChange.WatchEpisode -> change.params.episodeId
            is WatchChange.UnwatchEpisode -> change.episodeId
            is WatchChange.WatchSeason, is WatchChange.UnwatchSeason -> null
        }
        if (trackedEpisodeId != null) {
            updateState { copy(updatingEpisodeIds = (updatingEpisodeIds + trackedEpisodeId).toPersistentSet()) }
        }

        when (change) {
            is WatchChange.WatchEpisode ->
                markEpisodeWatchedInteractor(change.params)
            is WatchChange.UnwatchEpisode ->
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(change.showId, change.episodeId),
                )
            is WatchChange.WatchSeason ->
                markSeasonWatchedInteractor(
                    MarkSeasonWatchedParams(change.showId, change.seasonNumber, change.markPreviousSeasons),
                )
            is WatchChange.UnwatchSeason ->
                markSeasonUnwatchedInteractor(
                    MarkSeasonUnwatchedParams(change.showId, change.seasonNumber),
                )
        }.collectStatus(episodeLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)

        if (trackedEpisodeId != null) {
            updateState { copy(updatingEpisodeIds = (updatingEpisodeIds - trackedEpisodeId).toPersistentSet()) }
        }

        if (change is WatchChange.WatchEpisode && !change.params.markPreviousEpisodes) {
            val params = change.params
            val shouldPrompt = shouldPromptForRatingInteractor(
                ShouldPromptForRatingInteractor.Param(
                    showId = params.showId,
                    episodeId = params.episodeId,
                ),
            )
            if (shouldPrompt) {
                navigator.navigateTo(
                    RatingSheetRoute(
                        RatingSheetParam(ratingType = RatingEntityType.EPISODE, id = params.episodeId),
                    ),
                )
            }
        }
    }

    private fun updateState(update: SeasonDetailsModel.() -> SeasonDetailsModel) {
        _state.update { it.update() }
    }

    private fun observeSeasonDetails(forceReload: Boolean = false) {
        coroutineScope.launch {
            seasonDetailsInteractor(SeasonDetailsInteractor.Param(seasonDetailsParam, forceReload))
                .collectStatus(seasonDetailsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun prefetchPreviousSeasonsData() {
        if (param.seasonNumber <= 1) return
        coroutineScope.launch {
            fetchPreviousSeasonsInteractor(
                FetchPreviousSeasonsParams(
                    showId = param.showId,
                    seasonNumber = param.seasonNumber,
                ),
            ).collectStatus(checkingPreviousSeasonsLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: SeasonDetailsUiParam): SeasonDetailsPresenter
    }
}
