package com.thomaskioko.tvmaniac.ratingsheet.presenter

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.core.base.coroutines.AppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ObserveRatingTargetInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RateInteractor
import com.thomaskioko.tvmaniac.domain.ratings.RatingTarget
import com.thomaskioko.tvmaniac.domain.ratings.RemoveRatingInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@NavDestination(
    route = RatingSheetRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class RatingSheetPresenter internal constructor(
    @Assisted private val param: RatingSheetParam,
    componentContext: ComponentContext,
    observeRatingInteractor: ObserveRatingInteractor,
    observeRatingTargetInteractor: ObserveRatingTargetInteractor,
    private val rateInteractor: RateInteractor,
    private val removeRatingInteractor: RemoveRatingInteractor,
    private val navigator: Navigator,
    private val localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    private val appScopeLauncher: AppScopeLauncher,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val uiMessageManager = UiMessageManager()
    private val ratingLoadingState = ObservableLoadingCounter()
    private val headerLabel = localizer.getString(StringResourceKey.LabelRatingSheetHeader)
    private val scoreLabel = localizer.getString(StringResourceKey.LabelRatingSheetTitle)
    private val removeRatingLabel = localizer.getString(StringResourceKey.LabelActionRemoveRating)
    private val pendingSelection = MutableStateFlow<PendingSelection>(PendingSelection.None)

    public val state: StateFlow<RatingSheetState> = combine(
        observeRatingInteractor.flow,
        observeRatingTargetInteractor.flow,
        pendingSelection,
    ) { savedRating, target, pending ->
        RatingSheetState(
            headerLabel = headerLabel,
            title = target.title(),
            subtitle = target.subtitle(),
            scoreLabel = scoreLabel,
            removeRatingLabel = removeRatingLabel,
            userRating = when (pending) {
                PendingSelection.None -> savedRating
                is PendingSelection.Rated -> pending.rating
                PendingSelection.Cleared -> null
            },
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RatingSheetState(
            headerLabel = headerLabel,
            scoreLabel = scoreLabel,
            removeRatingLabel = removeRatingLabel,
        ),
    )

    public val stateValue: Value<RatingSheetState> = state.asValue(coroutineScope)

    init {
        observeRatingInteractor(ObserveRatingInteractor.Param(param.ratingType, param.id))
        observeRatingTargetInteractor(ObserveRatingTargetInteractor.Param(param.ratingType, param.id))
    }

    public fun dispatch(action: RatingSheetAction) {
        when (action) {
            is RatingSheetAction.RatingSelected -> onStarSelected(action.rating)
            RatingSheetAction.RatingCleared -> onRatingCleared()
            RatingSheetAction.Dismissed -> navigator.dismissOverlay()
        }
    }

    private fun onStarSelected(rating: Int) {
        pendingSelection.value = PendingSelection.Rated(rating)
        appScopeLauncher.launch(TAG) {
            rateInteractor(RateInteractor.Param(type = param.ratingType, id = param.id, rating = rating))
                .collectStatus(ratingLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun onRatingCleared() {
        pendingSelection.value = PendingSelection.Cleared
        appScopeLauncher.launch(TAG) {
            removeRatingInteractor(RemoveRatingInteractor.Param(type = param.ratingType, id = param.id))
                .collectStatus(ratingLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
    }

    private fun RatingTarget?.title(): String = when (this) {
        null -> ""
        is RatingTarget.Show -> title
        is RatingTarget.Season -> title
        is RatingTarget.Episode -> title
    }

    private fun RatingTarget?.subtitle(): String? = when (this) {
        null -> null
        is RatingTarget.Show -> year
        is RatingTarget.Season -> showName
        is RatingTarget.Episode -> "$showName • S${seasonNumber}E$episodeNumber"
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: RatingSheetParam): RatingSheetPresenter
    }

    private sealed interface PendingSelection {
        data object None : PendingSelection
        data class Rated(val rating: Int) : PendingSelection
        data object Cleared : PendingSelection
    }

    private companion object {
        private const val TAG = "RatingSheetPresenter"
    }
}
