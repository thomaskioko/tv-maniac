import Foundation

public struct SwiftProfileInfo: Equatable {
    public let username: String
    public let fullName: String?
    public let avatarUrl: String?
    public let backgroundUrl: String?
    public let stats: SwiftProfileStats

    public init(
        username: String,
        fullName: String?,
        avatarUrl: String?,
        backgroundUrl: String?,
        stats: SwiftProfileStats
    ) {
        self.username = username
        self.fullName = fullName
        self.avatarUrl = avatarUrl
        self.backgroundUrl = backgroundUrl
        self.stats = stats
    }
}

public struct SwiftProfileStats: Equatable {
    public let months: Int32
    public let days: Int32
    public let hours: Int32
    public let episodesWatched: Int32

    public init(months: Int32, days: Int32, hours: Int32, episodesWatched: Int32) {
        self.months = months
        self.days = days
        self.hours = hours
        self.episodesWatched = episodesWatched
    }
}

public struct SwiftFeatureItem: Identifiable, Equatable {
    public let id: String
    public let iconName: String
    public let title: String
    public let description: String

    public init(id: String, iconName: String, title: String, description: String) {
        self.id = id
        self.iconName = iconName
        self.title = title
        self.description = description
    }
}
