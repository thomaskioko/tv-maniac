import TvManiac

extension StatisticsState {
    func toState() -> StatisticsScreen.State {
        StatisticsScreen.State(
            isLoading: isLoading,
            isLocked: isLocked,
            showEmptyState: showEmptyState,
            showContent: showContent,
            showsMarkedWatchedTimes: showsMarkedWatchedTimes,
            totalWatchTime: totalWatchTime?.toSwift(),
            tiles: tiles.map { $0.toSwift() },
            mostWatchedShows: mostWatchedShows.map { $0.toSwift() },
            watchStatusBreakdown: watchStatusBreakdown.map { $0.toSwift() },
            ratingBreakdown: ratingBreakdown.map { $0.toSwift() },
            labels: labels.toSwift()
        )
    }
}

extension WatchTime {
    func toSwift() -> SwiftWatchTime {
        .init(days: days, hours: hours, minutes: minutes)
    }
}

extension StatisticTile {
    func toSwift() -> SwiftStatisticTile {
        .init(id: id.name, label: label, value: value, caption: caption)
    }
}

extension MostWatchedShowItem {
    func toSwift() -> SwiftMostWatchedShowItem {
        .init(showId: showId, title: title, posterPath: posterPath, caption: caption)
    }
}

extension WatchStatusItem {
    func toSwift() -> SwiftWatchStatusItem {
        .init(id: id.name, label: label, showCount: showCount, fraction: fraction)
    }
}

extension RatingBar {
    func toSwift() -> SwiftRatingBar {
        .init(rating: rating, count: count, fraction: fraction)
    }
}

extension StatisticsLabels {
    func toSwift() -> SwiftStatisticsLabels {
        .init(
            screenTitle: screenTitle,
            emptyMessage: emptyMessage,
            watchTimeTitle: watchTimeTitle,
            daysLabel: daysLabel,
            hoursLabel: hoursLabel,
            minutesLabel: minutesLabel,
            markedWatchedNote: markedWatchedNote,
            mostWatchedTitle: mostWatchedTitle,
            watchStatusTitle: watchStatusTitle,
            ratingsTitle: ratingsTitle,
            lockedTitle: lockedTitle,
            lockedMessage: lockedMessage,
            lockedBadgeText: lockedBadgeText,
            lockedActionText: lockedActionText,
            lockedContentDescription: lockedContentDescription
        )
    }
}
