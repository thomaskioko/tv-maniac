import Foundation

public struct SwiftEpisode: Identifiable {
    public let id: UUID = .init()
    public let episodeId: Int64
    public let title: String
    public let overview: String
    public let imageUrl: String?
    public let seasonNumber: Int64
    public let episodeNumber: Int64
    public let isWatched: Bool
    public let isEpisodeUpdating: Bool
    public let daysUntilAir: Int64?
    public let hasPreviousUnwatched: Bool
    public let hasAired: Bool

    public init(
        episodeId: Int64,
        title: String,
        overview: String,
        imageUrl: String?,
        seasonNumber: Int64 = 0,
        episodeNumber: Int64 = 0,
        isWatched: Bool = false,
        isEpisodeUpdating: Bool = false,
        daysUntilAir: Int64? = nil,
        hasPreviousUnwatched: Bool = false,
        hasAired: Bool = true
    ) {
        self.episodeId = episodeId
        self.imageUrl = imageUrl
        self.title = title
        self.overview = overview
        self.seasonNumber = seasonNumber
        self.episodeNumber = episodeNumber
        self.isWatched = isWatched
        self.isEpisodeUpdating = isEpisodeUpdating
        self.daysUntilAir = daysUntilAir
        self.hasPreviousUnwatched = hasPreviousUnwatched
        self.hasAired = hasAired
    }
}
