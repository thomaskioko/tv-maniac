import Foundation

public struct SwiftStartWatchingItem: Identifiable, Equatable {
    public var id: Int64 {
        traktId
    }

    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let year: String?

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        year: String?
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.year = year
    }
}
