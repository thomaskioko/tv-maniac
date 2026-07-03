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
import com.thomaskioko.tvmaniac.domain.ratings.RateInteractor
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@NavDestination(
    route = RatingSheetRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class RatingSheetPresenter(
    @Assisted private val param: RatingSheetParam,
    componentContext: ComponentContext,
    observeRatingInteractor: ObserveRatingInteractor,
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
    private val title = localizer.getString(StringResourceKey.LabelRatingSheetTitle)
    private val removeRatingLabel = localizer.getString(StringResourceKey.LabelActionRemoveRating)

    public val state: StateFlow<RatingSheetState> = observeRatingInteractor.flow
        .map { RatingSheetState(title = title, removeRatingLabel = removeRatingLabel, userRating = it) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RatingSheetState(),
        )

    public val stateValue: Value<RatingSheetState> = state.asValue(coroutineScope)

    init {
        observeRatingInteractor(ObserveRatingInteractor.Param(param.ratingType, param.id))
    }

    public fun dispatch(action: RatingSheetAction) {
        when (action) {
            is RatingSheetAction.RatingSelected -> onStarSelected(action.rating)
            RatingSheetAction.RatingCleared -> onRatingCleared()
            RatingSheetAction.Dismissed -> navigator.dismissOverlay()
        }
    }

    private fun onStarSelected(rating: Int) {
        appScopeLauncher.launch(TAG) {
            rateInteractor(RateInteractor.Param(type = param.ratingType, id = param.id, rating = rating))
                .collectStatus(ratingLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
        navigator.dismissOverlay()
    }

    private fun onRatingCleared() {
        appScopeLauncher.launch(TAG) {
            removeRatingInteractor(RemoveRatingInteractor.Param(type = param.ratingType, id = param.id))
                .collectStatus(ratingLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
        navigator.dismissOverlay()
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: RatingSheetParam): RatingSheetPresenter
    }

    private companion object {
        private const val TAG = "RatingSheetPresenter"
    }
}
