import Foundation

public struct SwiftSearchShow: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let traktId: Int64
    public let title: String
    public let overview: String?
    public let status: String?
    public let imageUrl: String?
    public let year: String?
    public let voteAverage: Double?

    public init(
        tmdbId: Int64,
        traktId: Int64,
        title: String,
        overview: String?,
        status: String?,
        imageUrl: String?,
        year: String?,
        voteAverage: Double?
    ) {
        self.tmdbId = tmdbId
        self.traktId = traktId
        self.title = title
        self.overview = overview
        self.status = status
        self.imageUrl = imageUrl
        self.year = year
        self.voteAverage = voteAverage
    }
}
