import Components
import DesignSystem
import Models
import SnapshotTestingLib
@testable import Statistics
import SwiftUI
import TvManiac
import XCTest

class StatisticsScreenTest: SnapshotTestCase {
    private let labels = SwiftStatisticsLabels(
        screenTitle: "Statistics",
        emptyMessage: "Mark an episode as watched to start building your statistics.",
        watchTimeTitle: "Total time watched",
        daysLabel: "days",
        hoursLabel: "hours",
        minutesLabel: "minutes",
        episodesOverTimeTitle: "Your year of watching",
        markedWatchedNote: "Dates show when episodes were marked as watched, not when you watched them.",
        mostWatchedTitle: "Episodes by show",
        highestRatedTitle: "Highest rated shows",
        watchStatusTitle: "Shows by status",
        ratingsTitle: "Your ratings",
        yearlyActivityTitle: "Episodes by year",
        monthlyActivityTitle: "Episodes by month",
        weekdayActivityTitle: "Episodes by day of the week",
        genresTitle: "Genres you watch",
        releaseYearsTitle: "Shows by release year",
        genresEmptyMessage: "We do not know the genres of the shows you have watched yet.",
        lockedTitle: "Statistics are a Premium feature",
        lockedMessage: "Upgrade to Premium to see how you watch.",
        lockedBadgeText: "Premium",
        lockedActionText: "Upgrade to Premium",
        lockedContentDescription: "Locked"
    )

    private let tiles: [SwiftStatisticTile] = [
        .init(id: "PeakYear", label: "Peak year", value: "2023", caption: "312 episodes"),
        .init(id: "WatchDaysThisYear", label: "Watch days this year", value: "112/214", caption: "days you watched something"),
        .init(id: "TopWeekday", label: "Top weekday", value: "Sunday", caption: "48 episodes"),
        .init(id: "LastThirtyDays", label: "Last 30 days", value: "24", caption: "18 days"),
        .init(id: "TitlesTracked", label: "Titles tracked", value: "128", caption: "18 completed"),
        .init(id: "Episodes", label: "Episodes", value: "1.2K", caption: ""),
        .init(id: "AverageRating", label: "Average rating", value: "8.4", caption: "across 42 rated"),
        .init(id: "WatchStreak", label: "Watch streak", value: "9 days", caption: "longest run"),
    ]

    private let highestRatedShows: [SwiftShowRowItem] = [
        .init(showId: 1396, title: "Breaking Bad", posterPath: nil, caption: "5/5"),
        .init(showId: 1399, title: "Game of Thrones", posterPath: nil, caption: "4.5/5"),
        .init(showId: 66732, title: "Stranger Things", posterPath: nil, caption: "4/5"),
    ]

    private let mostWatchedShows: [SwiftShowRowItem] = [
        .init(showId: 1396, title: "Breaking Bad", posterPath: nil, caption: "62 episodes"),
        .init(showId: 1399, title: "Game of Thrones", posterPath: nil, caption: "73 episodes"),
        .init(showId: 66732, title: "Stranger Things", posterPath: nil, caption: "34 episodes"),
    ]

    private let watchStatusBreakdown: [SwiftWatchStatusItem] = [
        .init(id: "Watchlist", label: "Watchlist", showCount: 24, fraction: 0.4),
        .init(id: "Watching", label: "Watching", showCount: 6, fraction: 0.1),
        .init(id: "Completed", label: "Completed", showCount: 28, fraction: 0.46),
        .init(id: "OnHold", label: "On hold", showCount: 2, fraction: 0.03),
        .init(id: "Dropped", label: "Dropped", showCount: 1, fraction: 0.01),
    ]

    private let ratingBreakdown: [SwiftRatingBar] = [
        .init(rating: 10, count: 12, fraction: 1.0),
        .init(rating: 9, count: 8, fraction: 0.66),
        .init(rating: 8, count: 10, fraction: 0.83),
        .init(rating: 7, count: 3, fraction: 0.25),
        .init(rating: 6, count: 1, fraction: 0.08),
    ]

    private let yearlyActivity: [SwiftActivityBar] = [
        .init(label: "2022", caption: "84 episodes", fraction: 0.27),
        .init(label: "2023", caption: "312 episodes", fraction: 1),
        .init(label: "2024", caption: "196 episodes", fraction: 0.63),
        .init(label: "2025", caption: "148 episodes", fraction: 0.47),
        .init(label: "2026", caption: "91 episodes", fraction: 0.29),
    ]

    private let monthlyActivity: [SwiftActivityBar] = [
        .init(label: "Jan", caption: "24 episodes", fraction: 0.6),
        .init(label: "Feb", caption: "18 episodes", fraction: 0.45),
        .init(label: "Mar", caption: "31 episodes", fraction: 0.77),
        .init(label: "Apr", caption: "12 episodes", fraction: 0.3),
        .init(label: "May", caption: "40 episodes", fraction: 1),
        .init(label: "Jun", caption: "22 episodes", fraction: 0.55),
        .init(label: "Jul", caption: "8 episodes", fraction: 0.2),
        .init(label: "Aug", caption: "27 episodes", fraction: 0.67),
        .init(label: "Sep", caption: "15 episodes", fraction: 0.37),
        .init(label: "Oct", caption: "33 episodes", fraction: 0.82),
        .init(label: "Nov", caption: "19 episodes", fraction: 0.47),
        .init(label: "Dec", caption: "29 episodes", fraction: 0.72),
    ]

    private let weekdayActivity: [SwiftActivityBar] = [
        .init(label: "Mon", caption: "12 episodes", fraction: 0.6),
        .init(label: "Tue", caption: "8 episodes", fraction: 0.4),
        .init(label: "Wed", caption: "14 episodes", fraction: 0.7),
        .init(label: "Thu", caption: "6 episodes", fraction: 0.3),
        .init(label: "Fri", caption: "17 episodes", fraction: 0.85),
        .init(label: "Sat", caption: "20 episodes", fraction: 1),
        .init(label: "Sun", caption: "16 episodes", fraction: 0.8),
    ]

    private let heatMap = SwiftWatchHeatMap(
        levels: (0 ..< 215).map { ($0 * 7) % 5 },
        leadingBlankCells: 3,
        scaleFloorCount: 1,
        scaleTopCount: 10,
        todayIndex: 214
    )

    private let longRunOfYears: [SwiftActivityBar] = (2008 ... 2026).enumerated().map { index, year in
        let episodeCount = (index * 7) % 40
        return .init(
            label: "\(year)",
            caption: "\(episodeCount) episodes",
            fraction: Float(episodeCount) / 40
        )
    }

    private let genreBreakdown: [SwiftGenreSlice] = [
        .init(name: "Drama", showCount: 24, caption: "24 shows", fraction: 1),
        .init(name: "Comedy", showCount: 11, caption: "11 shows", fraction: 0.45),
        .init(name: "Crime", showCount: 9, caption: "9 shows", fraction: 0.37),
        .init(name: "Science Fiction", showCount: 7, caption: "7 shows", fraction: 0.29),
        .init(name: "Other", showCount: 12, caption: "12 shows", fraction: 0.5),
    ]

    private let releaseYears: [SwiftActivityBar] = [
        .init(label: "2016", caption: "3 shows", fraction: 0.37),
        .init(label: "2017", caption: "5 shows", fraction: 0.62),
        .init(label: "2018", caption: "2 shows", fraction: 0.25),
        .init(label: "2019", caption: "8 shows", fraction: 1),
        .init(label: "2020", caption: "4 shows", fraction: 0.5),
        .init(label: "2021", caption: "6 shows", fraction: 0.75),
    ]

    private var contentState: StatisticsScreen.State {
        StatisticsScreen.State(
            isLoading: false,
            showContent: true,
            totalWatchTime: SwiftWatchTime(days: 12, hours: 4, minutes: 30),
            tiles: tiles,
            heatMap: heatMap,
            mostWatchedShows: mostWatchedShows,
            highestRatedShows: highestRatedShows,
            watchStatusBreakdown: watchStatusBreakdown,
            ratingBreakdown: ratingBreakdown,
            yearlyActivity: yearlyActivity,
            monthlyActivity: monthlyActivity,
            weekdayActivity: weekdayActivity,
            genreBreakdown: genreBreakdown,
            releaseYears: releaseYears,
            labels: labels
        )
    }

    func test_StatisticsScreen_Loading() {
        makeScreen(state: StatisticsScreen.State(isLoading: true, labels: labels))
            .assertSnapshot(layout: .defaultDevice, testName: "StatisticsScreen_Loading")
    }

    func test_StatisticsScreen_Empty() {
        makeScreen(state: StatisticsScreen.State(isLoading: false, showEmptyState: true, labels: labels))
            .assertSnapshot(layout: .defaultDevice, testName: "StatisticsScreen_Empty")
    }

    func test_StatisticsScreen_Content() {
        makeScreen(state: contentState)
            .assertSnapshot(layout: .defaultDevice, testName: "StatisticsScreen_Content")
    }

    func test_StatisticsScreen_MarkedWatchedTimes() {
        makeScreen(
            state: StatisticsScreen.State(
                isLoading: false,
                showContent: true,
                showsMarkedWatchedTimes: true,
                totalWatchTime: contentState.totalWatchTime,
                tiles: contentState.tiles,
                heatMap: contentState.heatMap,
                mostWatchedShows: contentState.mostWatchedShows,
                watchStatusBreakdown: contentState.watchStatusBreakdown,
                ratingBreakdown: contentState.ratingBreakdown,
                yearlyActivity: contentState.yearlyActivity,
                monthlyActivity: contentState.monthlyActivity,
                weekdayActivity: contentState.weekdayActivity,
                labels: labels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "StatisticsScreen_MarkedWatchedTimes")
    }

    func test_StatisticsScreen_Locked() {
        makeScreen(
            state: StatisticsScreen.State(
                isLoading: false,
                isLocked: true,
                showContent: true,
                totalWatchTime: contentState.totalWatchTime,
                tiles: contentState.tiles,
                heatMap: contentState.heatMap,
                mostWatchedShows: contentState.mostWatchedShows,
                watchStatusBreakdown: contentState.watchStatusBreakdown,
                ratingBreakdown: contentState.ratingBreakdown,
                yearlyActivity: contentState.yearlyActivity,
                monthlyActivity: contentState.monthlyActivity,
                weekdayActivity: contentState.weekdayActivity,
                labels: labels
            )
        )
        .assertSnapshot(layout: .defaultDevice, testName: "StatisticsScreen_Locked")
    }

    func test_ActivityChart_LongRunOfYears() {
        ActivityChartSectionView(
            bars: longRunOfYears,
            title: "Episodes by year",
            sectionTestTag: StatisticsTestTags.shared.YEARLY_ACTIVITY_TEST_TAG,
            sectionName: "yearly",
            showAxisLabels: true
        )
        .appPreview()
        .assertSnapshot(
            layout: .fixed(width: 390, height: 220),
            testName: "ActivityChart_LongRunOfYears"
        )
    }

    private func makeScreen(state: StatisticsScreen.State) -> some View {
        NavigationStack {
            StatisticsScreen(state: state, backButtonAccessibilityLabel: "Back")
        }
        .appPreview()
    }

    func test_WatchHeatMapSection_LegendAndToday() {
        WatchHeatMapSectionView(
            heatMap: SwiftWatchHeatMap(
                levels: (0 ..< 24).map { $0 % 5 },
                leadingBlankCells: 2,
                scaleFloorCount: 1,
                scaleTopCount: 10,
                todayIndex: 23
            ),
            title: "Your year of watching"
        )
        .appPreview()
        .assertSnapshot(
            layout: .fixed(width: 390, height: 200),
            testName: "WatchHeatMapSection_LegendAndToday"
        )
    }

    func test_GenreSection_Content() {
        GenreSectionView(
            genres: genreBreakdown,
            title: "Genres you watch",
            emptyMessage: ""
        )
        .appPreview()
        .assertSnapshot(
            layout: .fixed(width: 390, height: 400),
            testName: "GenreSection_Content"
        )
    }

    func test_GenreSection_Empty() {
        GenreSectionView(
            genres: [],
            title: "Genres you watch",
            emptyMessage: "We do not know the genres of the shows you have watched yet."
        )
        .appPreview()
        .assertSnapshot(
            layout: .fixed(width: 390, height: 160),
            testName: "GenreSection_Empty"
        )
    }
}
