import Components
import Foundation
import Models

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
    public let showsWatched: String
    public let episodesWatched: String
    public let months: Int32
    public let days: Int32
    public let hours: Int32
    public let listCount: Int32

    public init(
        showsWatched: String,
        episodesWatched: String,
        months: Int32,
        days: Int32,
        hours: Int32,
        listCount: Int32
    ) {
        self.showsWatched = showsWatched
        self.episodesWatched = episodesWatched
        self.months = months
        self.days = days
        self.hours = hours
        self.listCount = listCount
    }
}

public struct SwiftProfileList: Identifiable, Equatable {
    public let id: Int64
    public let name: String
    public let itemCountLabel: String
    public let posterUrls: [String]

    public init(
        id: Int64,
        name: String,
        itemCountLabel: String,
        posterUrls: [String]
    ) {
        self.id = id
        self.name = name
        self.itemCountLabel = itemCountLabel
        self.posterUrls = posterUrls
    }
}

public struct SwiftProfileShow: Identifiable, Equatable {
    public let id: Int64
    public let title: String
    public let posterUrl: String?

    public init(
        id: Int64,
        title: String,
        posterUrl: String?
    ) {
        self.id = id
        self.title = title
        self.posterUrl = posterUrl
    }
}

public struct SwiftProfileRecentShow: Identifiable, Equatable {
    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let episodeLabel: String

    public var id: String {
        "\(traktId)-\(episodeLabel)"
    }

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        episodeLabel: String
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.episodeLabel = episodeLabel
    }
}

public enum SwiftSectionState<Item: Equatable>: Equatable {
    case loading
    case empty
    case content([Item])
    case error(String)
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
