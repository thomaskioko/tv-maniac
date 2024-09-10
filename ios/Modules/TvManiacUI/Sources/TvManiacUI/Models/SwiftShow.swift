import Foundation

public struct SwiftShow: Identifiable {
    public let id: UUID = .init()
    public let tmdbId: Int64
    public let title: String
    public let posterUrl: String?
    public let backdropUrl: String?
    public let inLibrary: Bool

    public init(
        tmdbId: Int64,
        title: String,
        posterUrl: String?,
        backdropUrl: String? = nil,
        inLibrary: Bool
    ) {
        self.tmdbId = tmdbId
        self.title = title
        self.posterUrl = posterUrl
        self.backdropUrl = backdropUrl
        self.inLibrary = inLibrary
    }
}
