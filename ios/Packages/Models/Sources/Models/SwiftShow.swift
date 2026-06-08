import Foundation

public struct SwiftShow: Identifiable, Equatable {
    public var id: Int64 {
        showId
    }

    public let showId: Int64
    public let title: String
    public let posterUrl: String?
    public let backdropUrl: String?
    public let inLibrary: Bool
    public let overview: String?

    public init(
        showId: Int64,
        title: String,
        posterUrl: String?,
        backdropUrl: String? = nil,
        inLibrary: Bool,
        overview: String? = nil
    ) {
        self.showId = showId
        self.title = title
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.inLibrary = inLibrary
        self.overview = overview
    }
}
