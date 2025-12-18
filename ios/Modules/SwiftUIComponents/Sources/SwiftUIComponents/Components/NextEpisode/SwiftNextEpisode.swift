import SwiftUI

public struct SwiftNextEpisode: Identifiable {
    public let id: UUID = .init()
    public let showId: Int64
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
    public let isNew: Bool

    public init(
        showId: Int64,
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
        isNew: Bool
    ) {
        self.showId = showId
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
        self.isNew = isNew
    }
}
