import Foundation

public struct SwiftStartWatchingItem: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let year: String?
    public let episodeId: Int64?
    public let episodeTitle: String?
    public let episodeNumber: String?
    public let seasonNumber: Int64?
    public let episodeNumberValue: Int64?
    public let runtime: String?
    public let stillUrl: String?

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        year: String?,
        episodeId: Int64? = nil,
        episodeTitle: String? = nil,
        episodeNumber: String? = nil,
        seasonNumber: Int64? = nil,
        episodeNumberValue: Int64? = nil,
        runtime: String? = nil,
        stillUrl: String? = nil
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.year = year
        self.episodeId = episodeId
        self.episodeTitle = episodeTitle
        self.episodeNumber = episodeNumber
        self.seasonNumber = seasonNumber
        self.episodeNumberValue = episodeNumberValue
        self.runtime = runtime
        self.stillUrl = stillUrl
    }

    public var hasEpisode: Bool {
        episodeId != nil
    }
}
