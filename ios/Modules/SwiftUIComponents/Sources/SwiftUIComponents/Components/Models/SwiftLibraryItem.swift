import Foundation

public struct SwiftLibraryItem: Identifiable, Equatable {
    public var id: Int64 { traktId }
    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let year: String?
    public let status: String?
    public let seasonCount: Int64
    public let episodeCount: Int64
    public let rating: Double?
    public let genres: [String]?
    public let watchProviders: [SwiftProviders]

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        year: String?,
        status: String?,
        seasonCount: Int64,
        episodeCount: Int64,
        rating: Double?,
        genres: [String]?,
        watchProviders: [SwiftProviders]
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.year = year
        self.status = status
        self.seasonCount = seasonCount
        self.episodeCount = episodeCount
        self.rating = rating
        self.genres = genres
        self.watchProviders = watchProviders
    }
}
