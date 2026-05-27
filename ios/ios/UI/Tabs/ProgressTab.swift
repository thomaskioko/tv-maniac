import DesignSystem
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
            state: progressState.toState(),
            onPageChanged: { page in
                presenter.dispatch(action: ProgressActionSelectPage(index: Int32(page)))
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
                presenter.upNextPresenter.dispatch(action: UpNextMessageShown(id: message.id))
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
            state: calendarState.toState(),
            moreEpisodesFormat: { count in
                String(format: calendarState.moreEpisodesFormat, count)
            },
            onPreviousWeek: {
                presenter.calendarPresenter.dispatch(action: NavigateToPreviousWeek())
            },
            onNextWeek: {
                presenter.calendarPresenter.dispatch(action: NavigateToNextWeek())
            },
            onEpisodeCardClicked: { episodeTraktId in
                presenter.calendarPresenter.dispatch(action: EpisodeCardClicked(episodeTraktId: episodeTraktId))
            }
        )
    }
}

private extension ProgressState {
    func toState<U: View, C: View>() -> ProgressScreen<U, C>.State {
        ProgressScreen<U, C>.State(
            title: String(\.menu_item_progress),
            isLoading: isLoading,
            selectedPage: Int(selectedPage),
            upNextTabTitle: String(\.label_discover_up_next),
            calendarTabTitle: String(\.title_calendar)
        )
    }
}

private extension CalendarState {
    func toState() -> CalendarPageContent.State {
        CalendarPageContent.State(
            screenState: toScreenState(),
            weekLabel: weekLabel,
            canNavigatePrevious: canNavigatePrevious,
            canNavigateNext: canNavigateNext,
            isRefreshing: isRefreshing
        )
    }

    private func toScreenState() -> CalendarScreenState {
        if showLoading {
            .loading
        } else if !isLoggedIn {
            .loginRequired(title: loginTitle, message: loginMessage)
        } else if isEmpty {
            .empty(title: emptyTitle, message: emptyMessage)
        } else {
            .content(dateGroups: Array(dateGroups).map { $0.toSwift() })
        }
    }
}
