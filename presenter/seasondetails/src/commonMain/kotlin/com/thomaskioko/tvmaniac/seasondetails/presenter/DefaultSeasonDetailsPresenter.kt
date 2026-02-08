package com.thomaskioko.tvmaniac.seasondetails.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.annotations.ActivityScope
import com.thomaskioko.tvmaniac.core.base.extensions.combine
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeUnwatchedParams
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkEpisodeWatchedParams
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
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@ContributesBinding(ActivityScope::class, boundType = SeasonDetailsPresenter::class)
public class DefaultSeasonDetailsPresenter(
    @Assisted componentContext: ComponentContext,
    @Assisted private val param: SeasonDetailsUiParam,
    @Assisted private val onBack: () -> Unit,
    @Assisted private val onEpisodeClick: (id: Long) -> Unit,
    observableSeasonDetailsInteractor: ObservableSeasonDetailsInteractor,
    private val seasonDetailsInteractor: SeasonDetailsInteractor,
    private val markEpisodeWatchedInteractor: MarkEpisodeWatchedInteractor,
    private val markEpisodeUnwatchedInteractor: MarkEpisodeUnwatchedInteractor,
    private val markSeasonWatchedInteractor: MarkSeasonWatchedInteractor,
    private val markSeasonUnwatchedInteractor: MarkSeasonUnwatchedInteractor,
    private val fetchPreviousSeasonsInteractor: FetchPreviousSeasonsInteractor,
    observeSeasonWatchProgressInteractor: ObserveSeasonWatchProgressInteractor,
    observeUnwatchedInPreviousSeasonsInteractor: ObserveUnwatchedInPreviousSeasonsInteractor,
    private val logger: Logger,
) : SeasonDetailsPresenter, ComponentContext by componentContext {

    private val seasonDetailsParam: SeasonDetailsParam = SeasonDetailsParam(
        showTraktId = param.showTraktId,
        seasonId = param.seasonId,
        seasonNumber = param.seasonNumber,
    )
    private val seasonDetailsLoadingState = ObservableLoadingCounter()
    private val episodeLoadingState = ObservableLoadingCounter()
    private val checkingPreviousSeasonsLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val coroutineScope = coroutineScope()
    private val _state: MutableStateFlow<SeasonDetailsModel> = MutableStateFlow(SeasonDetailsModel.Empty)

    override val state: StateFlow<SeasonDetailsModel> = combine(
        seasonDetailsLoadingState.observable,
        checkingPreviousSeasonsLoadingState.observable,
        episodeLoadingState.observable,
        observableSeasonDetailsInteractor.flow,
        observeSeasonWatchProgressInteractor.flow,
        observeUnwatchedInPreviousSeasonsInteractor.flow,
        _state,
    ) { seasonDetailsUpdating, checkingPreviousSeasons, episodeUpdating,
        detailsResult, watchProgress, unwatchedInPreviousSeasons, currentState,
        ->
        currentState.copy(
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
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SeasonDetailsModel.Empty,
    )

    init {
        observableSeasonDetailsInteractor(seasonDetailsParam)
        observeSeasonWatchProgressInteractor(
            ObserveSeasonWatchProgressParams(
                showTraktId = param.showTraktId,
                seasonNumber = param.seasonNumber,
            ),
        )
        observeUnwatchedInPreviousSeasonsInteractor(
            ObserveUnwatchedInPreviousSeasonsParams(
                showTraktId = param.showTraktId,
                seasonNumber = param.seasonNumber,
            ),
        )
        observeSeasonDetails(forceReload = param.forceRefresh)
        prefetchPreviousSeasonsData()
    }

    override fun dispatch(action: SeasonDetailsAction) {
        coroutineScope.launch {
            when (action) {
                is EpisodeClicked -> onEpisodeClick(action.id)
                SeasonDetailsBackClicked -> onBack()
                ReloadSeasonDetails -> observeSeasonDetails()
                OnEpisodeHeaderClicked -> updateState { copy(expandEpisodeItems = !expandEpisodeItems) }
                ShowGallery -> updateState { copy(dialogState = SeasonDialogState.Gallery) }
                is MarkSeasonAsWatched -> handleMarkSeasonAsWatched(action.hasUnwatchedInPreviousSeasons)
                MarkSeasonAsUnwatched -> handleMarkSeasonAsUnwatched()
                is MarkEpisodeWatched -> handleMarkEpisodeWatched(action)
                is MarkEpisodeUnwatched -> updateState {
                    copy(
                        dialogState = SeasonDialogState.UnwatchEpisodeConfirmation(
                            primaryOperation = WatchOperation.MarkEpisodeUnwatched(param.showTraktId, action.episodeId),
                        ),
                    )
                }
                is ToggleEpisodeWatched -> handleToggleEpisodeWatched(action.episodeId)
                ToggleSeasonWatched -> handleToggleSeasonWatched()
                DismissDialog -> updateState { copy(dialogState = SeasonDialogState.Hidden) }
                ConfirmDialogAction -> handleConfirmDialogAction()
                SecondaryDialogAction -> handleSecondaryDialogAction()
            }
        }
    }

    private suspend fun handleMarkEpisodeWatched(action: MarkEpisodeWatched) {
        val params = MarkEpisodeWatchedParams(
            showTraktId = param.showTraktId,
            episodeId = action.episodeId,
            seasonNumber = action.seasonNumber,
            episodeNumber = action.episodeNumber,
        )
        if (action.hasPreviousUnwatched) {
            updateState {
                copy(
                    dialogState = SeasonDialogState.MarkPreviousEpisodesConfirmation(
                        primaryOperation = WatchOperation.MarkEpisodeWatched(params.copy(markPreviousEpisodes = true)),
                        secondaryOperation = WatchOperation.MarkEpisodeWatched(params),
                    ),
                )
            }
        } else {
            execute(WatchOperation.MarkEpisodeWatched(params))
        }
    }

    private fun handleMarkSeasonAsUnwatched() {
        updateState {
            copy(
                dialogState = SeasonDialogState.UnwatchSeasonConfirmation(
                    primaryOperation = WatchOperation.MarkSeasonUnwatched(param.showTraktId, param.seasonNumber),
                ),
            )
        }
    }

    private suspend fun handleMarkSeasonAsWatched(hasUnwatchedInPreviousSeasons: Boolean) {
        if (hasUnwatchedInPreviousSeasons) {
            updateState {
                copy(
                    dialogState = SeasonDialogState.MarkPreviousSeasonsConfirmation(
                        primaryOperation = WatchOperation.MarkSeasonWatched(param.showTraktId, param.seasonNumber, markPreviousSeasons = true),
                        secondaryOperation = WatchOperation.MarkSeasonWatched(param.showTraktId, param.seasonNumber, markPreviousSeasons = false),
                    ),
                )
            }
            return
        }
        execute(WatchOperation.MarkSeasonWatched(param.showTraktId, param.seasonNumber))
    }

    private suspend fun handleToggleEpisodeWatched(episodeId: Long) {
        if (episodeId in state.value.updatingEpisodeIds) return

        updateState { copy(updatingEpisodeIds = (updatingEpisodeIds + episodeId).toPersistentSet()) }

        val episode = state.value.episodeDetailsList.find { it.id == episodeId }
        if (episode == null) {
            updateState { copy(updatingEpisodeIds = (updatingEpisodeIds - episodeId).toPersistentSet()) }
            return
        }

        if (episode.isWatched) {
            updateState {
                copy(
                    updatingEpisodeIds = (updatingEpisodeIds - episodeId).toPersistentSet(),
                    dialogState = SeasonDialogState.UnwatchEpisodeConfirmation(
                        primaryOperation = WatchOperation.MarkEpisodeUnwatched(param.showTraktId, episodeId),
                    ),
                )
            }
        } else {
            updateState { copy(updatingEpisodeIds = (updatingEpisodeIds - episodeId).toPersistentSet()) }
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
        (state.value.dialogState as? SeasonDialogState.Confirmation)
            ?.let { execute(it.primaryOperation) }
    }

    private suspend fun handleSecondaryDialogAction() {
        (state.value.dialogState as? SeasonDialogState.Confirmation)
            ?.secondaryOperation?.let { execute(it) }
    }

    private suspend fun execute(operation: WatchOperation) {
        when (operation) {
            is WatchOperation.MarkEpisodeWatched ->
                markEpisodeWatchedInteractor(operation.params)
            is WatchOperation.MarkEpisodeUnwatched ->
                markEpisodeUnwatchedInteractor(
                    MarkEpisodeUnwatchedParams(operation.showTraktId, operation.episodeId),
                )
            is WatchOperation.MarkSeasonWatched ->
                markSeasonWatchedInteractor(
                    MarkSeasonWatchedParams(operation.showTraktId, operation.seasonNumber, operation.markPreviousSeasons),
                )
            is WatchOperation.MarkSeasonUnwatched ->
                markSeasonUnwatchedInteractor(
                    MarkSeasonUnwatchedParams(operation.showTraktId, operation.seasonNumber),
                )
        }.collectStatus(episodeLoadingState, logger, uiMessageManager)

        updateState { copy(dialogState = SeasonDialogState.Hidden) }
    }

    private fun updateState(update: SeasonDetailsModel.() -> SeasonDetailsModel) {
        _state.update { it.update() }
    }

    private fun observeSeasonDetails(forceReload: Boolean = false) {
        coroutineScope.launch {
            seasonDetailsInteractor(SeasonDetailsInteractor.Param(seasonDetailsParam, forceReload))
                .collectStatus(seasonDetailsLoadingState, logger, uiMessageManager)
        }
    }

    private fun prefetchPreviousSeasonsData() {
        if (param.seasonNumber <= 1) return
        coroutineScope.launch {
            fetchPreviousSeasonsInteractor(
                FetchPreviousSeasonsParams(
                    showTraktId = param.showTraktId,
                    seasonNumber = param.seasonNumber,
                ),
            ).collectStatus(checkingPreviousSeasonsLoadingState, logger, uiMessageManager)
        }
    }
}

@Inject
@SingleIn(ActivityScope::class)
@ContributesBinding(ActivityScope::class, SeasonDetailsPresenter.Factory::class)
public class DefaultSeasonDetailsPresenterFactory(
    private val presenter: (
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (id: Long) -> Unit,
    ) -> SeasonDetailsPresenter,
) : SeasonDetailsPresenter.Factory {
    override fun invoke(
        componentContext: ComponentContext,
        param: SeasonDetailsUiParam,
        onBack: () -> Unit,
        onNavigateToEpisodeDetails: (id: Long) -> Unit,
    ): SeasonDetailsPresenter = presenter(componentContext, param, onBack, onNavigateToEpisodeDetails)
}
