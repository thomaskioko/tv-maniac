import Components
import Foundation
import Models

public struct SwiftSearchShow: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let showId: Int64
    public let title: String
    public let overview: String?
    public let status: String?
    public let imageUrl: String?
    public let year: String?
    public let voteAverage: Double?
    public let inLibrary: Bool

    public init(
        tmdbId: Int64,
        showId: Int64,
        title: String,
        overview: String?,
        status: String?,
        imageUrl: String?,
        year: String?,
        voteAverage: Double?,
        inLibrary: Bool = false
    ) {
        self.tmdbId = tmdbId
        self.showId = showId
        self.title = title
        self.overview = overview
        self.status = status
        self.imageUrl = imageUrl
        self.year = year
        self.voteAverage = voteAverage
        self.inLibrary = inLibrary
    }
}
