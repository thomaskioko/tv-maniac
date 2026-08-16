package com.thomaskioko.tvmaniac.watchdateselection.presenter

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
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.domain.episode.MarkWatchedAtInteractor
import com.thomaskioko.tvmaniac.domain.episode.MarkWatchedAtParams
import com.thomaskioko.tvmaniac.domain.episode.ObserveEpisodeByIdInteractor
import com.thomaskioko.tvmaniac.domain.ratings.ShouldPromptForRatingInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedDate
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetParam
import com.thomaskioko.tvmaniac.ratingsheet.nav.RatingSheetRoute
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionParam
import com.thomaskioko.tvmaniac.watchdateselection.nav.WatchDateSelectionRoute
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import io.github.thomaskioko.codegen.annotations.DestinationKind
import io.github.thomaskioko.codegen.annotations.NavDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@NavDestination(
    route = WatchDateSelectionRoute::class,
    parentScope = ActivityScope::class,
    kind = DestinationKind.OVERLAY,
)
@AssistedInject
public class WatchDateSelectionPresenter internal constructor(
    @Assisted private val param: WatchDateSelectionParam,
    componentContext: ComponentContext,
    observeEpisodeByIdInteractor: ObserveEpisodeByIdInteractor,
    private val markWatchedAtInteractor: MarkWatchedAtInteractor,
    private val shouldPromptForRatingInteractor: ShouldPromptForRatingInteractor,
    private val dateTimeProvider: DateTimeProvider,
    private val navigator: Navigator,
    localizer: Localizer,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
    private val appScopeLauncher: AppScopeLauncher,
) {

    private val coroutineScope = componentContext.coroutineScope()
    private val markLoadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()

    private val episode: StateFlow<EpisodeById?> = observeEpisodeByIdInteractor.flow
        .stateIn(scope = coroutineScope, started = SharingStarted.Eagerly, initialValue = null)

    private val title = localizer.getString(
        if (param.isEdit) StringResourceKey.LabelWatchedDateEditTitle else StringResourceKey.LabelWatchedDateTitle,
    )
    private val justNowLabel = localizer.getString(StringResourceKey.LabelWatchedDateJustNow)
    private val releaseDateLabel = localizer.getString(StringResourceKey.LabelWatchedDateReleaseDate)
    private val otherDateLabel = localizer.getString(StringResourceKey.LabelWatchedDateOther)
    private val unknownDateLabel = localizer.getString(StringResourceKey.LabelWatchedDateUnknown)
    private val unknownDateDisplayLabel = localizer.getString(StringResourceKey.LabelWatchedDateUnknownDisplay)

    public val state: StateFlow<WatchDateSelectionState> = episode
        .map { buildState(it) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = buildState(episode = null),
        )

    public val stateValue: Value<WatchDateSelectionState> = state.asValue(coroutineScope)

    init {
        observeEpisodeByIdInteractor(param.episodeId)
    }

    public fun dispatch(action: WatchDateSelectionAction) {
        when (action) {
            WatchDateSelectionAction.JustNowSelected -> markWatched(watchedAt = dateTimeProvider.nowMillis())
            WatchDateSelectionAction.ReleaseDateSelected -> markWatched(useReleaseDate = true)
            is WatchDateSelectionAction.OtherDateSelected -> markWatched(watchedAt = pickedDateTime(action.date, action.time))
            WatchDateSelectionAction.UnknownDateSelected -> markWatched(watchedAt = WatchedDate.UNKNOWN_MILLIS)
            WatchDateSelectionAction.Dismissed -> navigator.dismissOverlay()
        }
    }

    private fun buildState(episode: EpisodeById?): WatchDateSelectionState = WatchDateSelectionState(
        title = title,
        justNowLabel = justNowLabel,
        releaseDateLabel = releaseDateLabel,
        otherDateLabel = otherDateLabel,
        unknownDateLabel = unknownDateLabel,
        isReleaseDateEnabled = isReleaseDateEnabled(episode),
        currentWatchedAtLabel = currentWatchedAtLabel(episode),
        maxSelectableDate = today(),
    )

    private fun isReleaseDateEnabled(episode: EpisodeById?): Boolean = when (param.target) {
        WatchedDateTarget.EPISODE -> episode?.first_aired != null
        WatchedDateTarget.SEASON, WatchedDateTarget.SHOW -> true
    }

    private fun currentWatchedAtLabel(episode: EpisodeById?): String? {
        if (!param.isEdit) return null
        val watchedAt = episode?.watched_at ?: return null
        return if (WatchedDate.isUnknown(watchedAt)) {
            unknownDateDisplayLabel
        } else {
            dateTimeProvider.epochToDisplayDateTime(watchedAt)
        }
    }

    private fun today(): LocalDate =
        dateTimeProvider.now().toLocalDateTime(dateTimeProvider.getTimeZone()).date

    private fun pickedDateTime(date: LocalDate, time: LocalTime): Long {
        val picked = LocalDateTime(date, time)
            .toInstant(dateTimeProvider.getTimeZone())
            .toEpochMilliseconds()
        return minOf(picked, dateTimeProvider.nowMillis())
    }

    private fun markWatched(watchedAt: Long? = null, useReleaseDate: Boolean = false) {
        val wasWatched = episode.value?.let { it.is_watched != 0L } ?: false
        appScopeLauncher.launch(TAG) {
            markWatchedAtInteractor(markParams(watchedAt, useReleaseDate))
                .collectStatus(markLoadingState, logger, uiMessageManager, errorToStringMapper = errorToStringMapper)
        }
        coroutineScope.launch {
            val ratingRoute = ratingRouteOrNull(wasWatched)
            navigator.dismissOverlay()
            if (ratingRoute != null) navigator.navigateTo(ratingRoute)
        }
    }

    private fun markParams(watchedAt: Long?, useReleaseDate: Boolean) = MarkWatchedAtParams(
        target = param.target,
        showId = param.showId,
        episodeId = param.episodeId,
        seasonNumber = param.seasonNumber,
        episodeNumber = param.episodeNumber,
        markPrevious = param.markPrevious,
        isEdit = param.isEdit,
        watchedAt = watchedAt,
        useReleaseDate = useReleaseDate,
    )

    private suspend fun ratingRouteOrNull(wasWatched: Boolean): RatingSheetRoute? {
        if (param.target != WatchedDateTarget.EPISODE) return null
        if (param.isEdit || wasWatched) return null
        val shouldRate = shouldPromptForRatingInteractor(
            ShouldPromptForRatingInteractor.Param(
                showId = param.showId,
                episodeId = param.episodeId,
            ),
        )
        if (!shouldRate) return null
        return RatingSheetRoute(
            RatingSheetParam(
                ratingType = RatingEntityType.EPISODE,
                id = param.episodeId,
            ),
        )
    }

    @AssistedFactory
    public fun interface Factory {
        public fun create(param: WatchDateSelectionParam): WatchDateSelectionPresenter
    }

    private companion object {
        private const val TAG = "WatchDateSelectionPresenter"
    }
}
