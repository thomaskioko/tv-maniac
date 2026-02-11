import SwiftUI

public enum SwiftEpisodeBadge {
    case premiere
    case new
    case none
}

public struct SwiftNextEpisode: Identifiable {
    public let id: UUID = .init()
    public let showTraktId: Int64
    public let showName: String
    public let showPoster: String?
    public let episodeId: Int64
    public let episodeTitle: String
    public let episodeNumber: String
    public let seasonId: Int64
    public let seasonNumber: Int64
    public let episodeNumberValue: Int64
    public let runtime: String?
    public let stillImage: String?
    public let overview: String
    public let badge: SwiftEpisodeBadge
    public let remainingEpisodes: Int32
    public let watchedCount: Int64
    public let totalCount: Int64

    public init(
        showTraktId: Int64,
        showName: String,
        showPoster: String?,
        episodeId: Int64,
        episodeTitle: String,
        episodeNumber: String,
        seasonId: Int64 = 0,
        seasonNumber: Int64 = 0,
        episodeNumberValue: Int64 = 0,
        runtime: String?,
        stillImage: String?,
        overview: String,
        badge: SwiftEpisodeBadge = .none,
        remainingEpisodes: Int32 = 0,
        watchedCount: Int64 = 0,
        totalCount: Int64 = 0
    ) {
        self.showTraktId = showTraktId
        self.showName = showName
        self.showPoster = showPoster
        self.episodeId = episodeId
        self.episodeTitle = episodeTitle
        self.episodeNumber = episodeNumber
        self.seasonId = seasonId
        self.seasonNumber = seasonNumber
        self.episodeNumberValue = episodeNumberValue
        self.runtime = runtime
        self.stillImage = stillImage
        self.overview = overview
        self.badge = badge
        self.remainingEpisodes = remainingEpisodes
        self.watchedCount = watchedCount
        self.totalCount = totalCount
    }
}
