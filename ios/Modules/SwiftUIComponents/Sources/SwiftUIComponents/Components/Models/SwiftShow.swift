import Foundation

public struct SwiftShow: Identifiable {
    public let id: UUID = .init()
    public let traktId: Int64
    public let title: String
    public let posterUrl: String?
    public let backdropUrl: String?
    public let inLibrary: Bool
    public let overview: String?

    public init(
        traktId: Int64,
        title: String,
        posterUrl: String?,
        backdropUrl: String? = nil,
        inLibrary: Bool,
        overview: String? = nil
    ) {
        self.traktId = traktId
        self.title = title
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.inLibrary = inLibrary
        self.overview = overview
    }
}
