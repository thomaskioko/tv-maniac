import Foundation

public struct SwiftWatchTime: Equatable {
    public let days: Int64
    public let hours: Int64
    public let minutes: Int64

    public init(days: Int64, hours: Int64, minutes: Int64) {
        self.days = days
        self.hours = hours
        self.minutes = minutes
    }
}

public struct SwiftStatisticTile: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let value: String
    public let caption: String

    public init(id: String, label: String, value: String, caption: String) {
        self.id = id
        self.label = label
        self.value = value
        self.caption = caption
    }
}

public struct SwiftMostWatchedShowItem: Identifiable, Equatable {
    public let showId: Int64
    public let title: String
    public let posterPath: String?
    public let caption: String

    public var id: Int64 {
        showId
    }

    public init(showId: Int64, title: String, posterPath: String?, caption: String) {
        self.showId = showId
        self.title = title
        self.posterPath = posterPath
        self.caption = caption
    }
}

public struct SwiftWatchStatusItem: Identifiable, Equatable {
    public let id: String
    public let label: String
    public let showCount: Int64
    public let fraction: Float

    public init(id: String, label: String, showCount: Int64, fraction: Float) {
        self.id = id
        self.label = label
        self.showCount = showCount
        self.fraction = fraction
    }
}

public struct SwiftRatingBar: Identifiable, Equatable {
    public let rating: Int32
    public let count: Int64
    public let fraction: Float

    public var id: Int32 {
        rating
    }

    public init(rating: Int32, count: Int64, fraction: Float) {
        self.rating = rating
        self.count = count
        self.fraction = fraction
    }
}

public struct SwiftStatisticsLabels: Equatable {
    public let screenTitle: String
    public let emptyMessage: String
    public let watchTimeTitle: String
    public let daysLabel: String
    public let hoursLabel: String
    public let minutesLabel: String
    public let markedWatchedNote: String
    public let mostWatchedTitle: String
    public let watchStatusTitle: String
    public let ratingsTitle: String
    public let lockedTitle: String
    public let lockedMessage: String
    public let lockedBadgeText: String
    public let lockedActionText: String
    public let lockedContentDescription: String

    public init(
        screenTitle: String = "",
        emptyMessage: String = "",
        watchTimeTitle: String = "",
        daysLabel: String = "",
        hoursLabel: String = "",
        minutesLabel: String = "",
        markedWatchedNote: String = "",
        mostWatchedTitle: String = "",
        watchStatusTitle: String = "",
        ratingsTitle: String = "",
        lockedTitle: String = "",
        lockedMessage: String = "",
        lockedBadgeText: String = "",
        lockedActionText: String = "",
        lockedContentDescription: String = ""
    ) {
        self.screenTitle = screenTitle
        self.emptyMessage = emptyMessage
        self.watchTimeTitle = watchTimeTitle
        self.daysLabel = daysLabel
        self.hoursLabel = hoursLabel
        self.minutesLabel = minutesLabel
        self.markedWatchedNote = markedWatchedNote
        self.mostWatchedTitle = mostWatchedTitle
        self.watchStatusTitle = watchStatusTitle
        self.ratingsTitle = ratingsTitle
        self.lockedTitle = lockedTitle
        self.lockedMessage = lockedMessage
        self.lockedBadgeText = lockedBadgeText
        self.lockedActionText = lockedActionText
        self.lockedContentDescription = lockedContentDescription
    }
}
