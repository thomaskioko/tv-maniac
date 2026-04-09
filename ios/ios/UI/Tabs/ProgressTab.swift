import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProgressTab: View {
    private let presenter: ProgressPresenter
    @StateValue private var progressState: ProgressState
    @StateValue private var upNextState: UpNextState
    @StateValue private var calendarState: CalendarState
    @State private var toast: Toast?

    init(presenter: ProgressPresenter) {
        self.presenter = presenter
        _progressState = .init(presenter.stateValue)
        _upNextState = .init(presenter.upNextPresenter.stateValue)
        _calendarState = .init(presenter.calendarPresenter.stateValue)
    }

    var body: some View {
        ProgressScreen(
            title: String(\.menu_item_progress),
            isLoading: upNextState.isLoading || calendarState.isLoading,
            selectedPage: Int(progressState.selectedPage),
            upNextTabTitle: String(\.label_discover_up_next),
            calendarTabTitle: String(\.title_calendar),
            onPageChanged: { page in
                presenter.dispatch(action______: ProgressActionSelectPage(index: Int32(page)))
            },
            upNextContent: {
                upNextContent
            },
            calendarContent: {
                calendarContent
            }
        )
        .onChange(of: upNextState.message) { _, newValue in
            if let message = newValue {
                toast = Toast(
                    type: .error,
                    title: "Error",
                    message: message.message
                )
                presenter.upNextPresenter.dispatch(action_______: UpNextMessageShown(id: message.id))
            }
        }
        .toastView(toast: $toast)
    }

    private var upNextContent: some View {
        UpNextPageContent(
            presenter: presenter.upNextPresenter,
            uiState: upNextState
        )
    }

    private var calendarContent: some View {
        CalendarPageContent(
            state: mapCalendarScreenState(),
            weekLabel: calendarState.weekLabel,
            canNavigatePrevious: calendarState.canNavigatePrevious,
            canNavigateNext: calendarState.canNavigateNext,
            isRefreshing: calendarState.isRefreshing,
            moreEpisodesFormat: { count in
                String(format: calendarState.moreEpisodesFormat, count)
            },
            onPreviousWeek: {
                presenter.calendarPresenter.dispatch(action___: NavigateToPreviousWeek())
            },
            onNextWeek: {
                presenter.calendarPresenter.dispatch(action___: NavigateToNextWeek())
            },
            onEpisodeCardClicked: { episodeTraktId in
                presenter.calendarPresenter.dispatch(action___: EpisodeCardClicked(episodeTraktId: episodeTraktId))
            }
        )
    }

    private func mapCalendarScreenState() -> CalendarScreenState {
        if calendarState.showLoading {
            .loading
        } else if !calendarState.isLoggedIn {
            .loginRequired(
                title: calendarState.loginTitle,
                message: calendarState.loginMessage
            )
        } else if calendarState.isEmpty {
            .empty(
                title: calendarState.emptyTitle,
                message: calendarState.emptyMessage
            )
        } else {
            .content(
                dateGroups: Array(calendarState.dateGroups).map { $0.toSwift() }
            )
        }
    }
}
