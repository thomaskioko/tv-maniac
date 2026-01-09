import SwiftUI

public struct SwiftContinueTrackingEpisode: Identifiable {
    public var id: Int64 { episodeId }
    public let episodeId: Int64
    public let seasonId: Int64
    public let showId: Int64
    public let episodeNumber: Int64
    public let seasonNumber: Int64
    public let episodeNumberFormatted: String
    public let episodeTitle: String
    public let imageUrl: String?
    public let isWatched: Bool
    public let daysUntilAir: Int64?
    public let hasAired: Bool

    public init(
        episodeId: Int64,
        seasonId: Int64,
        showId: Int64,
        episodeNumber: Int64,
        seasonNumber: Int64,
        episodeNumberFormatted: String,
        episodeTitle: String,
        imageUrl: String?,
        isWatched: Bool,
        daysUntilAir: Int64?,
        hasAired: Bool
    ) {
        self.episodeId = episodeId
        self.seasonId = seasonId
        self.showId = showId
        self.episodeNumber = episodeNumber
        self.seasonNumber = seasonNumber
        self.episodeNumberFormatted = episodeNumberFormatted
        self.episodeTitle = episodeTitle
        self.imageUrl = imageUrl
        self.isWatched = isWatched
        self.daysUntilAir = daysUntilAir
        self.hasAired = hasAired
    }
}
