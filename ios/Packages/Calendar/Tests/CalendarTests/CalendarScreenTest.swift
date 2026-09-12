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
        let view = makeScreen(state: .loading)
        view.assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Loading")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "CalendarScreen_Loading")
    }

    func test_CalendarScreen_LoginRequired() {
        let view = makeScreen(
            state: .loginRequired(
                title: "Nothing to see here",
                message: "Login to Trakt to see your calendar"
            ),
            canNavigateNext: false
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_LoginRequired")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "CalendarScreen_LoginRequired")
    }

    func test_CalendarScreen_Empty() {
        let view = makeScreen(
            state: .empty(
                title: "Nothing to see here",
                message: "No upcoming episodes"
            )
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Empty")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "CalendarScreen_Empty")
    }

    func test_CalendarScreen_Locked() {
        let view = makeScreen(
            state: .locked(
                underlying: .content(dateGroups: sampleDateGroups),
                title: "Calendar is a Premium feature",
                message: "Upgrade to see upcoming episodes for your shows"
            ),
            lockedBadgeText: "Premium",
            lockedActionText: "Upgrade to Premium",
            lockedAccessibilityLabel: "Locked"
        )
        view.assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Locked")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "CalendarScreen_Locked")
    }

    func test_CalendarScreen_Content() {
        let view = makeScreen(state: .content(dateGroups: sampleDateGroups))
        view.assertSnapshot(layout: .defaultDevice, testName: "CalendarScreen_Content")
        view.assertSnapshot(layout: .defaultDevice, liquidGlass: true, testName: "CalendarScreen_Content")
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
