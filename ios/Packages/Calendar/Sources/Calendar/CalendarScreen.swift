import Components
import DesignSystem
import Models
import SwiftUI

public struct CalendarScreen: View {
    public struct State: Equatable {
        public let screenState: CalendarScreenState
        public let weekLabel: String
        public let canNavigatePrevious: Bool
        public let canNavigateNext: Bool
        public let isRefreshing: Bool

        public init(
            screenState: CalendarScreenState,
            weekLabel: String,
            canNavigatePrevious: Bool,
            canNavigateNext: Bool,
            isRefreshing: Bool
        ) {
            self.screenState = screenState
            self.weekLabel = weekLabel
            self.canNavigatePrevious = canNavigatePrevious
            self.canNavigateNext = canNavigateNext
            self.isRefreshing = isRefreshing
        }
    }

    private let state: State
    private let lockedBadgeText: String
    private let lockedActionText: String
    private let lockedAccessibilityLabel: String
    private let onUpgradeClicked: () -> Void
    private let moreEpisodesFormat: (Int32) -> String
    private let onPreviousWeek: () -> Void
    private let onNextWeek: () -> Void
    private let onEpisodeCardClicked: (Int64) -> Void

    public init(
        state: State,
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = "",
        onUpgradeClicked: @escaping () -> Void = {},
        moreEpisodesFormat: @escaping (Int32) -> String,
        onPreviousWeek: @escaping () -> Void,
        onNextWeek: @escaping () -> Void,
        onEpisodeCardClicked: @escaping (Int64) -> Void
    ) {
        self.state = state
        self.lockedBadgeText = lockedBadgeText
        self.lockedActionText = lockedActionText
        self.lockedAccessibilityLabel = lockedAccessibilityLabel
        self.onUpgradeClicked = onUpgradeClicked
        self.moreEpisodesFormat = moreEpisodesFormat
        self.onPreviousWeek = onPreviousWeek
        self.onNextWeek = onNextWeek
        self.onEpisodeCardClicked = onEpisodeCardClicked
    }

    public var body: some View {
        CalendarPageContent(
            state: CalendarPageContent.State(
                screenState: state.screenState,
                weekLabel: state.weekLabel,
                canNavigatePrevious: state.canNavigatePrevious,
                canNavigateNext: state.canNavigateNext,
                isRefreshing: state.isRefreshing,
                useToolbar: true
            ),
            lockedBadgeText: lockedBadgeText,
            lockedActionText: lockedActionText,
            lockedAccessibilityLabel: lockedAccessibilityLabel,
            onUpgradeClicked: onUpgradeClicked,
            moreEpisodesFormat: moreEpisodesFormat,
            onPreviousWeek: onPreviousWeek,
            onNextWeek: onNextWeek,
            onEpisodeCardClicked: onEpisodeCardClicked
        )
    }
}

#Preview("Loading") {
    NavigationStack {
        CalendarScreen(
            state: CalendarScreen.State(
                screenState: .loading,
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false
            ),
            moreEpisodesFormat: { "+\($0) episodes" },
            onPreviousWeek: {},
            onNextWeek: {},
            onEpisodeCardClicked: { _ in }
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Login Required") {
    NavigationStack {
        CalendarScreen(
            state: CalendarScreen.State(
                screenState: .loginRequired(
                    title: "Nothing to see here",
                    message: "Login to Trakt to see your calendar"
                ),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: false,
                isRefreshing: false
            ),
            moreEpisodesFormat: { "+\($0) episodes" },
            onPreviousWeek: {},
            onNextWeek: {},
            onEpisodeCardClicked: { _ in }
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Empty") {
    NavigationStack {
        CalendarScreen(
            state: CalendarScreen.State(
                screenState: .empty(
                    title: "Nothing to see here",
                    message: "No upcoming episodes"
                ),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false
            ),
            moreEpisodesFormat: { "+\($0) episodes" },
            onPreviousWeek: {},
            onNextWeek: {},
            onEpisodeCardClicked: { _ in }
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Locked") {
    NavigationStack {
        CalendarScreen(
            state: CalendarScreen.State(
                screenState: .locked(
                    underlying: .content(dateGroups: [
                        SwiftCalendarDateGroup(
                            dateLabel: "Today, Jan 31, 2026",
                            episodes: [
                                SwiftCalendarEpisodeItem(
                                    showId: 1,
                                    episodeId: 100,
                                    showTitle: "Severance",
                                    posterUrl: nil,
                                    episodeInfo: "S02E01 · Hello, Ms. Cobel",
                                    airTime: "03:00",
                                    network: "Apple TV+",
                                    additionalEpisodesCount: 0
                                ),
                            ]
                        ),
                    ]),
                    title: "Calendar is a Premium feature",
                    message: "Upgrade to see upcoming episodes for your shows"
                ),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false
            ),
            lockedBadgeText: "Premium",
            lockedActionText: "Upgrade to Premium",
            lockedAccessibilityLabel: "Locked",
            moreEpisodesFormat: { "+\($0) episodes" },
            onPreviousWeek: {},
            onNextWeek: {},
            onEpisodeCardClicked: { _ in }
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Content") {
    NavigationStack {
        CalendarScreen(
            state: CalendarScreen.State(
                screenState: .content(dateGroups: [
                    SwiftCalendarDateGroup(
                        dateLabel: "Today, Jan 31, 2026",
                        episodes: [
                            SwiftCalendarEpisodeItem(
                                showId: 1,
                                episodeId: 100,
                                showTitle: "Severance",
                                posterUrl: nil,
                                episodeInfo: "S02E01 · Hello, Ms. Cobel",
                                airTime: "03:00",
                                network: "Apple TV+",
                                additionalEpisodesCount: 0
                            ),
                        ]
                    ),
                    SwiftCalendarDateGroup(
                        dateLabel: "Tomorrow, Feb 1, 2026",
                        episodes: [
                            SwiftCalendarEpisodeItem(
                                showId: 2,
                                episodeId: 200,
                                showTitle: "Hell's Paradise",
                                posterUrl: nil,
                                episodeInfo: "S02E04 · The Battle Begins",
                                airTime: "15:45",
                                network: nil,
                                additionalEpisodesCount: 1
                            ),
                        ]
                    ),
                ]),
                weekLabel: "Jan 31, 2026 - Feb 6, 2026",
                canNavigatePrevious: false,
                canNavigateNext: true,
                isRefreshing: false
            ),
            moreEpisodesFormat: { "+\($0) episodes" },
            onPreviousWeek: {},
            onNextWeek: {},
            onEpisodeCardClicked: { _ in }
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}
