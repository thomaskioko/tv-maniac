import SwiftUI
import SwiftUIComponents
import TvManiacKit

struct ProgressTab: View {
    private let presenter: ProgressPresenter
    @StateObject @KotlinStateFlow private var progressState: ProgressState
    @StateObject @KotlinStateFlow private var upNextState: UpNextState
    @StateObject @KotlinStateFlow private var calendarState: CalendarState
    @State private var toast: Toast?

    init(presenter: ProgressPresenter) {
        self.presenter = presenter
        _progressState = .init(presenter.state)
        _upNextState = .init(presenter.upNextPresenter.state)
        _calendarState = .init(presenter.calendarPresenter.state)
    }

    var body: some View {
        ProgressScreen(
            title: String(\.menu_item_progress),
            isLoading: upNextState.isLoading || calendarState.isLoading,
            selectedPage: Int(progressState.selectedPage),
            upNextTabTitle: String(\.label_discover_up_next),
            calendarTabTitle: String(\.title_calendar),
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
            state: mapCalendarScreenState(),
            weekLabel: calendarState.weekLabel,
            canNavigatePrevious: calendarState.canNavigatePrevious,
            canNavigateNext: calendarState.canNavigateNext,
            isRefreshing: calendarState.isRefreshing,
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
        .sheet(
            isPresented: Binding(
                get: { calendarState.showEpisodeDetail },
                set: { isPresented in
                    if !isPresented {
                        presenter.calendarPresenter.dispatch(action: EpisodeDetailDismissed())
                    }
                }
            )
        ) {
            if let episode = calendarState.selectedEpisode {
                let swiftEpisode = episode.toSwift()
                EpisodeDetailSheetContent(
                    episode: EpisodeDetailInfo(
                        title: swiftEpisode.showTitle,
                        imageUrl: swiftEpisode.posterUrl,
                        episodeInfo: {
                            if let formattedDate = swiftEpisode.formattedAirDate {
                                return "\(swiftEpisode.episodeInfo) \u{2022} \(formattedDate)"
                            }
                            return swiftEpisode.episodeInfo
                        }(),
                        overview: swiftEpisode.overview,
                        rating: swiftEpisode.rating,
                        voteCount: swiftEpisode.votes.map { Int64($0) }
                    )
                )
            }
        }
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
