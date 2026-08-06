import DesignSystem
import SwiftUI

private let previewLabels = SwiftStatisticsLabels(
    screenTitle: "Statistics",
    emptyMessage: "Mark an episode as watched to start building your statistics.",
    watchTimeTitle: "Total time watched",
    daysLabel: "days",
    hoursLabel: "hours",
    minutesLabel: "minutes",
    markedWatchedNote: "Dates show when episodes were marked as watched, not when you watched them.",
    mostWatchedTitle: "Most watched shows",
    watchStatusTitle: "Shows by status",
    ratingsTitle: "Your ratings",
    lockedTitle: "Statistics are a Premium feature",
    lockedMessage: "Upgrade to Premium to see how you watch.",
    lockedBadgeText: "Premium",
    lockedActionText: "Upgrade to Premium",
    lockedContentDescription: "Locked"
)

private let previewTiles: [SwiftStatisticTile] = [
    .init(id: "PeakYear", label: "Peak year", value: "2023", caption: "312 episodes"),
    .init(id: "WatchDaysThisYear", label: "Watch days this year", value: "112/214", caption: "days you watched something"),
    .init(id: "TopWeekday", label: "Top weekday", value: "Sunday", caption: "48 episodes"),
    .init(id: "LastThirtyDays", label: "Last 30 days", value: "24", caption: "18 days"),
    .init(id: "TitlesTracked", label: "Titles tracked", value: "128", caption: "18 completed"),
    .init(id: "Episodes", label: "Episodes", value: "1.2K", caption: ""),
    .init(id: "AverageRating", label: "Average rating", value: "8.4", caption: "across 42 rated"),
    .init(id: "WatchStreak", label: "Watch streak", value: "9 days", caption: "longest run"),
]

private let previewMostWatchedShows: [SwiftMostWatchedShowItem] = [
    .init(showId: 1396, title: "Breaking Bad", posterPath: nil, episodeCount: 62, caption: "62 episodes"),
    .init(showId: 1399, title: "Game of Thrones", posterPath: nil, episodeCount: 73, caption: "73 episodes"),
    .init(showId: 66732, title: "Stranger Things", posterPath: nil, episodeCount: 34, caption: "34 episodes"),
]

private let previewWatchStatusBreakdown: [SwiftWatchStatusItem] = [
    .init(id: "Watchlist", label: "Watchlist", showCount: 24, fraction: 0.4),
    .init(id: "Watching", label: "Watching", showCount: 6, fraction: 0.1),
    .init(id: "Completed", label: "Completed", showCount: 28, fraction: 0.46),
    .init(id: "OnHold", label: "On hold", showCount: 2, fraction: 0.03),
    .init(id: "Dropped", label: "Dropped", showCount: 1, fraction: 0.01),
]

private let previewRatingBreakdown: [SwiftRatingBar] = [
    .init(rating: 10, count: 12, fraction: 1.0),
    .init(rating: 9, count: 8, fraction: 0.66),
    .init(rating: 8, count: 10, fraction: 0.83),
    .init(rating: 7, count: 3, fraction: 0.25),
    .init(rating: 6, count: 1, fraction: 0.08),
]

private let previewContentState = StatisticsScreen.State(
    isLoading: false,
    showContent: true,
    totalWatchTime: SwiftWatchTime(days: 12, hours: 4, minutes: 30),
    tiles: previewTiles,
    mostWatchedShows: previewMostWatchedShows,
    watchStatusBreakdown: previewWatchStatusBreakdown,
    ratingBreakdown: previewRatingBreakdown,
    labels: previewLabels
)

#Preview("Loading") {
    NavigationStack {
        StatisticsScreen(state: StatisticsScreen.State(isLoading: true, labels: previewLabels))
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Empty") {
    NavigationStack {
        StatisticsScreen(
            state: StatisticsScreen.State(isLoading: false, showEmptyState: true, labels: previewLabels)
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Content") {
    NavigationStack {
        StatisticsScreen(state: previewContentState)
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Marked Watched Times") {
    NavigationStack {
        StatisticsScreen(
            state: StatisticsScreen.State(
                isLoading: false,
                showContent: true,
                showsMarkedWatchedTimes: true,
                totalWatchTime: previewContentState.totalWatchTime,
                tiles: previewContentState.tiles,
                mostWatchedShows: previewContentState.mostWatchedShows,
                watchStatusBreakdown: previewContentState.watchStatusBreakdown,
                ratingBreakdown: previewContentState.ratingBreakdown,
                labels: previewLabels
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}

#Preview("Locked") {
    NavigationStack {
        StatisticsScreen(
            state: StatisticsScreen.State(
                isLoading: false,
                isLocked: true,
                showContent: true,
                totalWatchTime: previewContentState.totalWatchTime,
                tiles: previewContentState.tiles,
                mostWatchedShows: previewContentState.mostWatchedShows,
                watchStatusBreakdown: previewContentState.watchStatusBreakdown,
                ratingBreakdown: previewContentState.ratingBreakdown,
                labels: previewLabels
            )
        )
    }
    .appPreview()
    .preferredColorScheme(.dark)
}
