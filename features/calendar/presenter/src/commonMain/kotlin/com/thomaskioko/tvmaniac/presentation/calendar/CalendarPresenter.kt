package com.thomaskioko.tvmaniac.presentation.calendar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.thomaskioko.tvmaniac.calendar.nav.CalendarNavigator
import com.thomaskioko.tvmaniac.core.base.extensions.asValue
import com.thomaskioko.tvmaniac.core.base.extensions.coroutineScope
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.core.view.ObservableLoadingCounter
import com.thomaskioko.tvmaniac.core.view.UiMessageManager
import com.thomaskioko.tvmaniac.core.view.collectStatus
import com.thomaskioko.tvmaniac.domain.calendar.CalendarWeekCalculator
import com.thomaskioko.tvmaniac.domain.calendar.CalendarWeekCalculator.Companion.DAYS_IN_WEEK
import com.thomaskioko.tvmaniac.domain.calendar.FetchCalendarInteractor
import com.thomaskioko.tvmaniac.domain.calendar.ObserveCalendarInteractor
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelCalendarEmpty
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelCalendarLoginRequired
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelCalendarMoreEpisodes
import com.thomaskioko.tvmaniac.i18n.StringResourceKey.LabelCalendarNoData
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
public class CalendarPresenter(
    componentContext: ComponentContext,
    private val navigator: CalendarNavigator,
    private val observeCalendarInteractor: ObserveCalendarInteractor,
    private val fetchCalendarInteractor: FetchCalendarInteractor,
    private val traktAuthRepository: TraktAuthRepository,
    private val calendarWeekCalculator: CalendarWeekCalculator,
    private val calendarStateMapper: CalendarStateMapper,
    private val errorToStringMapper: ErrorToStringMapper,
    private val logger: Logger,
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val loadingState = ObservableLoadingCounter()
    private val uiMessageManager = UiMessageManager()
    private val _state = MutableStateFlow(CalendarState())

    init {
        observeAuthState()
        observeCalendar()
        fetchCalendar(startDate = calendarWeekCalculator.getStartDateForOffset(_state.value.weekOffset))
    }

    public val state: StateFlow<CalendarState> = combine(
        loadingState.observable,
        observeCalendarInteractor.flow,
        traktAuthRepository.state,
        uiMessageManager.message,
        _state,
    ) { isLoading, entries, authState, message, currentState ->
        val isLoggedIn = authState == TraktAuthState.LOGGED_IN
        currentState.copy(
            isLoading = isLoading && entries.isEmpty(),
            isRefreshing = isLoading,
            isLoggedIn = isLoggedIn,
            weekLabel = calendarWeekCalculator.formatWeekLabel(currentState.weekOffset),
            canNavigatePrevious = currentState.weekOffset > 0,
            canNavigateNext = isLoggedIn,
            dateGroups = calendarStateMapper.toCalendarDateGroups(entries),
            emptyTitle = calendarStateMapper.getString(LabelCalendarNoData),
            emptyMessage = calendarStateMapper.getString(LabelCalendarEmpty),
            loginTitle = calendarStateMapper.getString(LabelCalendarNoData),
            loginMessage = calendarStateMapper.getString(LabelCalendarLoginRequired),
            moreEpisodesFormat = calendarStateMapper.getString(LabelCalendarMoreEpisodes),
            message = message,
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = CalendarState(),
    )

    public val stateValue: Value<CalendarState> = state.asValue(coroutineScope)

    public fun dispatch(action: CalendarAction) {
        when (action) {
            is RefreshCalendar -> fetchCalendar(
                startDate = calendarWeekCalculator.getStartDateForOffset(_state.value.weekOffset),
                forceRefresh = true,
            )

            is NavigateToPreviousWeek -> navigateToPreviousWeek()
            is NavigateToNextWeek -> navigateToNextWeek()
            is EpisodeCardClicked -> navigator.showEpisodeSheet(action.episodeTraktId)
            is MessageShown -> clearMessage(action.id)
        }
    }

    private fun observeAuthState() {
        coroutineScope.launch {
            traktAuthRepository.state
                .distinctUntilChanged()
                .filter { it == TraktAuthState.LOGGED_IN }
                .drop(1)
                .collect {
                    fetchCalendar(startDate = calendarWeekCalculator.getStartDateForOffset(_state.value.weekOffset))
                }
        }
    }

    private fun observeCalendar() {
        coroutineScope.launch {
            _state
                .map { it.weekOffset }
                .distinctUntilChanged()
                .collect { weekOffset ->
                    val (startEpoch, endEpoch) = calendarWeekCalculator.getWeekEpochRange(weekOffset)
                    observeCalendarInteractor(
                        ObserveCalendarInteractor.Params(
                            startDate = startEpoch,
                            endDate = endEpoch,
                        ),
                    )
                }
        }
    }

    private fun fetchCalendar(startDate: String, forceRefresh: Boolean = false) {
        coroutineScope.launch {
            fetchCalendarInteractor(
                FetchCalendarInteractor.Params(
                    startDate = startDate,
                    days = DAYS_IN_WEEK,
                    forceRefresh = forceRefresh,
                ),
            ).collectStatus(
                counter = loadingState,
                logger = logger,
                uiMessageManager = uiMessageManager,
                sourceId = "Calendar",
                errorToStringMapper = errorToStringMapper,
            )
        }
    }

    private fun navigateToPreviousWeek() {
        if (_state.value.weekOffset > 0) {
            _state.update { it.copy(weekOffset = it.weekOffset - 1) }
            fetchCalendar(startDate = calendarWeekCalculator.getStartDateForOffset(_state.value.weekOffset))
        }
    }

    private fun navigateToNextWeek() {
        _state.update { it.copy(weekOffset = it.weekOffset + 1) }
        fetchCalendar(startDate = calendarWeekCalculator.getStartDateForOffset(_state.value.weekOffset))
    }

    private fun clearMessage(id: Long) {
        coroutineScope.launch {
            uiMessageManager.clearMessage(id)
        }
    }
}
