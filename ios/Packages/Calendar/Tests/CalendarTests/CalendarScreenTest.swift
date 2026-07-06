import Calendar
import Components
import DesignSystem
import Models
import SnapshotTestingLib
import SwiftUI
import XCTest

class CalendarScreenTest: SnapshotTestCase {
    private let sampleDateGroups: [SwiftCalendarDateGroup] = [
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
    ]

    func test_CalendarScreen_Loading() {
        makeScreen(state: .loading)
            .assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Loading")
    }

    func test_CalendarScreen_LoginRequired() {
        makeScreen(
            state: .loginRequired(
                title: "Nothing to see here",
                message: "Login to Trakt to see your calendar"
            ),
            canNavigateNext: false
        )
        .assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_LoginRequired")
    }

    func test_CalendarScreen_Empty() {
        makeScreen(
            state: .empty(
                title: "Nothing to see here",
                message: "No upcoming episodes"
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Empty")
    }

    func test_CalendarScreen_Locked() {
        makeScreen(
            state: .locked(
                underlying: .content(dateGroups: sampleDateGroups),
                title: "Calendar is a Premium feature",
                message: "Upgrade to see upcoming episodes for your shows"
            ),
            lockedBadgeText: "Premium",
            lockedActionText: "Upgrade to Premium",
            lockedAccessibilityLabel: "Locked"
        )
        .assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Locked")
    }

    func test_CalendarScreen_Content() {
        makeScreen(state: .content(dateGroups: sampleDateGroups))
            .assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Content")
    }

    private func makeScreen(
        state screenState: CalendarScreenState,
        weekLabel: String = "Jan 31, 2026 - Feb 6, 2026",
        canNavigatePrevious: Bool = false,
        canNavigateNext: Bool = true,
        isRefreshing: Bool = false,
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedAccessibilityLabel: String = ""
    ) -> some View {
        NavigationStack {
            CalendarScreen(
                state: CalendarScreen.State(
                    screenState: screenState,
                    weekLabel: weekLabel,
                    canNavigatePrevious: canNavigatePrevious,
                    canNavigateNext: canNavigateNext,
                    isRefreshing: isRefreshing
                ),
                lockedBadgeText: lockedBadgeText,
                lockedActionText: lockedActionText,
                lockedAccessibilityLabel: lockedAccessibilityLabel,
                moreEpisodesFormat: { "+\($0) episodes" },
                onPreviousWeek: {},
                onNextWeek: {},
                onEpisodeCardClicked: { _ in }
            )
        }
        .appPreview()
    }
}
